import os
os.environ.setdefault("CUDA_DEVICE_ORDER", "PCI_BUS_ID")
os.environ.setdefault("CUDA_VISIBLE_DEVICES", "1")
os.environ.setdefault("PYTORCH_CUDA_ALLOC_CONF", "expandable_segments:True")

import argparse
import sys
from typing import List

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples, PAPER_PROMPT_IDS,
                    COT_PROMPTS, prompts_sample_dir, responses_sample_dir)
import token_count

import torch
from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig


MODEL_NAME = 'Qwen/Qwen2.5-Coder-32B-Instruct'
MAX_NEW_TOKENS = 4000
TEMPERATURE = 0.2
TOP_P = 0.9


_model = None
_tokenizer = None


def _load_model():
    global _model, _tokenizer
    if _model is not None:
        return
    print(f"CUDA devices: {torch.cuda.device_count()}")
    quantization_config = BitsAndBytesConfig(
        load_in_4bit=True,
        bnb_4bit_quant_type="nf4",
        bnb_4bit_compute_dtype=torch.bfloat16,
        bnb_4bit_use_double_quant=True,
    )
    _model = AutoModelForCausalLM.from_pretrained(
        MODEL_NAME, device_map={"": 0}, quantization_config=quantization_config,
    )
    _tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
    print("Qwen model + tokenizer loaded")


def _generate(messages, temperature=TEMPERATURE) -> str:
    _load_model()
    text = _tokenizer.apply_chat_template(messages, tokenize=False, add_generation_prompt=True)
    inputs = _tokenizer([text], return_tensors='pt').to(_model.device)
    gen_ids = _model.generate(
        **inputs, max_new_tokens=MAX_NEW_TOKENS,
        do_sample=True, temperature=temperature, top_p=TOP_P,
    )
    new_ids = [out[len(inp):] for inp, out in zip(inputs.input_ids, gen_ids)]
    return _tokenizer.batch_decode(new_ids, skip_special_tokens=True)[0]


def _write(path: str, content: str) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)


def _system_prompt(system_base: str) -> str:
    return ("You are a helpful assistant to repair program vulnerabilities."
            if system_base == 'role'
            else "You are assigned to give a repaired program of a buggy code.")


def run_zeroshot(prompt_text: str, save_path: str, system_base: str = 'role',
                 temperature: float = TEMPERATURE) -> None:
    system_prompt = _system_prompt(system_base)
    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user",   "content": prompt_text},
    ]
    output = _generate(messages, temperature)
    _write(save_path, output)

    messages.append({"role": "assistant", "content": output})
    messages.append({"role": "user",
                     "content": "Explain the reasoning of your repair in 500 words."})
    reasoning = _generate(messages, temperature)
    _write(save_path.replace('.txt', '_reasoning.txt'), reasoning)


def run_cot(prompt_turns: List[str], save_path: str,
            system_base: str = 'role', max_token_limit: int = 4000,
            temperature: float = TEMPERATURE) -> None:
    system_prompt = _system_prompt(system_base)
    responses = []
    messages = []

    prompt_id = os.path.basename(save_path).replace('prompt_', '').replace('.txt', '')
    prompt_dir = os.path.join(os.path.dirname(save_path), f'prompt_{prompt_id}')
    os.makedirs(prompt_dir, exist_ok=True)

    for idx, prompt in enumerate(prompt_turns):
        if "<CODE_CHANGE>" in prompt and responses:
            prompt = prompt.replace("<CODE_CHANGE>", responses[-1])
        if token_count.count_tokens(prompt) > max_token_limit and responses:
            over = token_count.count_tokens(prompt) - max_token_limit
            allowed_words = max((max_token_limit - over - 100) // 2, 50)
            summary = _generate([
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Summarize your response within {allowed_words} words."},
            ], temperature)
            prompt = prompt.replace(responses[-1], summary)
            responses[-1] = summary

        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user",   "content": prompt},
        ]
        response = _generate(messages, temperature)
        responses.append(response)
        _write(os.path.join(prompt_dir, f'prompt_{prompt_id}_{idx + 1}.txt'), response)

    _write(save_path, responses[-1])

    messages.append({"role": "assistant", "content": responses[-1]})
    messages.append({"role": "user",
                     "content": "Explain the natural language text reasoning of your repair in 500 words. Do not put any code in your reasoning text."})
    reasoning = _generate(messages, temperature)
    _write(save_path.replace('.txt', '_reasoning.txt'), reasoning)


P_VARIANTS = {
    'P1': [(TEMPERATURE, 'role')],
    'P2': [(0.5, 'role')],
    'P3': [(0.9, 'role')],
    'P4': [(TEMPERATURE, 'task')],
}


def process_sample(kind: str, sample_name: str, overwrite: bool) -> bool:
    in_dir = prompts_sample_dir(kind, sample_name)
    out_dir = responses_sample_dir('qwen', kind, sample_name)
    if not os.path.isdir(in_dir):
        return True
    os.makedirs(out_dir, exist_ok=True)

    for pid in PAPER_PROMPT_IDS:
        save_path = os.path.join(out_dir, f'prompt_{pid}.txt')
        if not overwrite and os.path.exists(save_path) and os.path.getsize(save_path) > 0:
            continue

        if pid in COT_PROMPTS:
            cot_in_dir = os.path.join(in_dir, f'prompt_{pid}')
            if not os.path.isdir(cot_in_dir):
                continue
            turns = []
            for fn in sorted(os.listdir(cot_in_dir)):
                if fn.endswith('.txt'):
                    with open(os.path.join(cot_in_dir, fn)) as f:
                        turns.append(f.read())
            if not turns:
                continue
            print(f"[CoT] {sample_name}/{pid}")
            run_cot(turns, save_path)
        else:
            p_path = os.path.join(in_dir, f'prompt_{pid}.txt')
            if not os.path.exists(p_path):
                continue
            with open(p_path) as f:
                text = f.read()
            temp, sb = P_VARIANTS.get(pid, [(TEMPERATURE, 'role')])[0]
            print(f"[0S] {sample_name}/{pid} (temp={temp}, system={sb})")
            run_zeroshot(text, save_path, system_base=sb, temperature=temp)
    return True


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sample-filter', type=str, default=None)
    parser.add_argument('--kind', choices=['real', 'synthetic', 'both'], default='both')
    parser.add_argument('--overwrite', action='store_true')
    args = parser.parse_args()

    ensure_logiceval_cwd()
    sample_filter = [s.strip() for s in args.sample_filter.split(',')] if args.sample_filter else None
    ok = fail = 0
    for kind, name, _ in iter_samples(args.kind, sample_filter):
        try:
            if process_sample(kind, name, args.overwrite):
                ok += 1
        except Exception as e:
            print(f"[ERROR] {name}: {e}")
            fail += 1
    print(f"\nProcessed: {ok} ok, {fail} failed")
    sys.exit(0 if fail == 0 else 1)


if __name__ == '__main__':
    main()
