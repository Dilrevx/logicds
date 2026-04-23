import os

PAPER_PROMPT_IDS = [f'P{i}' for i in range(1, 22)]

COT_PROMPTS = {'P7', 'P8'}

OPENAI_NA_PROMPTS = {'P2', 'P3'}


def is_cot_prompt(pid: str) -> bool:
    return pid in COT_PROMPTS


def logicds_sample_dir(kind: str, sample_name: str) -> str:
    return os.path.join('LogicDS', f'{kind}_samples', sample_name)


def prompts_sample_dir(kind: str, sample_name: str) -> str:
    return os.path.join('LLM_experiments', 'prompts', f'{kind}_samples', sample_name)


def responses_sample_dir(llm: str, kind: str, sample_name: str) -> str:
    return os.path.join('LLM_experiments', 'responses', llm, f'{kind}_samples', sample_name)


def default_patches_dir() -> str:
    return os.path.join('LLM_experiments', 'patches')


def evaluation_root() -> str:
    return os.path.join('LLM_experiments', 'evaluation')


def evaluation_sample_dir(kind: str, sample_name: str) -> str:
    return os.path.join(evaluation_root(), f'{kind}_samples', sample_name)


def iter_samples(kind: str = 'both', sample_filter=None):
    kinds = ['real', 'synthetic'] if kind == 'both' else [kind]
    filt = set(sample_filter) if sample_filter else None
    for k in kinds:
        base = f'LogicDS/{k}_samples'
        if not os.path.isdir(base):
            continue
        for name in sorted(os.listdir(base), key=_sample_sort_key):
            if filt is not None and name not in filt:
                continue
            p = os.path.join(base, name)
            if os.path.isdir(p):
                yield k, name, p


def _sample_sort_key(name: str):
    parts = name.split('_')
    try:
        return (parts[0], int(parts[1]), *parts[2:])
    except (IndexError, ValueError):
        return (name,)


def ensure_logiceval_cwd():
    for d in ('LogicDS', 'Codes'):
        if not os.path.isdir(d):
            raise RuntimeError(
                f"Expected to be run from LogicEval/ (looking for ./{d}). cwd={os.getcwd()}"
            )
