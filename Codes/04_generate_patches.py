import argparse
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples, PAPER_PROMPT_IDS,
                    OPENAI_NA_PROMPTS, logicds_sample_dir,
                    responses_sample_dir, prompts_sample_dir, default_patches_dir)


def extract_repair(text: str):
    m = re.search(r"<repair>(.*?)</repair>", text, re.DOTALL)
    return m.group(1) if m else None


def graft(original_text: str, patch: str, start_line: int, end_line: int) -> str:
    lines = original_text.splitlines()
    return "\n".join(lines[:start_line - 1] + [patch] + lines[end_line:])


def process_one(kind: str, sample_name: str, llm: str, overwrite: bool,
                patches_root: str, default_code_type: str) -> int:
    sample_dir = logicds_sample_dir(kind, sample_name)
    resp_dir = responses_sample_dir(llm, kind, sample_name)
    prompts_dir = prompts_sample_dir(kind, sample_name)
    cfg_path = os.path.join(sample_dir, 'inputConfig.json')
    fixed_src_path = os.path.join(sample_dir, 'fixed_source_code_file.txt')

    if not os.path.isdir(resp_dir):
        return 0
    if not (os.path.exists(cfg_path) and os.path.exists(fixed_src_path)):
        print(f"[SKIP] {sample_name}: missing inputConfig.json or fixed_source_code_file.txt")
        return 0

    with open(cfg_path) as f:
        cfg = json.load(f)
    with open(fixed_src_path) as f:
        original = f.read()

    meta_path = os.path.join(prompts_dir, 'meta_info.json')
    meta = {}
    if os.path.exists(meta_path):
        with open(meta_path) as f:
            meta = json.load(f)

    out_dir = os.path.join(patches_root, f'{kind}_samples', sample_name, 'llms', llm)
    os.makedirs(out_dir, exist_ok=True)

    n_ok = 0
    for pid in PAPER_PROMPT_IDS:
        if llm == 'openai' and pid in OPENAI_NA_PROMPTS:
            continue
        resp_path = os.path.join(resp_dir, f'prompt_{pid}.txt')
        if not os.path.exists(resp_path):
            continue
        out_path = os.path.join(out_dir, f'patch_{pid}.txt')
        out_reasoning_path = os.path.join(out_dir, f'patch_{pid}_reasoning.txt')
        resp_reasoning_path = os.path.join(resp_dir, f'prompt_{pid}_reasoning.txt')
        if not overwrite and os.path.exists(out_path):
            continue

        try:
            with open(resp_path) as f:
                response = f.read()
            patch = extract_repair(response)
            if patch is None:
                raise ValueError("<repair> tag not found")

            code_type = meta.get(pid, {}).get('buggy_code_type', default_code_type)
            if code_type == 'function':
                start = cfg['vul_code_func_fixed_start_line']
                end = cfg['vul_code_func_fixed_end_line']
            else:
                start = cfg['vul_code_block_fixed_start_line']
                end = cfg['vul_code_block_fixed_end_line']

            grafted = graft(original, patch, int(start), int(end))
            with open(out_path, 'w') as f:
                f.write(grafted)
            if os.path.exists(resp_reasoning_path):
                with open(resp_reasoning_path) as f:
                    reasoning_text = f.read()
                with open(out_reasoning_path, 'w') as f:
                    f.write(reasoning_text)
            n_ok += 1
        except Exception as e:
            print(f"  [ERROR] {sample_name}/{pid}: {e}")
            with open(out_path, 'w') as f:
                f.write(f"Grafting Error: {e}\n")
    return n_ok


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--llm', choices=['llama', 'qwen', 'openai', 'all'], required=True)
    parser.add_argument('--patches-dir', type=str, default=default_patches_dir())
    parser.add_argument('--sample-filter', type=str, default=None)
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true')
    parser.add_argument('--code-type', choices=['block', 'function'], default='block',
                        help='Fallback buggy_code_type if meta_info.json lacks this P<n> (default: block)')
    args = parser.parse_args()

    ensure_logiceval_cwd()
    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None
    llms = ['llama', 'qwen', 'openai'] if args.llm == 'all' else [args.llm]

    total = 0
    for llm in llms:
        for kind, name, _ in iter_samples(args.kind, sample_filter):
            total += process_one(kind, name, llm, args.overwrite, args.patches_dir, args.code_type)
    print(f"\nGrafted {total} patches total")


if __name__ == '__main__':
    main()
