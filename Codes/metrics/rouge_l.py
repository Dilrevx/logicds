METRIC_NAME = 'rouge_l'
NEEDS_LLM_JUDGE = False


def compute(reference: str, candidate: str, ctx: dict) -> dict:
    try:
        from rouge_score import rouge_scorer
    except ImportError:
        return {'rouge_l': _fallback_rouge_l(reference, candidate)}
    scorer = rouge_scorer.RougeScorer(['rougeL'], use_stemmer=True)
    scores = scorer.score(reference, candidate)
    return {'rouge_l': float(scores['rougeL'].fmeasure)}


def _fallback_rouge_l(ref: str, cand: str) -> float:
    ref_tokens = ref.split()
    cand_tokens = cand.split()
    if not ref_tokens or not cand_tokens:
        return 0.0
    m, n = len(ref_tokens), len(cand_tokens)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if ref_tokens[i - 1] == cand_tokens[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1
            else:
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
    lcs = dp[m][n]
    if lcs == 0:
        return 0.0
    p = lcs / n
    r = lcs / m
    return 2 * p * r / (p + r)
