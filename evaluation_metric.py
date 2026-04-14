from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from rouge_score import rouge_scorer
from codebleu import calc_codebleu

_MODEL_CACHE = {}

def _get_model(model_name: str, max_seq_length: int = None) -> SentenceTransformer:
    if model_name not in _MODEL_CACHE:
        model = SentenceTransformer(model_name)
        if max_seq_length is not None:
            model.max_seq_length = max_seq_length
            model.tokenizer.model_max_length = max_seq_length
        _MODEL_CACHE[model_name] = model
    return _MODEL_CACHE[model_name]

def cosine_similarity_between_texts(text1: str, text2: str, model_name: str = "all-MiniLM-L6-v2") -> float:
    model = _get_model(model_name)

    embedding1 = model.encode(
        text1, convert_to_numpy=True, clean_up_tokenization_spaces=True
    )
    embedding2 = model.encode(
        text2, convert_to_numpy=True, clean_up_tokenization_spaces=True
    )

    similarity = cosine_similarity(embedding1.reshape(1, -1), embedding2.reshape(1, -1))[0][0]
    return similarity

def code_cosine_similarity_between_texts(text1: str, text2: str, model_name: str = "microsoft/unixcoder-base") -> float:
    model = _get_model(model_name, max_seq_length=510)

    embedding1 = model.encode(text1, convert_to_numpy=True)
    embedding2 = model.encode(text2, convert_to_numpy=True)

    similarity = cosine_similarity(embedding1.reshape(1, -1), embedding2.reshape(1, -1))[0][0]
    return float(similarity)

def rouge_l_score_between_texts(text1: str, text2: str) -> float:
    scorer = rouge_scorer.RougeScorer(['rougeL'], use_stemmer=True)
    scores = scorer.score(text1, text2)
    rouge_l_f1 = scores['rougeL'].fmeasure
    return rouge_l_f1

def codebleu_between_texts(text1: str, text2: str) -> float:
    result = calc_codebleu([text1], [text2], lang="python", weights=(0.25, 0.25, 0.25, 0.25), tokenizer=None)
    return result['codebleu']

