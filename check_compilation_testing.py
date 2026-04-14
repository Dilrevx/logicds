import os
import json
import shutil
from pathlib import Path
import sys
import subprocess
from pprint import pformat

def analyze_test_log(test_output):
    if "TEST PASS" in test_output:
        return 'Pass'

    if ((': FAIL' in test_output) or ('ERROR' in test_output) or
            ('Failing tests: ' in test_output and 'Failing tests: 0' not in test_output)\
            or ('tests failed out of' in test_output and '0 tests failed out of' not in test_output)
            or ('R:dlzexternal:FAIL'in test_output)
            or('Error ' in test_output)
            or('TEST FAIL' in test_output)
            or('Error:' in test_output)):
        return 'Fail'

    return 'Pass'

def run_script(script_path, cwd):
    try:
        print(f"Running script: {script_path} in {cwd}")
        os.chmod(script_path, 0o755)
        result = subprocess.run([str(script_path)], cwd=cwd, capture_output=True, text=True, shell=True)
        return result.stdout + "\n" + result.stderr
    except Exception as e:
        return str(e)

def read_output_if_exists(path):
    if path.exists():
        print(f"Reading output from {path}")
        return path.read_text()
    return ""

def check_validity(vul_id, model_name) -> dict:
    result_dict = {}

    cwd = Path.cwd()
    manual_input_dir = cwd / 'manual_inputs' / vul_id
    base_path = cwd / 'llm_outputs_{}'.format(model_name) / vul_id
    project_dir = cwd / 'projects'

    print(f"\n[+] Running validity checks for vulnerability: {vul_id}")


    try:
        script_path = manual_input_dir / 'downloadFixed.sh'
        print(f"Running {script_path}")
        os.chmod(script_path, 0o755)
        subprocess.run(["bash", str(script_path)], cwd=cwd, check=True)
    except Exception as e:
        print(f"[!] Failed to run downloadFixed.sh: {e}")
        exit(1)


    try:
        with open(manual_input_dir / 'inputConfig.json') as f:
            config = json.load(f)
        print("Loaded inputConfig.json")
    except Exception as e:
        print(f"[!] Error loading inputConfig.json: {e}")
        exit(1)

    rel_path = config['vul_code_file_rel_path']
    source_file_path = project_dir / rel_path

    dest = Path(project_dir)
    dest.mkdir(parents=True, exist_ok=True)


    for sh_file in manual_input_dir.glob('*.sh'):
        dst = dest / sh_file.name
        shutil.copy(sh_file, dst)
        os.chmod(dst, 0o755)


    for c_file in Path(base_path).glob('*.c'):
        dst = dest / c_file.name
        shutil.copy(c_file, dst)


    for script_name in ['Dockerfile', "docker-compose.yml", "test.py"]:
        if not (manual_input_dir / script_name).exists():
            continue
        src_script = manual_input_dir / script_name
        dest_script = project_dir / script_name
        shutil.copy(src_script, dest_script)
        os.chmod(dest_script, 0o755)

    os.chdir(project_dir)
    if vul_id.startswith('S'):
        print("Running autoCompile.sh on fixed version...")
        compile_log = run_script('./autoCompile.sh', project_dir)
    else:
        print("Running compile.sh on fixed version...")
        compile_log = run_script('./compile.sh', project_dir)
    make_output = read_output_if_exists(project_dir / 'make_output.txt') or compile_log
    os.chdir(cwd)


    if "child process started successfully, parent exiting" not in make_output and\
        ('error:' in make_output  or " Error " in make_output or  'Error :' in make_output or 'Error:' in make_output):
        print("[!] Compilation failed on fixed version")
        result_dict['gt_fixed_compilation_success'] = False


    else:
        result_dict['gt_fixed_compilation_success'] = True
        print("[+] Fixed version compiled successfully")




    result_dict['gt_fixed_test_pass'] = True

    def run_test_if_exists(test_script_name):
        test_script = project_dir / test_script_name
        if not test_script.exists():
            return 'Pass'
        os.chmod(test_script, 0o755)
        os.chdir(project_dir)
        test_log = run_script(f'./{test_script_name}', project_dir)
        os.chdir(cwd)
        test_output = read_output_if_exists(project_dir / 'make_test_output.txt') or test_log
        if analyze_test_log(test_output) == 'Fail':
            print(f"[!] Test failed: {test_script_name}")
            result_dict['gt_fixed_test_pass'] = False


            return 'Fail'

    test_scripts = list(manual_input_dir.glob('*testRun*.sh')) + list(manual_input_dir.glob('*TestRun*.sh'))
    test_results = {'TestSingle': 'NA', 'TestAll': 'NA'}

    for test_script in test_scripts:
        shutil.copy(test_script, project_dir / test_script.name)
        os.chmod(project_dir / test_script.name, 0o755)
        print(f"Copied test script: {test_script.name}")

    if vul_id.startswith('S'):
        run_test_if_exists('autoTestRunAll.sh')
    else:
        run_test_if_exists('testRunSingle.sh')
        run_test_if_exists('testRunAll.sh')


    if result_dict['gt_fixed_test_pass']:
        print("[+] Tests passed on fixed version")
    else:
        print("[!] Tests failed on fixed version")






    buggy_path = base_path / 'buggy_source_code_file.txt'
    if not buggy_path.exists():
        print(f"[!] Buggy source file not found: {buggy_path}")
        exit(1)

    print("Compilation try using block")

    buggy_lines = buggy_path.read_text().splitlines()
    full_source_lines = source_file_path.read_text().splitlines()

    block_start = config['vul_code_block_fixed_start_line'] - 1
    block_end = config['vul_code_block_fixed_end_line']
    buggy_block_start = config['vul_code_block_start_line'] - 1
    buggy_block_end = config['vul_code_block_end_line']

    buggy_block = buggy_lines[buggy_block_start:buggy_block_end]
    new_source_lines = full_source_lines[:block_start] + buggy_block + full_source_lines[block_end:]

    source_file_path.write_text('\n'.join(new_source_lines) + '\n')
    print("[+] Replaced fixed block with buggy block in source file")


    os.chdir(project_dir)
    if vul_id.startswith('S'):
        print("Running autoCompile.sh on buggy version...")
        compile_log = run_script('./autoCompile.sh', project_dir)
    else:
        print("Running compile.sh on buggy version...")
        compile_log = run_script('./compile.sh', project_dir)

    make_output = read_output_if_exists(project_dir / 'make_output.txt') or compile_log
    os.chdir(cwd)




    if "child process started successfully, parent exiting" not in make_output and\
        ('error:' in make_output or " Error " in make_output or 'Error :' in make_output or 'Error:' in make_output):
        print("[-] Compilation failed on buggy version with block change")
        result_dict['gt_buggy_block_compilation_success'] = False

        buggy_lines = buggy_path.read_text().splitlines()

        func_start = config['vul_code_func_fixed_start_line'] - 1
        func_end = config['vul_code_func_fixed_end_line']
        buggy_func_start = config['vul_code_func_start_line'] - 1
        buggy_func_end = config['vul_code_func_end_line']

        buggy_func = buggy_lines[buggy_func_start:buggy_func_end]
        new_source_lines = full_source_lines[:func_start] + buggy_func + full_source_lines[func_end:]

        source_file_path.write_text('\n'.join(new_source_lines) + '\n')
        print("[+] Replaced fixed function with buggy in source file")


        os.chdir(project_dir)
        if vul_id.startswith('S'):
            print("Running autoCompile.sh on buggy version...")
            compile_log = run_script('./autoCompile.sh', project_dir)
        else:
            print("Running compile.sh on buggy version...")
            compile_log = run_script('./compile.sh', project_dir)
        make_output = read_output_if_exists(project_dir / 'make_output.txt') or compile_log
        os.chdir(cwd)

        if "child process started successfully, parent exiting" not in make_output and\
            ('error:' in make_output  or " Error " in make_output or 'Error :' in make_output or 'Error:' in make_output):
            print("[-] Compilation failed on buggy version with function change")
            result_dict['gt_buggy_function_compilation_success'] = False


        else:
            result_dict['gt_buggy_function_compilation_success'] = True
            print("[+] Buggy version compiled successfully with function change")
    else:
        result_dict['gt_buggy_block_compilation_success'] = True
        print("[+] Buggy version compiled successfully with block change")

    if not result_dict['gt_buggy_block_compilation_success'] and not result_dict['gt_buggy_function_compilation_success']:
        print("[!] Buggy version compilation failed with both block and function changes")

    else:
        print("[+] Buggy version compiled successfully")


    result_dict['gt_buggy_test_pass'] = False
    def test_should_fail(test_script_name):
        test_script = project_dir / test_script_name
        if not test_script.exists():
            return 'Fail'
        os.chmod(test_script, 0o755)
        os.chdir(project_dir)
        test_log = run_script(f'./{test_script_name}', project_dir)
        os.chdir(cwd)
        test_output = read_output_if_exists(project_dir / 'make_test_output.txt') or test_log
        if analyze_test_log(test_output) != 'Fail':
            print(f"[!] Buggy version unexpectedly passed: {test_script_name}")
            result_dict['gt_buggy_test_pass'] = True


        else:
            result_dict['gt_buggy_test_pass'] = False
            print(f"[+] Buggy version failed as expected: {test_script_name}")
            return 'Fail'

    if vul_id.startswith('S'):
        test_should_fail('autoTestRunAll.sh')
    else:
        test_should_fail('testRunSingle.sh')
        test_should_fail('testRunAll.sh')

    if result_dict['gt_buggy_test_pass']:
        print("[!] Buggy version passed tests unexpectedly")

    else:
        print("[+] Validity check passed: Buggy version compiles but fails tests")

    return result_dict

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python check_compilation_testing.py <vul_id> <model_name>")
        sys.exit(1)

    vul_id = sys.argv[1]
    model_name = sys.argv[2]
    validity_result = check_validity(vul_id, model_name)
    print(f"[+] Validity check result for {vul_id}: \n{pformat(validity_result, indent=2)}")
