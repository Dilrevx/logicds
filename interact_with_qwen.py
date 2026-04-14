import transformers
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM
import os
from tqdm import tqdm
import json

confile_file = open("api_config.json", "r")
config_dict = json.load(confile_file)
confile_file.close()
os.environ["CUDA_VISIBLE_DEVICES"] = config_dict["usable_gpus"]

model_name = "Qwen/Qwen2.5-Coder-32B-Instruct"


MAX_NEW_TOKENS = 8000

TEMPERATURE = 0.2
TOP_P = 0.9






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

def generate_repair_description_qwen(repair_prompt, output_file):

    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate repair steps for the code patch provided."
                                      "The text should be within 500 words."},
        {"role": "user", "content": repair_prompt},
    ]


    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )



    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]



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


    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )



    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]


    return output

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


    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )



    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]



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


    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )



    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]



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


    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )



    model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

    generated_ids = model.generate(
        **model_inputs,
        max_new_tokens=8000,
        temperature=TEMPERATURE
    )
    generated_ids = [
        output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
    ]

    output = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]



    if "YES" in output.split("\n")[0].strip():
        return True
    else:
        return False
