import argparse
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples, PAPER_PROMPT_IDS,
                    COT_PROMPTS, prompts_sample_dir, logicds_sample_dir)
import token_count


def _read(path: str) -> str:
    if os.path.exists(path):
        with open(path) as f:
            return f.read()
    return ''


def load_sample_info(sample_dir: str) -> dict:
    cfg_path = os.path.join(sample_dir, 'inputConfig.json')
    with open(cfg_path) as f:
        cfg = json.load(f)
    return {
        'config': cfg,
        'vulnerability_description': _read(os.path.join(sample_dir, 'vulnerability_description.txt')),
        'specification': _read(os.path.join(sample_dir, 'specification.txt')),
        'buggy_function': _read(os.path.join(sample_dir, 'buggy_function.txt')),
        'buggy_block': _read(os.path.join(sample_dir, 'buggy_block.txt')),
        'fixed_block': _read(os.path.join(sample_dir, 'fixed_block.txt')),
        'context_code': _read(os.path.join(sample_dir, 'context_codes.txt')),
        'repair_description': _read(os.path.join(sample_dir, 'repair_description.txt')),
        'initial_block': _read(os.path.join(sample_dir, 'initial_block.txt')),
    }


def _apply_token_limit(lines, suffix, max_tokens):
    joined = "\n".join(lines)
    full = f"{joined}\n{suffix}"
    while token_count.count_tokens(full) > max_tokens and lines:
        lines.pop()
        joined = "\n".join(lines)
        full = f"{joined}\n{suffix}"
    return lines


def _base_lines(func_start, block_start, buggy_function_lines):
    return [line for i, line in enumerate(buggy_function_lines)
            if i < (block_start - func_start)]


def _commented_nh(info, max_tokens=4000):
    cfg = info['config']
    lines = _base_lines(cfg['vul_code_func_start_line'], cfg['vul_code_block_start_line'],
                        info['buggy_function'].split("\n"))
    return "\n".join(_apply_token_limit(lines[:], "", max_tokens))


def _commented_s1(info, max_tokens=4000):
    cfg = info['config']
    lines = _base_lines(cfg['vul_code_func_start_line'], cfg['vul_code_block_start_line'],
                        info['buggy_function'].split("\n"))
    suffix = f"// bugfix: fixed {cfg.get('vul_description','')}"
    trimmed = _apply_token_limit(lines[:], suffix, max_tokens)
    trimmed.append(suffix)
    return "\n".join(trimmed)


def _commented_s2(info, max_tokens=4000):
    cfg = info['config']
    lines = _base_lines(cfg['vul_code_func_start_line'], cfg['vul_code_block_start_line'],
                        info['buggy_function'].split("\n"))
    suffix = f"// fixed {cfg.get('vul_description','')} bug"
    trimmed = _apply_token_limit(lines[:], suffix, max_tokens)
    trimmed.append(suffix)
    return "\n".join(trimmed)


def _commented_c(info, max_tokens=4000):
    cfg = info['config']
    fs = cfg['vul_code_func_start_line']
    bs = cfg['vul_code_block_start_line']
    be = cfg['vul_code_block_end_line']
    buggy_lines = info['buggy_function'].split("\n")
    pre, suffix = [], []
    for i, line in enumerate(buggy_lines):
        if i < (bs - fs):
            pre.append(line)
        elif (bs - fs) <= i <= (be - fs + 1):
            if i == (bs - fs):
                suffix.append(f"// BUG: {cfg.get('vul_description','')}")
            suffix.append(f"// {line}")
    suffix.append("// FIXED:")
    suffix.extend(info['initial_block'].split("\n"))
    trimmed = _apply_token_limit(pre[:], "\n".join(suffix), max_tokens)
    return "\n".join(trimmed + suffix)


def _commented_cm(info, max_tokens=4000):
    cfg = info['config']
    fs = cfg['vul_code_func_start_line']
    bs = cfg['vul_code_block_start_line']
    be = cfg['vul_code_block_end_line']
    buggy_lines = info['buggy_function'].split("\n")
    pre, suffix = [], []
    for i, line in enumerate(buggy_lines):
        if i < (bs - fs):
            pre.append(line)
        elif (bs - fs) <= i <= (be - fs + 1):
            if i == (bs - fs):
                suffix.append(f"// BUG: {cfg.get('vul_description','')}")
                temp = info['vulnerability_description'].replace("\n", " ")
                suffix.append(f"// MESSAGE: {temp}")
            suffix.append(f"// {line}")
    suffix.append("// FIXED VERSION:")
    suffix.extend(info['initial_block'].split("\n"))
    trimmed = _apply_token_limit(pre[:], "\n".join(suffix), max_tokens)
    return "\n".join(trimmed + suffix)


P17_INSTRUCTION = (
    "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
    "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
    "Your code should be until the end of the function.\n"
)


def _p1_base(info, include_repair_desc=False):
    p = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{info['vulnerability_description']}\n</vulnerability_description>\n"
        "Here is the buggy source code:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    )
    if include_repair_desc and info['repair_description']:
        p += ("Here is a repair description to fix the buggy source code\n"
              f"<repair_description>\n{info['repair_description']}\n</repair_description>\n")
    p += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    return p


def build_P1(info, fewshot_info=None):
    return _p1_base(info, include_repair_desc=False), {'buggy_code_type': 'block'}


def build_P2(info, fewshot_info=None):
    return _p1_base(info, include_repair_desc=False), {'buggy_code_type': 'block'}


def build_P3(info, fewshot_info=None):
    return _p1_base(info, include_repair_desc=False), {'buggy_code_type': 'block'}


def build_P4(info, fewshot_info=None):
    return _p1_base(info, include_repair_desc=False), {'buggy_code_type': 'block'}


def build_P5(info, fewshot_info=None):
    return build_P1(info, fewshot_info)


def _fewshot_prompt(info, example_info, include_repair_desc):
    p = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "As an example, here is a vulnerability description of a buggy source code:\n"
        f"<example_vulnerability_description>\n{example_info['vulnerability_description']}\n</example_vulnerability_description>\n"
        "Here is the buggy source code of the example:\n"
        f"<example_buggy_code>\n{example_info['buggy_block']}\n</example_buggy_code>\n"
        "Here is the fixed code of the example:\n"
        f"<example_answer>\n<repair>\n{example_info['fixed_block']}\n</repair>\n</example_answer>\n"
        "Here is the vulnerability description of the buggy source code we want to fix:\n"
        f"<vulnerability_description>\n{info['vulnerability_description']}\n</vulnerability_description>\n"
        "Here is the buggy source code we want to fix:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    )
    if include_repair_desc and info['repair_description']:
        p += ("Here is a repair description to fix the buggy source code we want to fix\n"
              f"<repair_description>\n{info['repair_description']}\n</repair_description>\n")
    p += "Provide a repair for the mentioned code snippet to fix the vulnerability we want to fix.\n"
    return p


def build_P6(info, fewshot_info=None):
    if fewshot_info is None:
        return None, None
    return _fewshot_prompt(info, fewshot_info, include_repair_desc=False), {'buggy_code_type': 'block'}


def build_P7(info, fewshot_info=None):
    repair_part = (f"Here is a repair description to fix the buggy source code\n"
                   f"<repair_description>\n{info['repair_description']}\n</repair_description>\n"
                   if info['repair_description'] else "")
    t1 = (
        "Q: You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{info['vulnerability_description']}\n</vulnerability_description>\n"
        "Here is the buggy source code:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
        f"{repair_part}"
        "A: Let's think step by step.\n"
    )
    t2 = (
        "Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the buggy source code:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
        "The following are the code changes you suggested:\n<CODE_CHANGE>\n"
        "Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n"
    )
    return [t1, t2], {'buggy_code_type': 'block'}


def build_P8(info, fewshot_info=None):
    t1 = (
        "You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{info['vulnerability_description']}\n</vulnerability_description>\n"
        "Here is the buggy source code:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    )
    t2 = (
        "Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the buggy source code:\n"
        f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
        "Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n"
    )
    return [t1, t2], {'buggy_code_type': 'block'}


def _with_context(code, vd, context_code, include_repair_desc, repair_desc, max_tokens=4000):
    base = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{vd}\n</vulnerability_description>\n"
        "Source code:\n"
        f"Context code:\n<context_code>\n{context_code}\n</context_code>\n"
        f"Buggy source code:\n\n<buggy_code>\n{code}\n</buggy_code>\n"
    )
    if include_repair_desc and repair_desc:
        base += ("Here is a repair description to fix the buggy source code\n"
                 f"<repair_description>\n{repair_desc}\n</repair_description>\n")
    base += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    return base if token_count.count_tokens(base) <= max_tokens else None


def _no_context(code, vd, include_repair_desc, repair_desc, max_tokens=4000):
    base = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{vd}\n</vulnerability_description>\n"
        f"Here is the buggy source code:\n<buggy_code>\n{code}\n</buggy_code>\n"
    )
    if include_repair_desc and repair_desc:
        base += ("Here is a repair description to fix the buggy source code\n"
                 f"<repair_description>\n{repair_desc}\n</repair_description>\n")
    base += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    return base if token_count.count_tokens(base) <= max_tokens else None


def build_P9(info, fewshot_info=None):
    return build_P1(info, fewshot_info)


def build_P10(info, fewshot_info=None):
    p = _no_context(info['buggy_function'], info['vulnerability_description'], False, info['repair_description'])
    if p is None:
        return None, None
    return p, {'buggy_code_type': 'function'}


def build_P11(info, fewshot_info=None):
    if not info['context_code']:
        return None, None
    p = _with_context(info['buggy_block'], info['vulnerability_description'], info['context_code'],
                     False, info['repair_description'])
    if p is None:
        return None, None
    return p, {'buggy_code_type': 'block'}


def build_P12(info, fewshot_info=None):
    bug_code = f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    p = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        f"Here is the buggy source code:\n{bug_code}"
        "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    )
    return p, {'buggy_code_type': 'block'}


def build_P13(info, fewshot_info=None):
    return build_P1(info, fewshot_info)


def build_P14(info, fewshot_info=None):
    if not info['specification']:
        return None, None
    bug_code = f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    p = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        "Here is the vulnerability description of the buggy source code:\n"
        f"<vulnerability_description>\n{info['vulnerability_description']}\n</vulnerability_description>\n"
        "Here is a description of specification related to the vulnerability:\n"
        f"<specification>\n{info['specification']}\n</specification>\n"
        f"Here is the buggy source code:\n{bug_code}"
        "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    )
    return p, {'buggy_code_type': 'block'}


def build_P15(info, fewshot_info=None):
    return _p1_base(info, include_repair_desc=True), {'buggy_code_type': 'block'}


def build_P16(info, fewshot_info=None):
    if not info['repair_description']:
        return None, None
    bug_code = f"<buggy_code>\n{info['buggy_block']}\n</buggy_code>\n"
    p = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
        f"Here is the buggy source code:\n{bug_code}"
        "Here is a description to repair the buggy source code\n"
        f"<repair_description>\n{info['repair_description']}\n</repair_description>\n"
        "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
    )
    return p, {'buggy_code_type': 'block'}


def _build_pearce(info, variant_fn, meta):
    try:
        body = variant_fn(info)
    except Exception:
        return None, None
    return P17_INSTRUCTION + body, meta


def build_P17(info, fewshot_info=None):
    return _build_pearce(info, _commented_nh, {'buggy_code_type': 'function'})


def build_P18(info, fewshot_info=None):
    return _build_pearce(info, _commented_s1, {'buggy_code_type': 'function'})


def build_P19(info, fewshot_info=None):
    return _build_pearce(info, _commented_s2, {'buggy_code_type': 'function'})


def build_P20(info, fewshot_info=None):
    if not info['initial_block']:
        return None, None
    return _build_pearce(info, _commented_c, {'buggy_code_type': 'function'})


def build_P21(info, fewshot_info=None):
    if not info['initial_block']:
        return None, None
    return _build_pearce(info, _commented_cm, {'buggy_code_type': 'function'})


BUILDERS = {
    'P1':  build_P1,  'P2':  build_P2,  'P3':  build_P3,  'P4':  build_P4,
    'P5':  build_P5,  'P6':  build_P6,  'P7':  build_P7,  'P8':  build_P8,
    'P9':  build_P9,  'P10': build_P10, 'P11': build_P11,
    'P12': build_P12, 'P13': build_P13, 'P14': build_P14, 'P15': build_P15, 'P16': build_P16,
    'P17': build_P17, 'P18': build_P18, 'P19': build_P19, 'P20': build_P20, 'P21': build_P21,
}


def process_sample(kind: str, sample_name: str, sample_dir: str,
                   fewshot_info: dict, overwrite: bool) -> bool:
    out_dir = prompts_sample_dir(kind, sample_name)
    expected = []
    for pid in PAPER_PROMPT_IDS:
        if pid in COT_PROMPTS:
            expected.append(os.path.join(out_dir, f'prompt_{pid}'))
        else:
            expected.append(os.path.join(out_dir, f'prompt_{pid}.txt'))
    if not overwrite and all(os.path.exists(p) for p in expected):
        print(f"[SKIP] {sample_name} already has all prompts")
        return True

    os.makedirs(out_dir, exist_ok=True)
    try:
        info = load_sample_info(sample_dir)
    except Exception as e:
        print(f"[ERROR] {sample_name}: failed to load inputs: {e}")
        return False

    meta = {}
    count = 0
    for pid in PAPER_PROMPT_IDS:
        builder = BUILDERS[pid]
        result, pid_meta = builder(info, fewshot_info)
        if result is None:
            print(f"  [SKIP] {sample_name}/{pid}: template produced no output")
            continue
        if isinstance(result, list):
            dst_dir = os.path.join(out_dir, f'prompt_{pid}')
            os.makedirs(dst_dir, exist_ok=True)
            for i, turn in enumerate(result, 1):
                with open(os.path.join(dst_dir, f'prompt_{pid}_{i}.txt'), 'w') as f:
                    f.write(turn)
            full_text = "".join(result)
        else:
            with open(os.path.join(out_dir, f'prompt_{pid}.txt'), 'w') as f:
                f.write(result)
            full_text = result
        pid_meta['token_count'] = token_count.count_tokens(full_text)
        meta[pid] = pid_meta
        count += 1

    with open(os.path.join(out_dir, 'meta_info.json'), 'w') as f:
        json.dump(meta, f, indent=2)

    print(f"[OK] {sample_name}: wrote {count} paper prompts + meta_info.json")
    return True


def load_fewshot_info(fewshot_sample: str):
    for kind in ('real', 'synthetic'):
        sdir = logicds_sample_dir(kind, fewshot_sample)
        if os.path.isdir(sdir):
            return load_sample_info(sdir)
    raise RuntimeError(f"Few-shot sample '{fewshot_sample}' not found under LogicDS/")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sample-filter', type=str, default=None)
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true')
    parser.add_argument('--fewshot-sample', type=str, default='sample_6',
                        help='Sample whose fixed_block is used as P6 few-shot example (default: sample_6)')
    args = parser.parse_args()

    ensure_logiceval_cwd()
    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None

    fewshot_info = load_fewshot_info(args.fewshot_sample)

    ok = fail = 0
    for kind, name, sdir in iter_samples(args.kind, sample_filter):
        fs = fewshot_info
        if name == args.fewshot_sample:
            fs = load_fewshot_info('sample_4')
        if process_sample(kind, name, sdir, fs, args.overwrite):
            ok += 1
        else:
            fail += 1
    print(f"\nProcessed: {ok} ok, {fail} failed")
    sys.exit(0 if fail == 0 else 1)


if __name__ == '__main__':
    main()
