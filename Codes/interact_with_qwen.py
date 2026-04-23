import os
os.environ.setdefault("CUDA_DEVICE_ORDER", "PCI_BUS_ID")
os.environ.setdefault("CUDA_VISIBLE_DEVICES", "0")
os.environ.setdefault("PYTORCH_CUDA_ALLOC_CONF", "expandable_segments:True")

import transformers
import torch
import json
from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig
from tqdm import tqdm

def _load_config():
    cfg_path = "api_config.json"
    if os.path.exists(cfg_path):
        with open(cfg_path) as f:
            return json.load(f)
    return {}

config_dict = _load_config()

model_name = "Qwen/Qwen2.5-Coder-32B-Instruct"

MAX_NEW_TOKENS = 8000
TEMPERATURE = 0.2
TOP_P = 0.9

access_token = config_dict.get("hf_token") or os.environ.get("HF_TOKEN")

print(f"CUDA_VISIBLE_DEVICES = {os.environ.get('CUDA_VISIBLE_DEVICES')}")
print(f"torch.cuda.device_count() = {torch.cuda.device_count()}")
for i in range(torch.cuda.device_count()):
    props = torch.cuda.get_device_properties(i)
    free, total = torch.cuda.mem_get_info(i)
    print(f"  [cuda:{i}] {props.name}  free={free/1e9:.2f} GB  total={total/1e9:.2f} GB")

quantization_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_compute_dtype=torch.bfloat16,
    bnb_4bit_use_double_quant=True,
)

model = AutoModelForCausalLM.from_pretrained(
    model_name,
    device_map={"": 0},
    quantization_config=quantization_config,
)
print("Model loaded")


tokenizer = AutoTokenizer.from_pretrained(model_name)
print("Tokenizer loaded")


def _generate_qwen(messages):
    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True,
    )
    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=MAX_NEW_TOKENS,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]
    return tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]


def generate_repair_description_qwen(repair_prompt, output_file):
    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate repair steps for the code patch provided."
                                      "The text should be within 500 words."},
        {"role": "user", "content": repair_prompt},
    ]
    output = _generate_qwen(messages)
    with open(output_file, 'w') as f:
        f.write(output)


def summarize_with_qwen(text):
    prompt = "Summarize the following text within 250 words:\n\n" + text
    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will summarize the text provided."
                                      "The text should be within 250 words."},
        {"role": "user", "content": prompt},
    ]
    return _generate_qwen(messages)


def generate_gt_reasoning_qwen(vulnerability_description, buggy_block, fixed_block, output_file):
    prompt = ("Explain the reasoning, in natural language text, for the following repair to fix the vulnerability within 500 words.\n "
              "Do not use any code in your reasoning.\n"
              "Here is the vulnerability description:\n<vulnerability_description>\n" + vulnerability_description + "</vulnerability_description>\n"
              "Here is the buggy code.\n" + "<buggy_code>\n" + buggy_block + "\n</buggy_code>\nHere is the repair:\n" +
              "<repair_code>\n" + fixed_block + "\n</repair_code>\n")
    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate a natural language text reasoning for a given fix for a vulnerability. Do not use any code in your reasoning."
                                      "The text should be within 500 words."},
        {"role": "user", "content": prompt},
    ]
    output = _generate_qwen(messages)
    with open(output_file, 'w') as f:
        f.write(output)


def generate_patch_reasoning_qwen(buggy_block, fixed_block, output_file):
    prompt = ("In 500 words, explain the steps taken in a proposed patch, in natural language text. The patch tries to fix a vulnerability.\n "
              "Do not use any code in your reasoning.\n"
              "Here is the buggy code.\n" + "<buggy_code>\n" + buggy_block + "\n</buggy_code>\nHere is the proposed patch:\n" +
              "<proposed_patch>\n" + fixed_block + "\n</proposed_patch>\n")
    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate a natural language that explains the steps taken in a proposed patch. Do not use any code in your reasoning."
                                      "The text should be within 500 words."},
        {"role": "user", "content": prompt},
    ]
    output = _generate_qwen(messages)
    with open(output_file, 'w') as f:
        f.write(output)


def evaluate_reasoning_qwen(gt_reasoning, eval_reasoning):
    prompt = ("Assess whether the two provided reasoning for repair are same or not. Output YES if they are the similar or NO if not in the first line.\n"
              "Ground Truth Reasoning:\n<gt_reasoning>\n" + gt_reasoning + "\n</gt_reasoning>\n"
              "Here is the reasoning provided:\n<provided_reasoning>\n" + eval_reasoning + "\n</provided_reasoning>\n")
    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will evaluate two text reasoning of vulnerability fixes and decide whether they are similar or not. You will only output YES or NO in the"
                                      "first line."
                                      "YES if they agree and NO if they do not."},
        {"role": "user", "content": prompt},
    ]
    output = _generate_qwen(messages)
    first_line = output.split("\n")[0].strip() if output else ""
    return "YES" in first_line
