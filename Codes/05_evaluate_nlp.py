import argparse
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples,
                    logicds_sample_dir, prompts_sample_dir,
                    default_patches_dir,
                    evaluation_root, evaluation_sample_dir)
from _locked_merge import merge_into_eval_json
from metrics import discover_metrics


def extract_repair_or_whole(text: str) -> str:
    m = re.search(r"<repair>(.*?)</repair>", text, re.DOTALL)
    return m.group(1).strip() if m else text


def walk_sample_patches(sample_patch_dir: str):
    for root, _, files in os.walk(sample_patch_dir):
        for fname in files:
            if fname.endswith('.txt') and not fname.endswith('_reasoning.txt'):
                full = os.path.join(root, fname)
                yield full, os.path.relpath(full, sample_patch_dir)


def eval_path_for(kind: str, sample_name: str, rel: str) -> str:
    stem = os.path.splitext(rel)[0]
    return os.path.join(evaluation_root(), f'{kind}_samples', sample_name, stem + '_evaluation.json')


def infer_llm_pid(rel: str):
    parts = rel.split(os.sep)
    if len(parts) >= 3 and parts[0] == 'llms' and parts[-1].startswith('patch_P'):
        llm = parts[1]
        m = re.match(r'patch_(P\d+)\.txt$', parts[-1])
        if m and llm in ('llama', 'qwen', 'openai'):
            return llm, m.group(1)
    return None, None


def resolve_reference(kind: str, sample_name: str, code_type: str) -> str:
    ref_file = 'fixed_function.txt' if code_type == 'function' else 'fixed_block.txt'
    ref_path = os.path.join(logicds_sample_dir(kind, sample_name), ref_file)
    if not os.path.exists(ref_path):
        return ''
    with open(ref_path) as f:
        return f.read()


def resolve_code_type(kind: str, sample_name: str, pid: str, default: str) -> str:
    meta_path = os.path.join(prompts_sample_dir(kind, sample_name), 'meta_info.json')
    if not os.path.exists(meta_path):
        return default
    try:
        with open(meta_path) as f:
            meta = json.load(f)
    except Exception:
        return default
    return meta.get(pid, {}).get('buggy_code_type', default)


def resolve_candidate_reasoning(patch_path: str) -> str:
    base, _ = os.path.splitext(patch_path)
    path = base + '_reasoning.txt'
    if os.path.exists(path):
        with open(path) as f:
            return f.read()
    return ''


def ensure_gt_reasoning(kind: str, sample_name: str, judge: str) -> str:
    cache_dir = evaluation_sample_dir(kind, sample_name)
    os.makedirs(cache_dir, exist_ok=True)
    cache = os.path.join(cache_dir, f'gt_reasoning_{judge}.txt')
    if os.path.exists(cache) and os.path.getsize(cache) > 0:
        with open(cache) as f:
            return f.read()

    sdir = logicds_sample_dir(kind, sample_name)
    def _read(p):
        return open(p).read() if os.path.exists(p) else ''
    vd = _read(os.path.join(sdir, 'vulnerability_description.txt'))
    bb = _read(os.path.join(sdir, 'buggy_block.txt'))
    fb = _read(os.path.join(sdir, 'fixed_block.txt'))
    if not (vd and bb and fb):
        return ''

    try:
        if judge == 'openai':
            from interact_with_openai import generate_gt_reasoning_openai as gen
        elif judge == 'llama':
            from interact_with_llama import generate_gt_reasoning_llama as gen
        elif judge == 'qwen':
            from interact_with_qwen import generate_gt_reasoning_qwen as gen
        else:
            return ''
    except ImportError as e:
        print(f"  [WARN] ensure_gt_reasoning {judge}: {e}")
        return ''
    gen(vd, bb, fb, cache)
    if os.path.exists(cache):
        with open(cache) as f:
            return f.read()
    return ''


JUDGE_METRICS = {'judge_llama', 'judge_qwen', 'judge_openai', 'reasoning_cosine_similarity'}


def process_sample(kind: str, sample_name: str, patches_root: str,
                   metric_modules: dict, overwrite: bool,
                   default_code_type: str) -> int:
    sample_patch_dir = os.path.join(patches_root, f'{kind}_samples', sample_name)
    if not os.path.isdir(sample_patch_dir):
        return 0

    gt_reasoning_by_judge = {}
    for m_name in metric_modules:
        if m_name.startswith('judge_'):
            judge = m_name.replace('judge_', '')
            gt_reasoning_by_judge[judge] = ensure_gt_reasoning(kind, sample_name, judge)
        if m_name == 'reasoning_cosine_similarity':
            if 'openai' not in gt_reasoning_by_judge:
                gt_reasoning_by_judge['openai'] = ensure_gt_reasoning(kind, sample_name, 'openai')

    count = 0
    for patch_path, rel in walk_sample_patches(sample_patch_dir):
        eval_path = eval_path_for(kind, sample_name, rel)
        with open(patch_path) as f:
            candidate_raw = f.read()
        candidate = extract_repair_or_whole(candidate_raw)

        llm, pid = infer_llm_pid(rel)
        code_type = (resolve_code_type(kind, sample_name, pid, default_code_type)
                     if pid else default_code_type)
        reference = resolve_reference(kind, sample_name, code_type)

        candidate_reasoning = resolve_candidate_reasoning(patch_path)

        existing = {}
        if os.path.exists(eval_path):
            try:
                with open(eval_path) as f:
                    existing = json.load(f)
            except Exception:
                existing = {}

        new_fields = {}
        for name, mod in metric_modules.items():
            if name.startswith('judge_'):
                judge = name.replace('judge_', '')
                ctx = {'sample_dir': logicds_sample_dir(kind, sample_name),
                       'kind': kind, 'sample': sample_name,
                       'patch_path': patch_path, 'rel': rel,
                       'llm': llm, 'pid': pid, 'code_type': code_type,
                       'gt_reasoning': gt_reasoning_by_judge.get(judge, ''),
                       'candidate_reasoning': candidate_reasoning}
                field_key = name.replace('judge_', 'reasoning_similar_judge_')
            elif name == 'reasoning_cosine_similarity':
                ctx = {'sample_dir': logicds_sample_dir(kind, sample_name),
                       'kind': kind, 'sample': sample_name,
                       'patch_path': patch_path, 'rel': rel,
                       'llm': llm, 'pid': pid, 'code_type': code_type,
                       'gt_reasoning': gt_reasoning_by_judge.get('openai', ''),
                       'candidate_reasoning': candidate_reasoning}
                field_key = 'reasoning_cosine_similarity'
            else:
                ctx = {'sample_dir': logicds_sample_dir(kind, sample_name),
                       'kind': kind, 'sample': sample_name,
                       'patch_path': patch_path, 'rel': rel,
                       'llm': llm, 'pid': pid, 'code_type': code_type}
                field_key = name

            if not overwrite and any(k.startswith(field_key) for k in existing):
                continue

            try:
                new_fields.update(mod.compute(reference, candidate, ctx))
            except Exception as e:
                print(f"  [WARN] {rel}/{name}: {e}")
        if new_fields:
            merge_into_eval_json(eval_path, new_fields)
            count += 1
    return count


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--patches-dir', type=str, default=default_patches_dir())
    parser.add_argument('--metrics', type=str, default='rouge_l,patch_cosine_similarity',
                        help='Comma-separated metric names, or "all". Default: rouge_l,patch_cosine_similarity')
    parser.add_argument('--sample-filter', type=str, default=None)
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true')
    parser.add_argument('--code-type', choices=['block', 'function'], default='block',
                        help='Fallback code_type when meta_info can\'t resolve P<n> (default: block)')
    args = parser.parse_args()

    ensure_logiceval_cwd()

    discovered = discover_metrics()
    if args.metrics == 'all':
        metric_modules = discovered
    else:
        wanted = [m.strip() for m in args.metrics.split(',')]
        metric_modules = {n: discovered[n] for n in wanted if n in discovered}
        missing = [n for n in wanted if n not in discovered]
        if missing:
            print(f"[WARN] unknown metrics: {missing}. Available: {sorted(discovered)}")

    if not metric_modules:
        print("[ERROR] no metrics to run")
        sys.exit(1)

    print(f"Running metrics: {sorted(metric_modules)}")
    print(f"Patches root: {args.patches_dir}")

    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None
    total = 0
    for kind, name, _ in iter_samples(args.kind, sample_filter):
        total += process_sample(kind, name, args.patches_dir, metric_modules, args.overwrite, args.code_type)
    print(f"\nEvaluated {total} patch files")


if __name__ == '__main__':
    main()
