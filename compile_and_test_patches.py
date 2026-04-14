import os
import json
import shutil
import difflib
from pathlib import Path
import sys
import subprocess
import check_compilation_testing

SKIP_EXISTING = False

def get_line_count(input_config: dict):
    f_count = input_config["vul_code_func_end_line"] - input_config["vul_code_func_start_line"] + 1
    b_count = input_config["vul_code_block_end_line"] - input_config["vul_code_block_start_line"] + 1
    return f_count, b_count

def compute_diff_stats(path_1, path_2):
    file_1 = open(path_1, 'r')
    lines1 = file_1.readlines()
    file_1.close()

    file_2 = open(path_2, 'r')
    lines2 = file_2.readlines()
    file_2.close()

    diff = difflib.ndiff(lines1, lines2)
    added = removed = changed = 0

    for line in diff:
        if line.startswith('+ '):
            added += 1
        elif line.startswith('- '):
            removed += 1



    matcher = difflib.SequenceMatcher(None, lines1, lines2)
    for tag, i1, i2, j1, j2 in matcher.get_opcodes():
        if tag == 'replace':
            changed += max(i2 - i1, j2 - j1)
            removed -= (i2 - i1)
            added -= (j2 - j1)

    return max(0, added), max(0, removed), changed

def analyze_patch(vul_id, model_name):
    base_result = check_compilation_testing.check_validity(vul_id, model_name)

    cwd = Path.cwd()
    manual_input_dir = cwd / 'manual_inputs' / vul_id
    base_path = cwd / 'llm_outputs_{}'.format(model_name) / vul_id
    patch_base = base_path / 'suggested_patches'
    result_base = base_path / 'evaluation_results'
    project_dir = cwd / 'projects'

    print(f"Analyzing patches for vulnerability: {vul_id}, model: {model_name}")


    try:
        script_path = manual_input_dir / 'downloadFixed.sh'
        print(f"Preparing to run {script_path}")
        os.chmod(script_path, 0o755)
        subprocess.run(["bash", str(script_path)], cwd=cwd)
    except Exception as e:
        print(f"Failed to run downloadFixed.sh: {e}")
        return


    try:
        with open(manual_input_dir / 'inputConfig.json') as f:
            config = json.load(f)
        print("Loaded inputConfig.json")
    except Exception as e:
        print(f"Error loading inputConfig.json: {e}")
        return

    f_count, b_count = get_line_count(config)
    base_result["function_line_count"] = f_count
    base_result["block_line_count"] = b_count

    diff_count_added, diff_count_removed, diff_count_changed = compute_diff_stats(base_path / 'buggy_block.txt', base_path / 'fixed_block.txt')
    base_result["gt_diff_count_added"] = diff_count_added
    base_result["gt_diff_count_removed"] = diff_count_removed
    base_result["gt_diff_count_changed"] = diff_count_changed
    base_result["gt_diff_count_total"] = max(diff_count_added, diff_count_removed) + diff_count_changed

    rel_path = config['vul_code_file_rel_path']
    source_file_path = project_dir / rel_path


    for expt_dir in patch_base.glob('expt*'):
        print(f"Processing experiment: {expt_dir.name}")
        for patch_file in expt_dir.glob('*.txt'):
            prompt_id = patch_file.stem
            print(f"\nEvaluating patch: {patch_file}")
            result_path = result_base / expt_dir.name / f'{prompt_id}.json'
            result_path.parent.mkdir(parents=True, exist_ok=True)
            if result_path.exists():
                with open(result_path) as f:
                    existing = json.load(f)
                    if SKIP_EXISTING\
                        and "Compilation" in existing\
                        and os.path.exists(str(result_path).replace(".json", "_make_output.txt"))\
                        and os.path.exists(str(result_path).replace(".json", "_test_output.txt")):
                        print(f"Skipping existing results for {patch_file}")
                        continue

            results = {}
            results.update(base_result)
            results.update({
                'Compilation': 'Fail',
                'TestSingle': 'NA',
                'TestAll': 'NA',
            })
            output_results = {}

            try:
                original_cwd = os.getcwd()


                patch_text = patch_file.read_text()
                source_file_path.write_text(patch_text)
                print(f"Replaced content of {source_file_path} with patch")

                base = Path(base_path)
                dest = Path(project_dir)
                dest.mkdir(parents=True, exist_ok=True)


                for sh_file in base.glob('*.sh'):
                    dst = dest / sh_file.name
                    shutil.copy(sh_file, dst)
                    os.chmod(dst, 0o755)


                for c_file in base.glob('*.c'):
                    dst = dest / c_file.name
                    shutil.copy(c_file, dst)


                for script_name in ['Dockerfile', "docker-compose.yml", "test.py"]:
                    if not (base_path / script_name).exists():
                        continue
                    src_script = base_path / script_name
                    dest_script = project_dir / script_name
                    shutil.copy(src_script, dest_script)
                    os.chmod(dest_script, 0o755)


                os.chdir(project_dir)

                make_output_path = project_dir / 'make_output.txt'
                if make_output_path.exists():
                    make_output_path.write_text("")

                if vul_id.startswith('S'):
                    print("Running autoCompile.sh...")
                    compile_log = check_compilation_testing.run_script('./autoCompile.sh', project_dir)
                else:
                    print("Running compile.sh...")
                    compile_log = check_compilation_testing.run_script('./compile.sh', project_dir)

                os.chdir(original_cwd)

                make_output = check_compilation_testing.read_output_if_exists(project_dir / 'make_output.txt') or ""
                make_output += compile_log
                make_output.replace("\r\n","\n")


                if "child process started successfully, parent exiting" not in make_output and\
                    ('error:' in make_output or " Error " in make_output or 'Error :' in make_output or 'Error:' in make_output):
                    results['Compilation'] = 'Fail'

                    error_lines = [line for line in make_output.splitlines() if 'error:' in line or "Error :" in line or 'Error:' in line]
                    print("Compilation failed due to errors in the output:\n {}.".format(error_lines))
                else:
                    results['Compilation'] = 'Success'

                output_results['make_output'] = str(make_output)
                print(f"Compilation result: {results['Compilation']}")

                if results['Compilation'] == 'Success':
                    if vul_id.startswith('S'):
                        test_single = project_dir / 'autoTestRunAll.sh'
                    else:
                        test_single = project_dir / 'testRunSingle.sh'

                    if test_single.exists():
                        os.chdir(project_dir)
                        if vul_id.startswith('S'):
                            print("Running autoTestRunAll.sh...")
                            test_log = check_compilation_testing.run_script('./autoTestRunAll.sh', project_dir)
                        else:
                            print("Running testRunSingle.sh...")
                            test_log = check_compilation_testing.run_script('./testRunSingle.sh', project_dir)
                        os.chdir(original_cwd)
                        test_output = check_compilation_testing.read_output_if_exists(project_dir / 'make_test_output.txt') or ""
                        test_output += test_log
                        test_output.replace("\r\n","\n")
                        output_results['test_output'] = str(test_output)


                        if check_compilation_testing.analyze_test_log(test_output) == 'Fail':
                            results['TestSingle'] = 'Fail'
                            results['TestAll'] = 'Fail'
                        else:
                            results['TestSingle'] = 'Pass'
                            results['TestAll'] = 'Pass'
                    else:
                        results['TestSingle'] = 'NA'
                        results['TestAll'] = 'NA'

                    print(f"TestSingle result: {results['TestSingle']}")

            except Exception as e:
                print(f"Error during evaluation of {patch_file}: {e}")
                results = {
                    'Compilation': 'Fail',
                    'TestSingle': 'NA',
                    'TestAll': 'NA',
                }


            if result_path.exists():
                with open(result_path) as f:
                    existing = json.load(f)
            else:
                existing = {}

            existing.update(results)

            with open(result_path, 'w') as f:
                json.dump(existing, f, indent=2)

            with open(str(result_path).replace(".json", "_make_output.txt"), 'w') as f:
                f.write(output_results.get('make_output', ''))
            with open(str(result_path).replace(".json", "_test_output.txt"), 'w') as f:
                f.write(output_results.get('test_output', ''))

            print(f"Saved results to {result_path}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python compile_and_test_patches.py <model_name> <vul_id>")
        sys.exit(1)

    model_name = sys.argv[1]
    vul_id = sys.argv[2]

    if len(sys.argv) > 3:
        SKIP_EXISTING = sys.argv[3].lower() == 'true'

    analyze_patch(vul_id, model_name)
