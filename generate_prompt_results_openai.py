import os
import sys
from typing import List

import token_count
import json
from tqdm import tqdm
from openai import OpenAI


model_id = "o3-mini"
MAX_NEW_TOKENS = 4000
REASONING_TOKEN_ADDITION = 4000
TEMPERATURE = 0.2


confile_file = open("api_config.json", "r")
config_dict = json.load(confile_file)
confile_file.close()
os.environ["OPENAI_API_KEY"] = config_dict["openai_key"]

client = OpenAI()

def read_prompt_file(filepath):
    print(f"[READ] Reading prompt file: {filepath}")
    with open(filepath, 'r') as f:
        return f.read().strip()

def write_response_file(filepath, content):
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w') as f:
        f.write(content)
    print(f"[WRITE] Wrote response to: {filepath}")

def is_file_filled(filepath):
    filled = os.path.exists(filepath) and os.path.getsize(filepath) > 0
    if filled:
        print(f"[SKIP] Already exists and non-empty: {filepath}")
    return filled

def dummy_llm_response(prompt, temperature, system_prompt):
    print(f"[LLM] Calling dummy LLM with temp={temperature}, sys_prompt='{system_prompt[:40]}...', prompt starts with: '{prompt[:50]}...'")
    return f"[Response to]: {prompt[:50]}..."

def interact_with_llm_zeroshot(prompts: List[str], temperature=0.2, system_base="role", save_path="", running_expt_2=False):

    if running_expt_2:

        assert len(prompts) == 1
        prompt = prompts[0]


        base_dir, filename = os.path.split(save_path)
        name_no_ext, ext = os.path.splitext(filename)


        variants = [
            (0.2, system_base),


            (0.2, "task"),
        ]

        for idx, (temp_val, sb) in enumerate(variants, start=1):

            idx = 1 if idx == 1 else 4


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
            completion = client.chat.completions.create(
                model=model_id,
                messages=messages,
                max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
            )
            output = completion.choices[0].message.content
            write_response_file(variant_save_path, output)


            messages.append({"role": "assistant", "content": output})
            messages.append({"role": "user", "content": "Explain the reasoning of your repair in 500 words."})

            completion = client.chat.completions.create(
                model=model_id,
                messages=messages,
                max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
            )
            reasoning_output = completion.choices[0].message.content
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
        {"role": "user", "content": prompt}
    ]
    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content
    write_response_file(save_path, output)


    messages.append({"role": "assistant", "content": output})

    messages.append({"role": "user", "content": "Can you walk me through the repair and explain how it fixes the vulnerability within 500 words?"})


    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
    )
    reasoning_output = completion.choices[0].message.content
    reasoning_path = save_path.replace(".txt", "_reasoning.txt")
    write_response_file(reasoning_path, reasoning_output)

def interact_with_llm_cot(prompts: List[str], temperature=0.2, system_base="role",
                          save_path="", max_token_limit=4000):
    if is_file_filled(save_path):
        return

    system_prompt = (
        "You are a helpful assistant to repair program vulnerabilities."
        if system_base == "role"
        else "You are assigned to give a repaired program of a buggy code."
    )

    responses = []
    messages = []

    file_name = os.path.basename(save_path)
    prompt_id = file_name.replace("prompt_", "").replace(".txt", "")
    prompt_dir = os.path.join(os.path.dirname(save_path), f"prompt_{prompt_id}")
    os.makedirs(prompt_dir, exist_ok=True)
    print(f"[COT] Processing CoT prompts for {prompt_id}, saving to folder: {prompt_dir}")

    for idx, prompt in enumerate(prompts):
        print(f"[COT] Step {idx + 1} prompt input starts with: {prompt[:40]}...")

        if "<CODE_CHANGE>" in prompt and responses:
            print(f"[COT] Replacing <CODE_CHANGE> with previous response at step {idx}")
            prompt = prompt.replace("<CODE_CHANGE>", responses[-1])

        full_tokens = token_count.count_tokens(prompt)
        print(f"[COT] Token count for step {idx + 1}: {full_tokens}")

        if full_tokens > max_token_limit and responses:
            over = full_tokens - max_token_limit
            allowed_words = max((max_token_limit - over - 100) // 2, 50)
            print(f"[COT] Prompt too long. Adding summarization step. Max words: {allowed_words}")

            summary_messages = [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Summarize your response within {allowed_words} words."}
            ]

            completion = client.chat.completions.create(
                model=model_id,
                messages=summary_messages,
                max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
            )
            summary_output = completion.choices[0].message.content
            prompt = prompt.replace(responses[-1], summary_output)
            responses[-1] = summary_output

        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": prompt}
        ]
        completion = client.chat.completions.create(
            model=model_id,
            messages=messages,
            max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
        )
        response = completion.choices[0].message.content
        responses.append(response)

        step_file_path = os.path.join(prompt_dir, f"prompt_{prompt_id}_{idx + 1}.txt")
        write_response_file(step_file_path, response)


    write_response_file(save_path, responses[-1])


    messages.append({"role": "assistant", "content": responses[-1]})

    messages.append({"role": "user", "content": "Can you walk me through the repair and explain how it fixes the vulnerability within 500 words? Do not put any code in your response."})


    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
    )
    reasoning_output = completion.choices[0].message.content

    reasoning_path = save_path.replace(".txt", "_reasoning.txt")
    write_response_file(reasoning_path, reasoning_output)

def generate_response_for_experiment(experiment_id, model_name, vuln_id,
                                     temperature=0.2, system_base="role"):
    input_path = f"llm_outputs_{model_name}/{vuln_id}/prompts/expt{experiment_id}"
    save_base = f"llm_outputs_{model_name}/{vuln_id}/prompt_responses/expt{experiment_id}"

    print(f"\n[EXPERIMENT {experiment_id}] Processing prompts from {input_path}")

    if not os.path.exists(input_path):
        print(f"[WARNING] Input path does not exist: {input_path}")
        return

    items = os.listdir(input_path)


    prompt_folders = sorted(
        [item for item in items if os.path.isdir(os.path.join(input_path, item)) and item.startswith("prompt_")],
        key=lambda x: int(x.split('_')[1])
    )
    prompt_files = sorted(
        [item for item in items if os.path.isfile(os.path.join(input_path, item)) and item.endswith(".txt")]
    )


    for item in prompt_files:
        prompt_id = item.replace(".txt", "")
        save_path = os.path.join(save_base, f"{prompt_id}.txt")
        prompt = read_prompt_file(os.path.join(input_path, item))
        print(f"[EXPT{experiment_id}] Zero-shot prompt: {prompt_id}")
        if experiment_id != 2:
            interact_with_llm_zeroshot([prompt], temperature, system_base, save_path)
        else:
            interact_with_llm_zeroshot([prompt], temperature, system_base, save_path, running_expt_2=True)


    for item in prompt_folders:
        item_path = os.path.join(input_path, item)
        prompt_files = sorted(os.listdir(item_path))
        prompt_list = [read_prompt_file(os.path.join(item_path, pf)) for pf in prompt_files if pf.endswith(".txt")]
        save_path = os.path.join(save_base, f"{item}.txt")
        print(f"[EXPT{experiment_id}] CoT prompt folder: {item}")
        interact_with_llm_cot(prompt_list, temperature, system_base, save_path)

def main():
    if len(sys.argv) != 2:
        print("Usage: python script.py <vuln_id>")
        sys.exit(1)

    model_name = "openai"
    vuln_id = sys.argv[1]

    print(f"[START] Generating LLM responses for model: {model_name}, vulnerability: {vuln_id}")
    for expt_id in range(1, 6):
        generate_response_for_experiment(expt_id, model_name, vuln_id)
    print(f"[COMPLETE] All prompts processed for vulnerability: {vuln_id}")

if __name__ == "__main__":
    main()
