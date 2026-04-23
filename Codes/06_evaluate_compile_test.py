import argparse
import json
import os
import shutil
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples,
                    logicds_sample_dir, default_patches_dir, evaluation_root)
from _locked_merge import merge_into_eval_json


def run_script(script_path: str, cwd: str) -> str:
    try:
        os.chmod(script_path, 0o755)
        result = subprocess.run([str(script_path)], cwd=cwd, capture_output=True, text=True)
        return (result.stdout or '') + "\n" + (result.stderr or '')
    except Exception as e:
        return str(e)


def analyze_test_log(out: str) -> str:
    if "TEST PASS" in out:
        return 'Pass'
    fail_markers = [': FAIL', 'ERROR', 'TEST FAIL', 'Error:']
    if any(m in out for m in fail_markers):
        return 'Fail'
    if 'Failing tests: ' in out and 'Failing tests: 0' not in out:
        return 'Fail'
    if 'tests failed out of' in out and '0 tests failed out of' not in out:
        return 'Fail'
    return 'Pass'


def analyze_compile_log(out: str) -> str:
    if "child process started successfully, parent exiting" in out:
        return 'Success'
    markers = ['error:', ' Error ', 'Error :', 'Error:']
    if any(m in out for m in markers):
        return 'Fail'
    return 'Success'


def compile_test_one(patch_text: str, sample_ds_dir: str, is_synth: bool) -> dict:
    if shutil.which('docker') is None:
        return {'Compilation': 'NotRun', 'TestSingle': 'NA', 'TestAll': 'NA',
                'reason': 'docker-not-available'}

    cfg_path = os.path.join(sample_ds_dir, 'inputConfig.json')
    df = os.path.join(sample_ds_dir, 'downloadFixed.sh')
    compile_sh = os.path.join(sample_ds_dir, 'compile.sh')
    test_sh = os.path.join(sample_ds_dir, 'testRunSingle.sh')

    if not (os.path.exists(cfg_path) and os.path.exists(df) and os.path.exists(compile_sh)):
        return {'Compilation': 'NotRun', 'TestSingle': 'NA', 'TestAll': 'NA',
                'reason': 'missing-scripts-or-config'}

    with open(cfg_path) as f:
        cfg = json.load(f)
    rel_path = cfg['vul_code_file_rel_path']

    scratch = os.path.abspath(os.path.join(sample_ds_dir, '_scratch_test_projects'))
    try:
        if os.path.isdir(scratch):
            shutil.rmtree(scratch)
        os.makedirs(scratch)
        subprocess.run(['bash', df, scratch], check=True, capture_output=True)

        target = os.path.join(scratch, rel_path)
        if not os.path.exists(target):
            return {'Compilation': 'Fail', 'TestSingle': 'NA', 'TestAll': 'NA',
                    'reason': f'target-file-not-in-projects:{rel_path}'}
        with open(target, 'w') as f:
            f.write(patch_text)

        for script_name in ['compile.sh', 'testRunSingle.sh', 'testRunAll.sh',
                            'autoCompile.sh', 'autoTestRunSingle.sh', 'autoTestRunAll.sh',
                            'Dockerfile']:
            src = os.path.join(sample_ds_dir, script_name)
            if os.path.exists(src):
                dst = os.path.join(scratch, script_name)
                shutil.copy(src, dst)
                if script_name.endswith('.sh'):
                    os.chmod(dst, 0o755)

        compile_target = os.path.join(scratch, 'autoCompile.sh' if is_synth else 'compile.sh')
        if not os.path.exists(compile_target):
            compile_target = os.path.join(scratch, 'compile.sh')
        compile_log = run_script(compile_target, scratch)
        result = {'Compilation': analyze_compile_log(compile_log),
                  'TestSingle': 'NA', 'TestAll': 'NA'}

        if result['Compilation'] == 'Success':
            test_target = os.path.join(scratch, 'autoTestRunAll.sh' if is_synth else 'testRunSingle.sh')
            if os.path.exists(test_target):
                test_log = run_script(test_target, scratch)
                verdict = analyze_test_log(test_log)
                result['TestSingle'] = verdict
                result['TestAll'] = verdict
        return result
    finally:
        shutil.rmtree(scratch, ignore_errors=True)


def walk_sample_patches(sample_patch_dir: str):
    for root, _, files in os.walk(sample_patch_dir):
        for fname in files:
            if fname.endswith('.txt'):
                full = os.path.join(root, fname)
                yield full, os.path.relpath(full, sample_patch_dir)


def eval_path_for(kind: str, sample_name: str, rel: str) -> str:
    stem = os.path.splitext(rel)[0]
    return os.path.join(evaluation_root(), f'{kind}_samples', sample_name, stem + '_evaluation.json')


def process_sample(kind: str, sample_name: str, patches_root: str, overwrite: bool) -> int:
    sample_patch_dir = os.path.join(patches_root, f'{kind}_samples', sample_name)
    if not os.path.isdir(sample_patch_dir):
        return 0

    sample_ds_dir = logicds_sample_dir(kind, sample_name)
    is_synth = (kind == 'synthetic')

    count = 0
    for patch_path, rel in walk_sample_patches(sample_patch_dir):
        eval_path = eval_path_for(kind, sample_name, rel)

        if not overwrite and os.path.exists(eval_path):
            try:
                with open(eval_path) as f:
                    ex = json.load(f)
                if 'Compilation' in ex:
                    continue
            except Exception:
                pass

        with open(patch_path) as f:
            patch_text = f.read()
        result = compile_test_one(patch_text, sample_ds_dir, is_synth)
        merge_into_eval_json(eval_path, result)
        count += 1
        print(f"  {sample_name}/{rel}: {result.get('Compilation')}, test={result.get('TestSingle')}")
    return count


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--patches-dir', type=str, default=default_patches_dir())
    parser.add_argument('--sample-filter', type=str, default=None)
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true')
    args = parser.parse_args()

    ensure_logiceval_cwd()
    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None

    total = 0
    for kind, name, _ in iter_samples(args.kind, sample_filter):
        total += process_sample(kind, name, args.patches_dir, args.overwrite)
    print(f"\nCompile/tested {total} patch files")


if __name__ == '__main__':
    main()
