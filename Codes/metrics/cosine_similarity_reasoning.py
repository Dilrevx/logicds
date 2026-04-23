import math
from collections import Counter

METRIC_NAME = 'reasoning_cosine_similarity'
NEEDS_LLM_JUDGE = False

_model = None


def _get_st_model():
    global _model
    if _model is not None:
        return _model
    try:
        from sentence_transformers import SentenceTransformer
        _model = SentenceTransformer('all-MiniLM-L6-v2')
        return _model
    except Exception:
        return None


def _bag_cosine(a: str, b: str) -> float:
    va, vb = Counter(a.split()), Counter(b.split())
    if not va or not vb:
        return 0.0
    keys = set(va) | set(vb)
    dot = sum(va[k] * vb[k] for k in keys)
    na = math.sqrt(sum(v * v for v in va.values()))
    nb = math.sqrt(sum(v * v for v in vb.values()))
    return dot / (na * nb) if na and nb else 0.0


def compute(reference: str, candidate: str, ctx: dict) -> dict:
    gt = ctx.get('gt_reasoning')
    cand_r = ctx.get('candidate_reasoning')
    if not gt or not cand_r:
        return {}
    model = _get_st_model()
    if model is None:
        return {METRIC_NAME: _bag_cosine(gt, cand_r)}
    ea = model.encode(gt, convert_to_numpy=True)
    eb = model.encode(cand_r, convert_to_numpy=True)
    dot = float((ea * eb).sum())
    na = float((ea * ea).sum()) ** 0.5
    nb = float((eb * eb).sum()) ** 0.5
    return {METRIC_NAME: dot / (na * nb) if na and nb else 0.0}
