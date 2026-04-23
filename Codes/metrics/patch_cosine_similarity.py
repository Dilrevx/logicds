import math
from collections import Counter

METRIC_NAME = 'patch_cosine_similarity'
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
    va = Counter(a.split())
    vb = Counter(b.split())
    if not va or not vb:
        return 0.0
    keys = set(va) | set(vb)
    dot = sum(va[k] * vb[k] for k in keys)
    na = math.sqrt(sum(v * v for v in va.values()))
    nb = math.sqrt(sum(v * v for v in vb.values()))
    return dot / (na * nb) if na and nb else 0.0


def compute(reference: str, candidate: str, ctx: dict) -> dict:
    model = _get_st_model()
    if model is None:
        return {'patch_cosine_similarity': _bag_cosine(reference, candidate)}
    ea = model.encode(reference, convert_to_numpy=True)
    eb = model.encode(candidate, convert_to_numpy=True)
    dot = float((ea * eb).sum())
    na = float((ea * ea).sum()) ** 0.5
    nb = float((eb * eb).sum()) ** 0.5
    return {'patch_cosine_similarity': dot / (na * nb) if na and nb else 0.0}
