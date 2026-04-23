import argparse
import json
import os
import sys
from typing import List

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import (ensure_logiceval_cwd, iter_samples, PAPER_PROMPT_IDS,
                    COT_PROMPTS, OPENAI_NA_PROMPTS,
                    prompts_sample_dir, responses_sample_dir)
import token_count

from openai import OpenAI

MODEL_ID = 'o3-mini'
MAX_NEW_TOKENS = 4000
REASONING_TOKEN_ADDITION = 4000

with open('api_config.json') as _f:
    _cfg = json.load(_f)
os.environ['OPENAI_API_KEY'] = _cfg['openai_key']
_client = OpenAI()


def _chat(messages):
    completion = _client.chat.completions.create(
        model=MODEL_ID,
        messages=messages,
        max_completion_tokens=MAX_NEW_TOKENS + REASONING_TOKEN_ADDITION,
    )
    return completion.choices[0].message.content


def _write(path: str, content: str) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)


def _system_prompt(system_base: str) -> str:
    return ("You are a helpful assistant to repair program vulnerabilities."
            if system_base == 'role'
            else "You are assigned to give a repaired program of a buggy code.")


def run_zeroshot(prompt_text: str, save_path: str, system_base: str = 'role') -> None:
    system_prompt = _system_prompt(system_base)
    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user",   "content": prompt_text},
    ]
    output = _chat(messages)
    _write(save_path, output)

    messages.append({"role": "assistant", "content": output})
    messages.append({"role": "user",
                     "content": "Can you walk me through the repair and explain how it fixes the vulnerability within 500 words?"})
    reasoning = _chat(messages)
    _write(save_path.replace('.txt', '_reasoning.txt'), reasoning)


def run_cot(prompt_turns: List[str], save_path: str,
            system_base: str = 'role', max_token_limit: int = 4000) -> None:
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
            summary_output = _chat([
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Summarize your response within {allowed_words} words."},
            ])
            prompt = prompt.replace(responses[-1], summary_output)
            responses[-1] = summary_output

        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user",   "content": prompt},
        ]
        response = _chat(messages)
        responses.append(response)

        _write(os.path.join(prompt_dir, f'prompt_{prompt_id}_{idx + 1}.txt'), response)

    _write(save_path, responses[-1])

    messages.append({"role": "assistant", "content": responses[-1]})
    messages.append({"role": "user",
                     "content": "Can you walk me through the repair and explain how it fixes the vulnerability within 500 words? Do not put any code in your response."})
    reasoning = _chat(messages)
    _write(save_path.replace('.txt', '_reasoning.txt'), reasoning)


def process_sample(kind: str, sample_name: str, overwrite: bool) -> bool:
    in_dir = prompts_sample_dir(kind, sample_name)
    out_dir = responses_sample_dir('openai', kind, sample_name)
    if not os.path.isdir(in_dir):
        print(f"[SKIP] {sample_name}: no prompts at {in_dir}")
        return True
    os.makedirs(out_dir, exist_ok=True)

    for pid in PAPER_PROMPT_IDS:
        if pid in OPENAI_NA_PROMPTS:
            continue
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
            sb = 'task' if pid == 'P4' else 'role'
            print(f"[0S] {sample_name}/{pid} (system={sb})")
            run_zeroshot(text, save_path, system_base=sb)
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
