import argparse
import json
import os
import shutil
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import ensure_logiceval_cwd, iter_samples


ARTIFACTS = [
    'buggy_source_code_file.txt',
    'buggy_function.txt',
    'buggy_block.txt',
    'fixed_source_code_file.txt',
    'fixed_function.txt',
    'fixed_block.txt',
]


def all_artifacts_present(sample_dir: str) -> bool:
    return all(os.path.exists(os.path.join(sample_dir, a)) for a in ARTIFACTS)


def run_download(script: str, target: str) -> None:
    if not os.path.exists(target):
        os.makedirs(target, exist_ok=True)
    subprocess.run(['bash', script, target], check=True)


def clean_dir(d: str) -> None:
    if os.path.isdir(d):
        shutil.rmtree(d)
    os.makedirs(d, exist_ok=True)


def slice_file(src_path: str, start: int, end: int) -> str:
    with open(src_path) as f:
        lines = f.readlines()
    return ''.join(lines[start - 1:end])


def process_sample(kind: str, sample_name: str, sample_dir: str, overwrite: bool) -> bool:
    if not overwrite and all_artifacts_present(sample_dir):
        print(f"[SKIP] {sample_name} already has all 6 artifacts (use --overwrite to regen)")
        return True

    cfg_path = os.path.join(sample_dir, 'inputConfig.json')
    db = os.path.join(sample_dir, 'downloadBuggy.sh')
    df = os.path.join(sample_dir, 'downloadFixed.sh')
    for required in (cfg_path, db, df):
        if not os.path.exists(required):
            print(f"[ERROR] {sample_name}: missing {required}")
            return False

    with open(cfg_path) as f:
        cfg = json.load(f)

    rel_path = cfg['vul_code_file_rel_path']
    scratch = os.path.abspath(os.path.join(sample_dir, '_scratch_projects'))

    try:
        clean_dir(scratch)
        run_download(db, scratch)
        src_full = os.path.join(scratch, rel_path)
        if not os.path.exists(src_full):
            print(f"[ERROR] {sample_name}: buggy source {rel_path} not found in projects dir")
            return False

        shutil.copy(src_full, os.path.join(sample_dir, 'buggy_source_code_file.txt'))
        with open(os.path.join(sample_dir, 'buggy_function.txt'), 'w') as f:
            f.write(slice_file(src_full, int(cfg['vul_code_func_start_line']),
                               int(cfg['vul_code_func_end_line'])))
        with open(os.path.join(sample_dir, 'buggy_block.txt'), 'w') as f:
            f.write(slice_file(src_full, int(cfg['vul_code_block_start_line']),
                               int(cfg['vul_code_block_end_line'])))

        clean_dir(scratch)
        run_download(df, scratch)
        fixed_full = os.path.join(scratch, rel_path)
        if not os.path.exists(fixed_full):
            print(f"[ERROR] {sample_name}: fixed source {rel_path} not found in projects dir")
            return False

        shutil.copy(fixed_full, os.path.join(sample_dir, 'fixed_source_code_file.txt'))
        with open(os.path.join(sample_dir, 'fixed_function.txt'), 'w') as f:
            f.write(slice_file(fixed_full, int(cfg['vul_code_func_fixed_start_line']),
                               int(cfg['vul_code_func_fixed_end_line'])))
        with open(os.path.join(sample_dir, 'fixed_block.txt'), 'w') as f:
            f.write(slice_file(fixed_full, int(cfg['vul_code_block_fixed_start_line']),
                               int(cfg['vul_code_block_fixed_end_line'])))

        print(f"[OK] {sample_name}: generated 6 artifacts")
        return True
    finally:
        shutil.rmtree(scratch, ignore_errors=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sample-filter', type=str, default=None,
                        help='Comma-separated sample names (e.g. sample_1,sample_44)')
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true',
                        help='Regenerate even if all 6 artifacts already exist')
    args = parser.parse_args()

    ensure_logiceval_cwd()

    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None
    ok = 0
    fail = 0
    for kind, name, sdir in iter_samples(args.kind, sample_filter):
        if process_sample(kind, name, sdir, args.overwrite):
            ok += 1
        else:
            fail += 1
    print(f"\nProcessed: {ok} ok, {fail} failed")
    sys.exit(0 if fail == 0 else 1)


if __name__ == '__main__':
    main()
