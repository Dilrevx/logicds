METRIC_NAME = 'judge_openai'
NEEDS_LLM_JUDGE = True


def compute(reference: str, candidate: str, ctx: dict) -> dict:
    gt = ctx.get('gt_reasoning')
    cand_r = ctx.get('candidate_reasoning')
    if not gt or not cand_r:
        return {}
    import sys, os
    sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    try:
        from interact_with_openai import evaluate_reasoning_openai
    except ImportError as e:
        return {'reasoning_similar_judge_openai_error': str(e)}
    verdict = evaluate_reasoning_openai(gt, cand_r)
    return {'reasoning_similar_judge_openai': bool(verdict)}
