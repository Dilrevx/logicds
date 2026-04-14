import json
import os
import re
import sys

from interact_with_llama import generate_gt_reasoning_llama, evaluate_reasoning_llama
from evaluation_metric import cosine_similarity_between_texts, rouge_l_score_between_texts, code_cosine_similarity_between_texts

def generate_gt_reasoning(vul_id: str, target_model_name):
    print(f"[DEBUG] Starting GT reasoning generation for: {vul_id}")
    manual_input_dir = os.path.join("manual_inputs", vul_id)
    base_path = os.path.join("llm_outputs_{}".format(target_model_name), vul_id)

    description_path = os.path.join(manual_input_dir, "vulnerability_description.txt")
    buggy_path = os.path.join(base_path, "buggy_block.txt")
    fixed_path = os.path.join(base_path, "fixed_block.txt")
    output_file = os.path.join(base_path, f"gt_reasoning_llama.txt")

    print(f"[DEBUG] Checking if GT reasoning file exists: {output_file}")
    if os.path.exists(output_file) and os.path.getsize(output_file) > 0:
        print(f"[SKIP] Ground truth reasoning already exists for {vul_id} using llama.")
        return

    try:
        with open(description_path, 'r') as f:
            vulnerability_description = f.read()
        print(f"[DEBUG] Loaded vulnerability_description from {description_path}")
        with open(buggy_path, 'r') as f:
            buggy_block = f.read()
        print(f"[DEBUG] Loaded buggy_block from {buggy_path}")
        with open(fixed_path, 'r') as f:
            fixed_block = f.read()
        print(f"[DEBUG] Loaded fixed_block from {fixed_path}")
    except FileNotFoundError as e:
        print(f"[ERROR] Missing input file: {e.filename}")
        return

    print(f"[INFO] Generating ground truth reasoning for {vul_id} using llama...")
    generate_gt_reasoning_llama(vulnerability_description, buggy_block, fixed_block, output_file)
    print(f"[SUCCESS] Reasoning saved to {output_file}")

def extract_patch_from_text(text: str) -> str:
    match = re.search(r"<repair>(.*?)</repair>", text, re.DOTALL)
    return match.group(1).strip() if match else ""

def evaluate_llm(vul_id: str, target_model_name: str):
    print(f"[DEBUG] Starting evaluation for: {vul_id}")
    base_output_path = os.path.join("llm_outputs_{}".format(target_model_name), vul_id)
    gt_reasoning_path = os.path.join(base_output_path, "gt_reasoning_llama.txt")
    evaluation_results_base = os.path.join(base_output_path, "evaluation_results")
    patch_base = os.path.join(base_output_path, "prompt_responses")
    meta_info_path = os.path.join(base_output_path, "prompts")

    print(f"[DEBUG] Checking for GT reasoning at {gt_reasoning_path}")
    if not os.path.exists(gt_reasoning_path):
        print(f"[ERROR] Ground truth reasoning not found: {gt_reasoning_path}")
        return

    with open(gt_reasoning_path, 'r') as f:
        gt_reasoning = f.read()

    print(f"[DEBUG] Loaded GT reasoning")

    for expt_id in os.listdir(patch_base):
        expt_dir = os.path.join(patch_base, expt_id)
        results_dir = os.path.join(evaluation_results_base, expt_id)
        os.makedirs(results_dir, exist_ok=True)
        print(f"[DEBUG] Processing experiment: {expt_id}")

        meta_path = os.path.join(meta_info_path, expt_id, "meta_info.json")
        if not os.path.exists(meta_path):
            print(f"[WARNING] Missing meta_info.json for {expt_id}, skipping patch similarity.")
            meta_info = {}
        else:
            with open(meta_path, 'r') as f:
                meta_info = json.load(f)
            print(f"[DEBUG] Loaded meta_info from {meta_path}")

        for file in os.listdir(expt_dir):
            if not file.endswith("_reasoning.txt"):
                continue

            prompt_id = file.replace("_reasoning.txt", "")
            reasoning_file_path = os.path.join(expt_dir, file)
            patch_file_path = os.path.join(patch_base, expt_id, f"{prompt_id}.txt")

            results_path = os.path.join(results_dir, f"{prompt_id}.json")

            print(f"[DEBUG] Evaluating prompt: {prompt_id}")
            print(f"[DEBUG] Reasoning path: {reasoning_file_path}")
            print(f"[DEBUG] Patch path: {patch_file_path}")

            try:
                with open(reasoning_file_path, 'r') as f:
                    generated_reasoning = f.read()
                print(f"[DEBUG] Loaded generated reasoning")
            except Exception as e:
                print(f"[ERROR] Could not read reasoning for {prompt_id}: {e}")
                continue

            reasoning_cosine = cosine_similarity_between_texts(gt_reasoning, generated_reasoning)
            print(f"[DEBUG] Reasoning cosine similarity: {reasoning_cosine:.4f}")
            reasoning_rouge = rouge_l_score_between_texts(gt_reasoning, generated_reasoning)
            print(f"[DEBUG] Reasoning ROUGE-L: {reasoning_rouge:.4f}")
            reasoning_gpt = evaluate_reasoning_llama(gt_reasoning, generated_reasoning)
            print(f"[DEBUG] Reasoning GPT judgment: {reasoning_gpt}")

            result_data = {
                "reasoning_cosine_similarity_judge_llama": float(reasoning_cosine),
                "reasoning_rouge_l_judge_llama": float(reasoning_rouge),
                "reasoning_similar_judge_llama": reasoning_gpt
            }

            meta_prompt_id = prompt_id
            if "expt2" in patch_file_path and "_" in prompt_id.replace("prompt_", ""):
                meta_prompt_id = "prompt_" + prompt_id.split("_")[1]
                print(f"[DEBUG] Meta prompt ID: {meta_prompt_id}")

            if meta_prompt_id in meta_info and os.path.exists(patch_file_path):
                buggy_type = meta_info[meta_prompt_id].get("buggy_code_type", "function")
                gt_patch_path = os.path.join(base_output_path, f"fixed_{buggy_type}.txt")
                print(f"[DEBUG] GT patch path: {gt_patch_path}")

                try:
                    with open(gt_patch_path, 'r') as f:
                        gt_patch = f.read()
                    print(f"[DEBUG] Loaded GT patch")

                    with open(patch_file_path, 'r') as f:
                        patch_text = f.read()
                    extracted_patch = extract_patch_from_text(patch_text)
                    print(f"[DEBUG] Extracted patch from LLM response")

                    patch_cosine = cosine_similarity_between_texts(gt_patch, extracted_patch)
                    print(f"[DEBUG] Patch cosine similarity: {patch_cosine:.4f}")
                    patch_rouge = rouge_l_score_between_texts(gt_patch, extracted_patch)
                    print(f"[DEBUG] Patch ROUGE-L: {patch_rouge:.4f}")
                    patch_cosine_code = code_cosine_similarity_between_texts(gt_patch, extracted_patch)
                    print(f"[DEBUG] Patch code cosine similarity: {patch_cosine_code:.4f}")

                    result_data["patch_cosine_similarity"] = float(patch_cosine)
                    result_data["patch_rouge_l"] = float(patch_rouge)
                    result_data["patch_cosine_similarity_code"] = float(patch_cosine_code)

                except Exception as e:
                    print(f"[WARNING] Failed to evaluate patch for {prompt_id}: {e}")
            else:
                print(f"[INFO] Skipping patch evaluation for {prompt_id} due to missing info. ")
                print(f"prompt_id : {prompt_id}, meta_info : {meta_info}, patch_file_path : {patch_file_path}")

            if os.path.exists(results_path):
                original_results = json.load(open(results_path, 'r'))
                original_results.update(result_data)
                result_data = original_results

            with open(results_path, 'w') as f:
                json.dump(result_data, f, indent=2)
            print(f"[DONE] Saved evaluation for {prompt_id} in {expt_id}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python nlp_evaluation_judge_qwen.py <vul_id> <model_name>")
        sys.exit(1)

    vul_id = sys.argv[1]
    target_model_name = sys.argv[2]
    print(f"[START] Processing vulnerability: {vul_id}")
    generate_gt_reasoning(vul_id, target_model_name)
    evaluate_llm(vul_id, target_model_name)
    print(f"[COMPLETE] Evaluation finished for: {vul_id}")
