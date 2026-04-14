import sys
import os
import json
from tqdm import tqdm
from openai import OpenAI


model_id = "o3-mini"
MAX_NEW_TOKENS = 8000
REASONING_TOKEN_ADDITION = 8000
TEMPERATURE = 0.2


confile_file = open("api_config.json", "r")
config_dict = json.load(confile_file)
confile_file.close()
os.environ["OPENAI_API_KEY"] = config_dict["openai_key"]

client = OpenAI()

def generate_repair_description_openai(repair_prompt, output_file):

    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will generate repair steps for the code patch provided."
                                      "The text should be within 500 words."},
        {"role": "user", "content": repair_prompt},
    ]

    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS+REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content

    with open(output_file, 'w') as f:
        f.write(output)

def summarize_with_openai(text):

    prompt = "Summarize the following text within 250 words:\n\n" + text

    messages = [
        {"role": "system", "content": "You are an helpful AI assistant."
                                      "You will summarize the text provided."
                                      "The text should be within 250 words."},
        {"role": "user", "content": prompt},
    ]

    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS+REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content

    return output

def generate_gt_reasoning_openai(vulnerability_description, buggy_block, fixed_block, output_file):

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

    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS+REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content


    with open(output_file, 'w') as f:
        f.write(output)

def generate_patch_reasoning_openai(buggy_block, fixed_block, output_file):

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

    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS+REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content


    with open(output_file, 'w') as f:
        f.write(output)

def evaluate_reasoning_openai(gt_reasoning, eval_reasoning):

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

    completion = client.chat.completions.create(
        model=model_id,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS+REASONING_TOKEN_ADDITION,
    )
    output = completion.choices[0].message.content


    if "YES" in output.split("\n")[0].strip():
        return True
    else:
        return False


















