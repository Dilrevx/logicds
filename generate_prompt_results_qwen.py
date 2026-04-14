import os
import json
import sys
from typing import List

import token_count
import transformers
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM
import os
from tqdm import tqdm

confile_file = open("api_config.json", "r")
config_dict = json.load(confile_file)
confile_file.close()
os.environ["CUDA_VISIBLE_DEVICES"] = config_dict["usable_gpus"]

model_name = "Qwen/Qwen2.5-Coder-32B-Instruct"


MAX_NEW_TOKENS = 8000

TEMPERATURE = 0.2
TOP_P = 0.9






INPUT_FOLDER = "inputs/"
OUTPUT_FOLDER = "outputs-qwen-2.5/"

access_token = "YOUR_HF_TOKEN_HERE"
print("Access token :", access_token)

model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype="auto",
    device_map="auto"
)
print("Model loaded")

tokenizer = AutoTokenizer.from_pretrained(model_name)
print("Tokenizer loaded")

def read_prompt_file(filepath: str) -> str:
    print(f"[READ] Reading prompt file: {filepath}")
    with open(filepath, 'r') as f:
        return f.read().strip()

def write_response_file(filepath: str, content: str):
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w') as f:
        f.write(content)
    print(f"[WRITE] Wrote response to: {filepath}")

def is_file_filled(filepath: str) -> bool:
    filled = os.path.exists(filepath) and os.path.getsize(filepath) > 0
    if filled:
        print(f"[SKIP] Already exists and non-empty: {filepath}")
    return filled

def generate_with_qwen(
    messages: List[dict],
    save_path: str,
) -> str:

    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )


    model_inputs = tokenizer([text], return_tensors="pt", padding=True).to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]


    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]


    write_response_file(save_path, output)
    return output







def interact_with_llm_zeroshot(prompts: List[str], temperature=0.2, system_base="role", save_path="", running_expt_2=False):

    if running_expt_2:

        assert len(prompts) == 1
        prompt = prompts[0]


        base_dir, filename = os.path.split(save_path)
        name_no_ext, ext = os.path.splitext(filename)


        variants = [
            (0.2, system_base),
            (0.5, system_base),
            (0.9, system_base),
            (0.2, "task"),
        ]

        for idx, (temp_val, sb) in enumerate(variants, start=1):

            variant_filename = f"{name_no_ext}_{idx}{ext}"
            variant_save_path = os.path.join(base_dir, variant_filename)


            if is_file_filled(variant_save_path):
                continue


            if sb == "role":
                system_prompt = "You are a helpful assistant to repair program vulnerabilities."
            else:
                system_prompt = "You are assigned to give a repaired program of a buggy code."

            print(f"[ZEROSHOT][EXP2] Processing variant {idx} (temperature={temp_val}, system_base='{sb}'), saving to: {variant_save_path}")


            messages = [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": prompt}
            ]

            text = tokenizer.apply_chat_template(
                messages,
                tokenize=False,
                add_generation_prompt=True
            )


            model_inputs = tokenizer([text], return_tensors="pt", padding=True).to(model.device)

            generated_ids = model.generate(
                **model_inputs,
                max_new_tokens=8000,
                temperature=temp_val
            )
            generated_ids = [
                output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
            ]


            output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

            write_response_file(variant_save_path, output)


            messages.append({"role": "assistant", "content": output})
            messages.append({"role": "user", "content": "Explain the reasoning of your repair in 500 words."})


            text = tokenizer.apply_chat_template(
                messages,
                tokenize=False,
                add_generation_prompt=True
            )


            model_inputs = tokenizer([text], return_tensors="pt", padding=True).to(model.device)

            generated_ids = model.generate(
                **model_inputs,
                max_new_tokens=8000,
                temperature=temp_val
            )
            generated_ids = [
                output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
            ]


            reasoning_output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

            reasoning_filename = f"{name_no_ext}_{idx}_reasoning{ext}"
            reasoning_path = os.path.join(base_dir, reasoning_filename)
            write_response_file(reasoning_path, reasoning_output)

        return



    assert len(prompts) == 1
    prompt = prompts[0]
    if is_file_filled(save_path):
        return

    system_prompt = (
        "You are a helpful assistant to repair program vulnerabilities."
        if system_base == "role"
        else "You are assigned to give a repaired program of a buggy code."
    )

    print(f"[ZEROSHOT] Processing zeroshot prompt, saving to: {save_path}")


    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user",   "content": prompt}
    ]
    repair = generate_with_qwen(messages, save_path)


    messages.append({"role": "assistant", "content": repair})
    messages.append({
        "role": "user",
        "content": "Explain the reasoning of your repair in 500 words."
    })
    reasoning_path = save_path.replace(".txt", "_reasoning.txt")
    generate_with_qwen(messages, reasoning_path)

def interact_with_llm_cot(
    prompts: List[str],
    temperature: float = 0.2,
    system_base: str = "role",
    save_path: str = "",
    max_token_limit: int = 8000,
):
    if is_file_filled(save_path):
        return

    system_prompt = (
        "You are a helpful assistant to repair program vulnerabilities."
        if system_base == "role"
        else "You are assigned to give a repaired program of a buggy code."
    )

    responses: List[str] = []

    file_name = os.path.basename(save_path)
    prompt_id = file_name.replace("prompt_", "").replace(".txt", "")
    prompt_dir = os.path.join(os.path.dirname(save_path), f"prompt_{prompt_id}")
    os.makedirs(prompt_dir, exist_ok=True)
    print(f"[COT] Processing CoT prompts for {prompt_id}, saving to folder: {prompt_dir}")

    for idx, prompt in enumerate(prompts):
        print(f"[COT] Step {idx + 1} prompt input starts with: {prompt[:40]}...")


        if "<CODE_CHANGE>" in prompt and responses:
            prompt = prompt.replace("<CODE_CHANGE>", responses[-1])


        full_tokens = token_count.count_tokens(prompt)
        print(f"[COT] Token count for step {idx + 1}: {full_tokens}")


        if full_tokens > max_token_limit and responses:
            over = full_tokens - max_token_limit
            allowed_words = max((max_token_limit - over - 100) // 2, 50)
            print(f"[COT] Prompt too long. Summarizing last response to {allowed_words} words.")

            summary_messages = [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Summarize your response within {allowed_words} words."},
                {"role": "assistant", "content": responses[-1]},
            ]


            text = tokenizer.apply_chat_template(
                summary_messages,
                tokenize=False,
                add_generation_prompt=True
            )


            model_inputs = tokenizer([text], return_tensors="pt").to(model.device)


            generated_ids = model.generate(
                **model_inputs,
                max_new_tokens=MAX_NEW_TOKENS,
                temperature=TEMPERATURE
            )


            generated_ids = [
                out_ids[input_ids.shape[-1]:]
                for input_ids, out_ids in zip(model_inputs.input_ids, generated_ids)
            ]


            summary_out = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]


            responses[-1] = summary_out


            prompt = prompt.replace(responses[-1], summary_out)


        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user",   "content": prompt}
        ]
        step_path = os.path.join(prompt_dir, f"prompt_{prompt_id}_{idx + 1}.txt")
        response = generate_with_qwen(messages, step_path)
        responses.append(response)


    write_response_file(save_path, responses[-1])


    messages.append({"role": "assistant", "content": responses[-1]})
    messages.append({
        "role": "user",
        "content": "Explain the natural language text reasoning of your repair in 500 words. Do not put any code in your reasoning text."
    })
    reasoning_path = save_path.replace(".txt", "_reasoning.txt")
    generate_with_qwen(messages, reasoning_path)

def generate_response_for_experiment(
    experiment_id: int,
    model_name: str,
    vuln_id: str,
    temperature: float = 0.2,
    system_base: str = "role",
):
    input_path = f"llm_outputs_{model_name}/{vuln_id}/prompts/expt{experiment_id}"
    save_base  = f"llm_outputs_{model_name}/{vuln_id}/prompt_responses/expt{experiment_id}"

    print(f"\n[EXPERIMENT {experiment_id}] Processing prompts from {input_path}")
    if not os.path.exists(input_path):
        print(f"[WARNING] Input path does not exist: {input_path}")
        return

    items = os.listdir(input_path)
    prompt_folders = sorted(
        [it for it in items if os.path.isdir(os.path.join(input_path, it)) and it.startswith("prompt_")],
        key=lambda x: int(x.split('_')[1])
    )
    prompt_files = sorted(
        [it for it in items if os.path.isfile(os.path.join(input_path, it)) and it.endswith(".txt")]
    )


    for item in prompt_files:
        prompt_id = item.replace(".txt", "")
        save_path = os.path.join(save_base, f"{prompt_id}.txt")
        prompt    = read_prompt_file(os.path.join(input_path, item))
        print(f"[EXPT{experiment_id}] Zero-shot prompt: {prompt_id}")
        if experiment_id != 2:
            interact_with_llm_zeroshot([prompt], temperature, system_base, save_path)
        else:
            interact_with_llm_zeroshot([prompt], temperature, system_base, save_path, running_expt_2=True)


    for item in prompt_folders:
        item_path    = os.path.join(input_path, item)
        txts         = sorted(os.listdir(item_path))
        prompt_list  = [read_prompt_file(os.path.join(item_path, t)) for t in txts if t.endswith(".txt")]
        save_path    = os.path.join(save_base, f"{item}.txt")
        print(f"[EXPT{experiment_id}] CoT prompt folder: {item}")
        interact_with_llm_cot(prompt_list, temperature, system_base, save_path)

def main():
    if len(sys.argv) != 2:
        print("Usage: python script.py <vuln_id>")
        sys.exit(1)

    model_name = "qwen"
    vuln_id    = sys.argv[1]

    out_dir = f"llm_outputs_{model_name}/{vuln_id}/prompt_responses"
    if not os.path.exists(out_dir):
        os.makedirs(out_dir)

    print(f"[START] Generating LLM responses for model: {model_name}, vulnerability: {vuln_id}")
    for expt_id in range(1, 6):
        generate_response_for_experiment(expt_id, model_name, vuln_id)
    print(f"[COMPLETE] All prompts processed for vulnerability: {vuln_id}")

if __name__ == "__main__":
    main()
