import os
os.environ.setdefault("CUDA_DEVICE_ORDER", "PCI_BUS_ID")
os.environ.setdefault("CUDA_VISIBLE_DEVICES", "0")
os.environ.setdefault("PYTORCH_CUDA_ALLOC_CONF", "expandable_segments:True")

import sys
import json
import transformers
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig
from tqdm import tqdm


def _load_config():
    cfg_path = "api_config.json"
    if os.path.exists(cfg_path):
        with open(cfg_path) as f:
            return json.load(f)
    return {}

config_dict = _load_config()

model_id = "meta-llama/Meta-Llama-3.1-70B-Instruct"

MAX_NEW_TOKENS = 8000
TEMPERATURE = 0.2
TOP_P = 0.9

access_token = config_dict.get("hf_token") or os.environ.get("HF_TOKEN")
if not access_token:
    raise RuntimeError(
        "HuggingFace token not set. Provide via HF_TOKEN env var or "
        "an 'hf_token' field in ./api_config.json."
    )

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

model = AutoModelForCausalLM.from_pretrained(model_id, token=access_token,
    device_map={"": 0},
    quantization_config=quantization_config)
print("Model loaded")


tokenizer = AutoTokenizer.from_pretrained(model_id, token=access_token)
print("Tokenizer loaded")


pipeline = transformers.pipeline(
    "text-generation",
    model=model,
    tokenizer=tokenizer,
    torch_dtype="auto",
    trust_remote_code=True,
    device_map="auto",
    token=access_token,
)
print("Pipeline loaded")

terminators = [
    pipeline.tokenizer.eos_token_id,
    pipeline.tokenizer.convert_tokens_to_ids("<|eot_id|>")
]

def generate_repair_description_llama(repair_prompt, output_file):

    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate repair steps for the code patch provided."
                                      "The text should be within 500 words."},
        {"role": "user", "content": repair_prompt},
    ]

    prompt = pipeline.tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    output_sequences = pipeline(
        prompt,
        max_new_tokens=MAX_NEW_TOKENS,
        eos_token_id=terminators,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )

    output = output_sequences[0]["generated_text"][len(prompt):]

    with open(output_file, 'w') as f:
        f.write(output)


def summarize_with_llama(text):

    prompt = "Summarize the following text within 250 words:\n\n" + text

    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will summarize the text provided."
                                      "The text should be within 250 words."},
        {"role": "user", "content": prompt},
    ]

    prompt = pipeline.tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    output_sequences = pipeline(
        prompt,
        max_new_tokens=MAX_NEW_TOKENS,
        eos_token_id=terminators,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )

    output = output_sequences[0]["generated_text"][len(prompt):]

    return output


def generate_gt_reasoning_llama(vulnerability_description, buggy_block, fixed_block, output_file):

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

    prompt = pipeline.tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    output_sequences = pipeline(
        prompt,
        max_new_tokens=MAX_NEW_TOKENS,
        eos_token_id=terminators,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )

    output = output_sequences[0]["generated_text"][len(prompt):]

    with open(output_file, 'w') as f:
        f.write(output)


def generate_patch_reasoning_llama(buggy_block, fixed_block, output_file):

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

    prompt = pipeline.tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    output_sequences = pipeline(
        prompt,
        max_new_tokens=MAX_NEW_TOKENS,
        eos_token_id=terminators,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )

    output = output_sequences[0]["generated_text"][len(prompt):]

    with open(output_file, 'w') as f:
        f.write(output)


def evaluate_reasoning_llama(gt_reasoning, eval_reasoning):

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

    prompt = pipeline.tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    output_sequences = pipeline(
        prompt,
        max_new_tokens=MAX_NEW_TOKENS,
        eos_token_id=terminators,
        do_sample=True,
        temperature=TEMPERATURE,
        top_p=TOP_P,
    )

    output = output_sequences[0]["generated_text"][len(prompt):]

    if "YES" in output.split("\n")[0].strip():
        return True
    else:
        return False
