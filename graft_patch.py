import os
import json
import re
import shutil
from pathlib import Path

def extract_patch_from_text(text):
    print("[DEBUG] Extracting patch from text...")
    match = re.search(r"<repair>(.*?)</repair>", text, re.DOTALL)
    return match.group(1) if match else None

def graft_patch(original_text, patch, start_line, end_line):
    print(f"[DEBUG] Grafting patch from line {start_line} to {end_line}...")
    try:
        lines = original_text.splitlines()
        return "\n".join(lines[:start_line - 1] + [patch] + lines[end_line:])
    except Exception as e:
        raise RuntimeError(f"Grafting error: {e}")

def create_patch(vul_id, model_name):
    print(f"[DEBUG] Starting patch creation for vuln ID: {vul_id} using model: {model_name}")
    cwd = Path.cwd()
    manual_input_dir = cwd / 'manual_inputs' / vul_id
    staging_dir = cwd / 'llm_outputs_{}'.format(model_name) / vul_id
    download_script = manual_input_dir / 'downloadFixed.sh'


    try:
        print(f"[DEBUG] Making {download_script} executable and running it...")
        os.chmod(download_script, 0o755)
        os.system(f"bash {download_script}")
    except Exception as e:
        print(f"[ERROR] Error running downloadFixed.sh: {e}")

    try:
        config_path = manual_input_dir / 'inputConfig.json'
        print(f"[DEBUG] Loading config from {config_path}")
        with open(config_path) as f:
            project_config = json.load(f)
    except Exception as e:
        print(f"[ERROR] Error loading inputConfig.json: {e}")
        return


    try:
        rel_path = project_config['vul_code_file_rel_path']
        code_file_path = cwd / 'projects' / rel_path
        print(f"[DEBUG] Reading original fixed code from {code_file_path}")
        with open(code_file_path) as f:
            original_text = f.read()
    except Exception as e:
        print(f"[ERROR] Error reading fixed code file: {e}")
        return

    prompts_dir = staging_dir / 'prompt_responses'
    out_dir = staging_dir / 'suggested_patches'


    if out_dir.exists():
        print(f"[DEBUG] Removing existing output directory: {out_dir}")
        shutil.rmtree(out_dir)
    print(f"[DEBUG] Creating output directory: {out_dir}")
    out_dir.mkdir(parents=True, exist_ok=True)

    for expt in prompts_dir.glob("expt*"):
        print(f"[DEBUG] Processing experiment: {expt.name}")
        meta_path = staging_dir / 'prompts' / expt.name / 'meta_info.json'
        try:
            with open(meta_path) as f:
                meta_info = json.load(f)
        except Exception as e:
            print(f"[ERROR] Error loading meta_info.json for {expt.name}: {e}")
            continue

        for prompt_file in expt.glob("*.txt"):
            if "reasoning" in prompt_file.name:
                continue

            prompt_id = prompt_file.stem
            out_path = out_dir / expt.name / f"{prompt_id}.txt"
            out_path.parent.mkdir(parents=True, exist_ok=True)

            try:
                print(f"[DEBUG] Reading LLM response from: {prompt_file}")
                with open(prompt_file) as f:
                    response_text = f.read()
                patch = extract_patch_from_text(response_text)
                if not patch:
                    raise ValueError("<repair> tag not found")

                if prompt_id in meta_info:
                    buggy_type = meta_info[prompt_id]['buggy_code_type']
                else:
                    if expt.name == 'expt2':
                        mod_prompt_id = "prompt_{}".format(prompt_id.split('_')[1])
                        buggy_type = meta_info[mod_prompt_id]['buggy_code_type']
                    else:
                        raise ValueError(f"Prompt ID {prompt_id} not found in meta_info")

                print(f"[DEBUG] Buggy code type: {buggy_type}")

                if buggy_type == 'block':
                    start = project_config['vul_code_block_fixed_start_line']
                    end = project_config['vul_code_block_fixed_end_line']
                elif buggy_type == 'function':
                    start = project_config['vul_code_func_fixed_start_line']
                    end = project_config['vul_code_func_fixed_end_line']
                else:
                    raise ValueError(f"Unknown buggy_code_type: {buggy_type}")

                print(f"[DEBUG] Grafting patch into code from line {start} to {end}")
                grafted_text = graft_patch(original_text, patch, start, end)

                print(f"[DEBUG] Writing patched code to: {out_path}")
                with open(out_path, 'w') as f:
                    f.write(grafted_text)

            except Exception as e:
                print(f"[ERROR] Error processing {prompt_file}: {e}")
                with open(out_path, 'w') as f:
                    f.write("Grafting Error")

if __name__ == "__main__":
    import sys

    if len(sys.argv) != 3:
        print("Usage: python graft_patch.py <vul_id> <model_name>")
        sys.exit(1)

    vul_id = sys.argv[1]
    model_name = sys.argv[2]

    create_patch(vul_id, model_name)
