import os
import sys

import token_count




def extract_file_content(vuln_id, filename):
    for root in ("manual_inputs", "llm_outputs_llama"):
        file_path = os.path.join(root, vuln_id, filename)
        if os.path.exists(file_path):
            with open(file_path, 'r') as fr:
                return fr.read()
    return ""

def get_info_from_vulnerability_id(vuln_id):
    if not os.path.exists(os.path.join("manual_inputs", vuln_id)):
        print(f"Error: Vulnerability id {vuln_id} does not exist.")
        sys.exit(1)

    vulnerability_description = extract_file_content(vuln_id, "vulnerability_description.txt")
    specification = extract_file_content(vuln_id, "specification.txt")
    buggy_function = extract_file_content(vuln_id, "buggy_function.txt")
    buggy_block = extract_file_content(vuln_id, "buggy_block.txt")
    context_code = extract_file_content(vuln_id, "context_codes.txt")
    repair_description = extract_file_content(vuln_id, "repair_description.txt")
    initial_block = extract_file_content(vuln_id, "initial_block.txt")

    return vulnerability_description, specification, buggy_function, buggy_block, context_code, repair_description, initial_block

def get_fewshot_info_from_vulnerability_id(vuln_id="A6"):
    if not os.path.exists(os.path.join("manual_inputs", vuln_id)):
        print(f"Error: Vulnerability id {vuln_id} does not exist.")
        sys.exit(1)

    vulnerability_description = extract_file_content(vuln_id, "vulnerability_description.txt")
    buggy_block = extract_file_content(vuln_id, "buggy_block.txt")
    fixed_block = extract_file_content(vuln_id, "fixed_block.txt")

    return vulnerability_description, buggy_block, fixed_block



def write_prompts_to_folder(vuln_id, prompts, experiment_id, prompts_info_json=None):
    base_path = os.path.join("llm_outputs_llama", vuln_id, "prompts", experiment_id)


    os.makedirs(base_path, exist_ok=True)


    for i, prompt in enumerate(prompts):
        prompt_index = i + 1
        if len(prompt) == 1:
            with open(os.path.join(base_path, f"prompt_{prompt_index}.txt"), 'w', encoding='utf-8') as f:
                f.write(prompt[0])
        else:
            sub_dir = os.path.join(base_path, f"prompt_{prompt_index}")
            os.makedirs(sub_dir, exist_ok=True)
            for j, sub_prompt in enumerate(prompt):
                with open(os.path.join(sub_dir, f"prompt_{prompt_index}_{j + 1}.txt"), 'w', encoding='utf-8') as f:
                    f.write(sub_prompt)


    if prompts_info_json:
        with open(os.path.join(base_path, "meta_info.json"), 'w', encoding='utf-8') as meta_file:
            json.dump(prompts_info_json, meta_file, indent=2)

def _load_config(vuln_id):
    config_file = os.path.join("manual_inputs", vuln_id, "inputConfig.json")
    return json.load(open(config_file, 'r'))

def _get_line_bounds(config):
    return (
        config["vul_code_func_start_line"],
        config["vul_code_block_start_line"],
        config["vul_code_block_end_line"]
    )

def _base_lines(vul_code_func_start, vul_code_block_start, buggy_function_lines):
    return [line for i, line in enumerate(buggy_function_lines)
            if i < (vul_code_block_start - vul_code_func_start)]

def _apply_token_limit(lines, suffix, max_tokens):
    import token_count
    joined = "\n".join(lines)
    full = f"{joined}\n{suffix}"
    if token_count.count_tokens(full) <= max_tokens:
        return lines

    while token_count.count_tokens(full) > max_tokens and lines:
        lines.pop()
        joined = "\n".join(lines)
        full = f"{joined}\n{suffix}"

    return lines

def get_commented_code_upto_bug_nh(vuln_id, max_tokens=4000):
    _, _, buggy_function, _, _, _, _ = get_info_from_vulnerability_id(vuln_id)
    config = _load_config(vuln_id)
    vul_func_start, vul_block_start, _ = _get_line_bounds(config)
    buggy_lines = buggy_function.split("\n")
    base = _base_lines(vul_func_start, vul_block_start, buggy_lines)
    trimmed = _apply_token_limit(base[:], "", max_tokens)
    return "\n".join(trimmed)

def get_commented_code_upto_bug_s1(vuln_id, max_tokens=4000):
    vulnerability_description, _, buggy_function, _, _, _, _ = get_info_from_vulnerability_id(vuln_id)
    config = _load_config(vuln_id)
    vul_func_start, vul_block_start, _ = _get_line_bounds(config)
    buggy_lines = buggy_function.split("\n")
    lines = _base_lines(vul_func_start, vul_block_start, buggy_lines)
    suffix = f"// bugfix: fixed {config['vul_description']}"
    trimmed = _apply_token_limit(lines[:], suffix, max_tokens)
    trimmed.append(suffix)
    return "\n".join(trimmed)

def get_commented_code_upto_bug_s2(vuln_id, max_tokens=4000):
    _, _, buggy_function, _, _, _, _ = get_info_from_vulnerability_id(vuln_id)
    config = _load_config(vuln_id)
    vul_func_start, vul_block_start, _ = _get_line_bounds(config)
    buggy_lines = buggy_function.split("\n")
    lines = _base_lines(vul_func_start, vul_block_start, buggy_lines)
    suffix = f"// fixed {config['vul_description']} bug"
    trimmed = _apply_token_limit(lines[:], suffix, max_tokens)
    trimmed.append(suffix)
    return "\n".join(trimmed)

def get_commented_code_upto_bug_c(vuln_id, max_tokens=4000):
    _, _, buggy_function, buggy_block, _, _, initial_block = get_info_from_vulnerability_id(vuln_id)
    config = _load_config(vuln_id)
    vul_func_start, vul_block_start, vul_block_end = _get_line_bounds(config)
    buggy_lines = buggy_function.split("\n")

    pre_lines = []
    suffix_lines = []

    for i, line in enumerate(buggy_lines):
        if i < (vul_block_start - vul_func_start):
            pre_lines.append(line)
        elif (vul_block_start - vul_func_start) <= i <= (vul_block_end - vul_func_start + 1):
            if i == (vul_block_start - vul_func_start):
                suffix_lines.append(f"// BUG: {config['vul_description']}")
            suffix_lines.append(f"// {line}")

    suffix_lines.append("// FIXED:")
    suffix_lines.extend(initial_block.split("\n"))

    trimmed = _apply_token_limit(pre_lines[:], "\n".join(suffix_lines), max_tokens)
    return "\n".join(trimmed + suffix_lines)

def get_commented_code_upto_bug_cm(vuln_id, max_tokens=4000):
    vulnerability_description, _, buggy_function, _, _, _, initial_block = get_info_from_vulnerability_id(vuln_id)
    config = _load_config(vuln_id)
    vul_func_start, vul_block_start, vul_block_end = _get_line_bounds(config)
    buggy_lines = buggy_function.split("\n")

    pre_lines = []
    suffix_lines = []

    for i, line in enumerate(buggy_lines):
        if i < (vul_block_start - vul_func_start):
            pre_lines.append(line)
        elif (vul_block_start - vul_func_start) <= i <= (vul_block_end - vul_func_start + 1):
            if i == (vul_block_start - vul_func_start):
                suffix_lines.append(f"// BUG: {config['vul_description']}")
                temp = vulnerability_description.replace("\n", " ")
                suffix_lines.append(f"// MESSAGE: {temp}")
            suffix_lines.append(f"// {line}")

    suffix_lines.append("// FIXED VERSION:")
    suffix_lines.extend(initial_block.split("\n"))

    trimmed = _apply_token_limit(pre_lines[:], "\n".join(suffix_lines), max_tokens)
    return "\n".join(trimmed + suffix_lines)

def generate_prompt_expt1():
    prompts = []
    prompts_info_json = {}

    instruction = (
        "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
        "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
        "Your code should be until the end of the function.\n"
    )

    def track_last_normal_index(vuln_id, getter_func):
        _, _, buggy_function, _, _, _, _ = get_info_from_vulnerability_id(vuln_id)
        full_code = getter_func(vuln_id).split("\n")
        original_lines = buggy_function.split("\n")

        last_normal_index = -1
        for i, line in enumerate(full_code):
            if i >= len(original_lines) or line.strip() != original_lines[i].strip():
                break
            last_normal_index = i

        return last_normal_index, last_normal_index + 1


    prompt1_body = get_commented_code_upto_bug_nh(vuln_id)
    prompt1 = instruction + prompt1_body
    idx1, rep1 = track_last_normal_index(vuln_id, get_commented_code_upto_bug_nh)
    prompts_info_json["prompt_1"] = {
        "last_normal_line_index": idx1,
        "first_replaced_line": rep1,
        "token_count": token_count.count_tokens(prompt1),
        "buggy_code_type": "function"
    }
    prompts.append([prompt1])


    prompt2_body = get_commented_code_upto_bug_s1(vuln_id)
    prompt2 = instruction + prompt2_body
    idx2, rep2 = track_last_normal_index(vuln_id, get_commented_code_upto_bug_s1)
    prompts_info_json["prompt_2"] = {
        "last_normal_line_index": idx2,
        "first_replaced_line": rep2,
        "token_count": token_count.count_tokens(prompt2),
        "buggy_code_type": "function"
    }
    prompts.append([prompt2])


    prompt3_body = get_commented_code_upto_bug_s2(vuln_id)
    prompt3 = instruction + prompt3_body
    idx3, rep3 = track_last_normal_index(vuln_id, get_commented_code_upto_bug_s2)
    prompts_info_json["prompt_3"] = {
        "last_normal_line_index": idx3,
        "first_replaced_line": rep3,
        "token_count": token_count.count_tokens(prompt3),
        "buggy_code_type": "function"
    }
    prompts.append([prompt3])


    prompt4_body = get_commented_code_upto_bug_c(vuln_id)
    prompt4 = instruction + prompt4_body
    idx4, rep4 = track_last_normal_index(vuln_id, get_commented_code_upto_bug_c)
    prompts_info_json["prompt_4"] = {
        "last_normal_line_index": idx4,
        "first_replaced_line": rep4,
        "token_count": token_count.count_tokens(prompt4),
        "buggy_code_type": "function"
    }
    prompts.append([prompt4])


    prompt5_body = get_commented_code_upto_bug_cm(vuln_id)
    prompt5 = instruction + prompt5_body
    idx5, rep5 = track_last_normal_index(vuln_id, get_commented_code_upto_bug_cm)
    prompts_info_json["prompt_5"] = {
        "last_normal_line_index": idx5,
        "first_replaced_line": rep5,
        "token_count": token_count.count_tokens(prompt5),
        "buggy_code_type": "function"
    }
    prompts.append([prompt5])

    return prompts, prompts_info_json

def generate_prompt_expt2():
    (vulnerability_description, specification, buggy_function, buggy_block,
     context_code, repair_description, initial_block) = get_info_from_vulnerability_id(vuln_id)

    prompts = []
    prompts_info_json = {}

    def build_prompt(include_repair_desc=False):
        prompt = (
            "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
            "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
            "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
            "Here is the vulnerability description of the buggy source code:\n"
            f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
            "Here is the buggy source code:\n"
            f"<buggy_code>\n{buggy_block}\n</buggy_code>\n"
        )
        if include_repair_desc:
            prompt += (
                "Here is a repair description to fix the buggy source code\n"
                f"<repair_description>\n{repair_description}\n</repair_description>\n"
            )
        prompt += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
        return prompt

    prompt1 = build_prompt(False)
    prompt2 = build_prompt(True)

    prompts.append([prompt1])
    prompts.append([prompt2])

    prompts_info_json["prompt_1"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(prompt1)
    }
    prompts_info_json["prompt_2"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(prompt2)
    }

    return prompts, prompts_info_json

def generate_prompt_fewshot():
    (vulnerability_description, specification, buggy_function, buggy_block,
     context_code, repair_description, initial_block) = get_info_from_vulnerability_id(vuln_id)

    if vuln_id != "A6":
        example_vulnerability_description, example_buggy_block, example_fixed_block = get_fewshot_info_from_vulnerability_id("A6")
    else:
        example_vulnerability_description, example_buggy_block, example_fixed_block  = get_fewshot_info_from_vulnerability_id("A4")

    prompts = []
    prompts_info_json = {}

    def build_prompt(include_repair_desc=False):
        prompt = (
            "Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
            "Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
            "The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
            "As an example, here is a vulnerability description of a buggy source code:\n"
            f"<example_vulnerability_description>\n{example_vulnerability_description}\n</example_vulnerability_description>\n"
            "Here is the buggy source code of the example:\n"
            f"<example_buggy_code>\n{example_buggy_block}\n</example_buggy_code>\n"
            "Here is the fixed code of the example:\n"
            f"<example_answer>\n<repair>\n{example_fixed_block}\n</repair>\n</example_answer>\n"
            "Here is the vulnerability description of the buggy source code we want to fix:\n"
            f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
            "Here is the buggy source code we want to fix:\n"
            f"<buggy_code>\n{buggy_block}\n</buggy_code>\n"
        )
        if include_repair_desc:
            prompt += (
                "Here is a repair description to fix the buggy source code we want to fix\n"
                f"<repair_description>\n{repair_description}\n</repair_description>\n"
            )
        prompt += "Provide a repair for the mentioned code snippet to fix the vulnerability we want to fix.\n"
        return prompt

    prompt1 = build_prompt(False)
    prompt2 = build_prompt(True)

    prompts.append([prompt1])
    prompts.append([prompt2])

    prompts_info_json["prompt_7"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(prompt1)
    }
    prompts_info_json["prompt_8"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(prompt2)
    }

    return prompts, prompts_info_json

def generate_prompt_expt3():
    (vulnerability_description, specification, buggy_function, buggy_block,
     context_code, repair_description, initial_block) = get_info_from_vulnerability_id(vuln_id)

    def build_prompt_with_intro(thinking=False, include_repair_desc=False, cot2=False):
        intro = "Q: " if thinking else ""
        reasoning = "A: Let's think step by step.\n" if thinking else ""

        repair_part = (f"Here is a repair description to fix the buggy source code\n"
                       f"<repair_description>\n{repair_description}\n</repair_description>\n") if include_repair_desc else ""

        prompt = (f"{intro}You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
                  f"Here is the vulnerability description of the buggy source code:\n"
                  f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n")

        if not cot2:
            prompt += (f"Here is the buggy source code:\n"
                       f"<buggy_code>\n{buggy_block}\n</buggy_code>\n")

        prompt += repair_part
        prompt += reasoning
        return prompt

    def build_repair_prompt(with_code_changes=False):
        repair_instruction = (f"Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
                              f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                              f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                              f"Here is the buggy source code:\n"
                              f"<buggy_code>\n{buggy_block}\n</buggy_code>\n")
        if with_code_changes:
            repair_instruction += (f"The following are the code changes you suggested:\n"
                                   f"<CODE_CHANGE>\n")

        repair_instruction += "Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n"
        return repair_instruction

    prompts = []
    prompts_info_json = {}

    z_prompt_1 = generate_prompt_expt2()[0][0][0]
    prompts.append([z_prompt_1])
    prompts_info_json["prompt_1"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(z_prompt_1)
    }

    z_prompt_2 = generate_prompt_expt2()[0][1][0]
    prompts.append([z_prompt_2])
    prompts_info_json["prompt_2"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(z_prompt_2)
    }

    def add_pair_prompt(prompt_id, prompt_a, prompt_b):
        prompts.append([prompt_a, prompt_b])
        prompts_info_json[f"prompt_{prompt_id}"] = {
            "buggy_code_type": "block",
            "token_count": token_count.count_tokens(prompt_a + prompt_b)
        }

    add_pair_prompt(3, build_prompt_with_intro(thinking=True), build_repair_prompt(with_code_changes=True))
    add_pair_prompt(4, build_prompt_with_intro(), build_repair_prompt())
    add_pair_prompt(5, build_prompt_with_intro(thinking=True, include_repair_desc=True), build_repair_prompt(with_code_changes=True))
    add_pair_prompt(6, build_prompt_with_intro(include_repair_desc=True, cot2=True), build_repair_prompt())

    f_prompt_1 = generate_prompt_fewshot()[0][0][0]
    prompts.append([f_prompt_1])
    prompts_info_json["prompt_7"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(f_prompt_1)
    }
    f_prompt_2 = generate_prompt_fewshot()[0][1][0]
    prompts.append([f_prompt_2])
    prompts_info_json["prompt_8"] = {
        "buggy_code_type": "block",
        "token_count": token_count.count_tokens(f_prompt_2)
    }

    return prompts, prompts_info_json

def generate_prompt_expt4(meth1="zeroshot", max_token_threshold=4000):
    (vulnerability_description, specification, buggy_function, buggy_block,
     context_code, repair_description, initial_block) = get_info_from_vulnerability_id(vuln_id)

    prompts = []
    prompts_info_json = {}

    def trim_context(prompt_template):
        nonlocal context_code
        while token_count.count_tokens(prompt_template()) > max_token_threshold:
            over_limit = token_count.count_tokens(prompt_template()) - max_token_threshold
            context_code = token_count.curtail_context_to_cut_tokens(context_code, over_limit)
            if context_code == "":
                return ""
        return prompt_template()

    def make_prompt_with_context_and_bug(code, include_repair_desc=False):
        def template():
            base = (
                f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
                f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                f"Here is the vulnerability description of the buggy source code:\n"
                f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
                f"Source code:\n"
                f"Context code:\n<context_code>\n{context_code}\n</context_code>\n"
                f"Buggy source code:\n\n<buggy_code>\n{code}\n</buggy_code>\n"
            )
            if include_repair_desc:
                base += (
                    f"Here is a repair description to fix the buggy source code\n"
                    f"<repair_description>\n{repair_description}\n</repair_description>\n"
                )
            base += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
            return base

        return trim_context(template)

    def make_prompt_with_bug_only(code, include_repair_desc=False):
        base = (
            f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
            f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
            f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
            f"Here is the vulnerability description of the buggy source code:\n"
            f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
            f"Here is the buggy source code:\n<buggy_code>\n{code}\n</buggy_code>\n"
        )
        if include_repair_desc:
            base += (
                f"Here is a repair description to fix the buggy source code\n"
                f"<repair_description>\n{repair_description}\n</repair_description>\n"
            )
        base += "Provide a repair for the mentioned code snippet to fix the vulnerability.\n"
        return base if token_count.count_tokens(base) <= max_token_threshold else ""

    def build_thinking_then_repair_prompts(code_block, include_context=False, include_repair_desc=False):
        context_part = (f"Context code:\n<context_code>\n{context_code}\n</context_code>\n" if include_context else "")
        repair_desc_part = (f"Here is a repair description to fix the buggy source code\n"
                            f"<repair_description>\n{repair_description}\n</repair_description>\n" if include_repair_desc else "")

        prompt_think = (f"Q: You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
                        f"Here is the vulnerability description of the buggy source code:\n"
                        f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
                        f"{context_part}"
                        f"Here is the buggy source code:\n"
                        f"<buggy_code>\n{code_block}\n</buggy_code>\n"
                        f"{repair_desc_part}"
                        f"A: Let's think step by step.\n")

        prompt_repair = (f"Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
                         f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                         f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                         f"Here is the buggy source code:\n"
                         f"<buggy_code>\n{code_block}\n</buggy_code>\n"
                         f"The following are the code changes you suggested:\n"
                         f"<CODE_CHANGE>\n"
                         f"Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n")

        return prompt_think, prompt_repair

    def build_cot2_prompts(code_block, include_context=False, include_repair_desc=False):
        context_part = (f"Context code:\n<context_code>\n{context_code}\n</context_code>\n" if include_context else "")
        repair_desc_part = (f"Here is a repair description to fix the buggy source code\n"
                            f"<repair_description>\n{repair_description}\n</repair_description>\n" if include_repair_desc else "")

        prompt_info = (f"You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
                       f"Here is the vulnerability description of the buggy source code:\n"
                       f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
                       f"{context_part}{repair_desc_part}")

        prompt_action = (f"Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
                         f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                         f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                         f"<buggy_code>\n{code_block}\n</buggy_code>\n"
                         f"Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n")

        return prompt_info, prompt_action

    def add_prompt_info(prompt_list, idx, is_empty, code_type):
        full_text = "\n".join(prompt_list) if isinstance(prompt_list, list) else prompt_list
        prompts_info_json[f"prompt_{idx}"] = {
            "isEmpty": is_empty,
            "buggy_code_type": code_type,
            "token_count": token_count.count_tokens(full_text)
        }

    if meth1 == "zeroshot":
        raw_prompts = [
            (generate_prompt_expt2()[0][0][0], "block"),
            (generate_prompt_expt2()[0][1][0], "block"),
            (make_prompt_with_context_and_bug(buggy_block), "block"),
            (make_prompt_with_context_and_bug(buggy_block, include_repair_desc=True), "block"),
            (make_prompt_with_bug_only(buggy_function), "function"),
            (make_prompt_with_bug_only(buggy_function, include_repair_desc=True), "function"),
            (make_prompt_with_context_and_bug(buggy_function), "function"),
            (make_prompt_with_context_and_bug(buggy_function, include_repair_desc=True), "function")
        ]

        for i, (p, code_type) in enumerate(raw_prompts):
            if p:
                prompts.append([p])
                add_prompt_info(p, i + 1, False, code_type)
            else:
                add_prompt_info("", i + 1, True, code_type)

    elif meth1 in ["cot1", "cot2"]:
        builder = build_thinking_then_repair_prompts if meth1 == "cot1" else build_cot2_prompts

        combinations = [
            (buggy_block, False, False, "block"),
            (buggy_block, True, False, "block"),
            (buggy_block, True, True, "block"),
            (buggy_function, False, False, "function"),
            (buggy_function, True, False, "function"),
            (buggy_function, True, True, "function")
        ]

        for i, (code_block, with_context, with_repair_desc, code_type) in enumerate(combinations):
            prompt_a, prompt_b = builder(code_block, with_context, with_repair_desc)
            full_prompt = prompt_a + prompt_b

            while token_count.count_tokens(full_prompt) > max_token_threshold:
                over_limit = token_count.count_tokens(full_prompt) - max_token_threshold
                context_code = token_count.curtail_context_to_cut_tokens(context_code, over_limit)
                if with_context and context_code == "":
                    break
                prompt_a, prompt_b = builder(code_block, with_context, with_repair_desc)
                full_prompt = prompt_a + prompt_b

            if token_count.count_tokens(full_prompt) <= max_token_threshold:
                prompts.append([prompt_a, prompt_b])
                add_prompt_info([prompt_a, prompt_b], i + 1, False, code_type)
            else:
                add_prompt_info("", i + 1, True, code_type)

    return prompts, prompts_info_json

def generate_prompt_expt5(meth1="zeroshot", code1="block", max_token_threshold=3000):
    (vulnerability_description, specification, buggy_function, buggy_block,
     context_code, repair_description, initial_block) = get_info_from_vulnerability_id(vuln_id)

    def construct_bug_code(code_type):
        if code_type == "function" and token_count.count_tokens(buggy_function) < max_token_threshold:
            return f"<buggy_code>\n{buggy_function}\n</buggy_code>\n", "function"
        elif code_type == "context_block" and token_count.count_tokens(context_code) + token_count.count_tokens(buggy_block) < max_token_threshold:
            return f"<context_code>\n{context_code}\n</context_code>\n<buggy_code>\n{buggy_block}\n</buggy_code>\n", "block_with_context"
        elif code_type == "context_function" and token_count.count_tokens(context_code) + token_count.count_tokens(buggy_function) < max_token_threshold:
            return f"<context_code>\n{context_code}\n</context_code>\n<buggy_code>\n{buggy_function}\n</buggy_code>\n", "function_with_context"
        return f"<buggy_code>\n{buggy_block}\n</buggy_code>\n", "block"

    def count_and_package(prompt):
        return {
            "text": prompt,
            "token_count": token_count.count_tokens(prompt),
            "isEmpty": prompt == ""
        }

    bug_code, code_type_used = construct_bug_code(code1)
    prompts = []
    prompts_info_json = {}

    def store_prompt(index, prompt):
        prompts.append([prompt["text"]])
        prompts_info_json[f"prompt_{index}"] = {
            "token_count": prompt["token_count"],
            "isEmpty": prompt["isEmpty"],
            "buggy_code_type": code_type_used
        }

    if meth1 == "zeroshot":
        p1 = count_and_package(
            f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
            f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
            f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
            f"Here is the buggy source code:\n{bug_code}"
            f"Provide a repair for the mentioned code snippet to fix the vulnerability.\n")
        store_prompt(1, p1)

        p2 = count_and_package(generate_prompt_expt2()[0][0][0])
        store_prompt(2, p2)

        if specification:
            p3_text = (f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
                       f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                       f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                       f"Here is the vulnerability description of the buggy source code:\n"
                       f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
                       f"Here is a description of specification related to the vulnerability:\n"
                       f"<specification>\n{specification}\n</specification>\n"
                       f"Here is the buggy source code:\n{bug_code}"
                       f"Provide a repair for the mentioned code snippet to fix the vulnerability.\n")
            store_prompt(3, count_and_package(p3_text))

        p4 = count_and_package(generate_prompt_expt2()[0][1][0])
        store_prompt(4, p4)

        p5_text = (f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
                   f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                   f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                   f"Here is the buggy source code:\n{bug_code}"
                   f"Here is a description to repair the buggy source code\n"
                   f"<repair_description>\n{repair_description}\n</repair_description>\n"
                   f"Provide a repair for the mentioned code snippet to fix the vulnerability.\n")
        store_prompt(5, count_and_package(p5_text))

    elif meth1 in ["cot1", "cot2"]:
        is_cot1 = meth1 == "cot1"

        def reasoning_prompt(include_spec=False):
            header = "Q:" if is_cot1 else ""
            reasoning = "A: Let's think step by step.\n" if is_cot1 else ""
            spec_block = (f"Here is a description of specification related to the vulnerability:\n"
                          f"<specification>\n{specification}\n</specification>\n") if include_spec else ""

            return (f"{header} You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
                    f"Here is the vulnerability description of the buggy source code:\n"
                    f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
                    f"{spec_block}"
                    f"Here is the buggy source code:\n{bug_code}"
                    f"{reasoning}")

        def repair_prompt(extra_text=""):
            return (f"Provide a repair for the mentioned buggy code snippet below to fix the vulnerability.\n"
                    f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
                    f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
                    f"Here is the buggy source code:\n{bug_code}"
                    f"{extra_text}Provide a repair for the mentioned code snippet to fix the previously mentioned vulnerability.\n")

        p1 = count_and_package(
            f"Provide a repair for the mentioned buggy code snippet below to fix a vulnerability.\n"
            f"Provide repaired code between <repair> and </repair> tags. Do not provide any extra text explanation.\n"
            f"The code between <repair> and </repair> will be directly copied to replace the code between <buggy_code> and </buggy_code>.\n"
            f"Here is the buggy source code:\n{bug_code}"
            f"Provide a repair for the mentioned code snippet to fix the vulnerability.\n")
        store_prompt(1, p1)

        p2a = count_and_package(reasoning_prompt())
        p2b = count_and_package(repair_prompt("The following are the code changes you suggested:\n<CODE_CHANGE>\n"))
        prompts.append([p2a["text"], p2b["text"]])
        prompts_info_json["prompt_2"] = {
            "token_count": p2a["token_count"] + p2b["token_count"],
            "isEmpty": False,
            "buggy_code_type": code_type_used
        }

        if specification:
            p3a = count_and_package(reasoning_prompt(include_spec=True))
            p3b = count_and_package(repair_prompt("The following are the code changes you suggested:\n<CODE_CHANGE>\n"))
            prompts.append([p3a["text"], p3b["text"]])
            prompts_info_json["prompt_3"] = {
                "token_count": p3a["token_count"] + p3b["token_count"],
                "isEmpty": False,
                "buggy_code_type": code_type_used
            }

        p4a = count_and_package(
            f"You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
            f"Here is the vulnerability description of the buggy source code:\n"
            f"<vulnerability_description>\n{vulnerability_description}\n</vulnerability_description>\n"
            f"Here is a repair description to fix the buggy source code\n"
            f"<repair_description>\n{repair_description}\n</repair_description>\n")

        p4b = count_and_package(repair_prompt())
        prompts.append([p4a["text"], p4b["text"]])
        prompts_info_json["prompt_4"] = {
            "token_count": p4a["token_count"] + p4b["token_count"],
            "isEmpty": False,
            "buggy_code_type": code_type_used
        }

        p5a = count_and_package(
            f"You will provide a repair for a mentioned buggy code snippet to fix a vulnerability.\n"
            f"Here is a repair description to fix the buggy source code\n"
            f"<repair_description>\n{repair_description}\n</repair_description>\n")
        p5b = count_and_package(repair_prompt())
        prompts.append([p5a["text"], p5b["text"]])
        prompts_info_json["prompt_5"] = {
            "token_count": p5a["token_count"] + p5b["token_count"],
            "isEmpty": False,
            "buggy_code_type": code_type_used
        }

    return prompts, prompts_info_json





if __name__ == "__main__":
    import json


    if len(sys.argv) != 2:
        print("Usage: python prepare_llm_prompts_llama.py <vulnerability_id>")
        sys.exit(1)

    vuln_id = sys.argv[1]
    if not os.path.exists(os.path.join("manual_inputs", vuln_id)):
        print(f"Error: Vulnerability id {vuln_id} does not exist.")
        sys.exit(1)


    prompts_expt1, info_expt1 = generate_prompt_expt1()
    write_prompts_to_folder(vuln_id, prompts_expt1, "expt1", prompts_info_json=info_expt1)


    prompts_expt2, info_expt2 = generate_prompt_expt2()
    write_prompts_to_folder(vuln_id, prompts_expt2, "expt2", prompts_info_json=info_expt2)


    prompts_expt3, info_expt3 = generate_prompt_expt3()
    write_prompts_to_folder(vuln_id, prompts_expt3, "expt3", prompts_info_json=info_expt3)


    prompts_expt4, info_expt4 = generate_prompt_expt4()
    write_prompts_to_folder(vuln_id, prompts_expt4, "expt4", prompts_info_json=info_expt4)


    prompts_expt5, info_expt5 = generate_prompt_expt5()
    write_prompts_to_folder(vuln_id, prompts_expt5, "expt5", prompts_info_json=info_expt5)

    print(f"Prompts for vulnerability id {vuln_id} have been written to llm_outputs_llama/{vuln_id}/prompts/")

