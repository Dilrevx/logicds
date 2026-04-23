# LogicEval Codes

Self-contained Python scripts that implement the LogicEval pipeline end-to-end. The pipeline is six steps; each step is idempotent and independently re-runnable.

## Pipeline flow

```
      LogicDS/<kind>/sample_X/                                (inputs to 01 — manual author)
              │
              ▼
    [01_generate_code_snippets.py]
              │   writes buggy/fixed source + function + block .txt files
              ▼
      LogicDS/<kind>/sample_X/
              │
              ▼
     [02_generate_prompts.py]
              │   renders P1..P21 prompt templates
              ▼
   LLM_experiments/prompts/<kind>/sample_X/  (prompt_P1.txt..P21.txt, prompt_P7/, prompt_P8/, meta_info.json)
              │
              ▼
[03_generate_responses_openai.py]   [03_generate_responses_llama.py]   [03_generate_responses_qwen.py] (or your own repair framework)
              │                              │                                │
              ▼                              ▼                                ▼
   LLM_experiments/responses/<llm>/<kind>/sample_X/  (prompt_P<n>.txt + _reasoning.txt [+ CoT dirs])
              │
              ▼
     [04_generate_patches.py]
              │   extracts <repair> from responses, grafts into fixed source
              ▼
   LLM_experiments/patches/<kind>/sample_X/llms/<llm>/patch_P<n>.txt
              │
              ├──────────────────────────────┬──────────────────────────┐
              ▼                              ▼                          ▼
   [05_evaluate_nlp.py]           [06_evaluate_compile_test.py]       (rerun)
              │                              │
              ▼                              ▼
        LLM_experiments/evaluation/<kind>/sample_X/… /<patch>_evaluation.json
```

## Common flags (every script)

- `--sample-filter sample_1,sample_44` — restrict to specific samples
- `--kind real|synthetic|both` (default `both`)
- `--overwrite` — regenerate outputs even if they already exist

## Step-by-step

### 01 — Generate code snippets from manual annotations

**Script**: `01_generate_code_snippets.py`

**Inputs** (per sample): `LogicDS/<kind>/sample_X/`
- `inputConfig.json` (line ranges + source file path)
- `downloadBuggy.sh`, `downloadFixed.sh` (user-authored)

**Outputs** (overwritten in the same sample dir):
- `buggy_source_code_file.txt` — full text of the buggy source file
- `buggy_function.txt` — the vulnerable function extracted by line range
- `buggy_block.txt` — the vulnerable block
- `fixed_source_code_file.txt`, `fixed_function.txt`, `fixed_block.txt` — counterparts from the fixed source

**Example**:
```bash
python3 Codes/01_generate_code_snippets.py --sample-filter sample_1 --kind real --overwrite
```

---

### 02 — Generate P1..P21 prompt templates

**Script**: `02_generate_prompts.py`

**Inputs** (per sample): `LogicDS/<kind>/sample_X/`
- `inputConfig.json`, `vulnerability_description.txt`
- The 6 code-snippet files from step 01
- Optional: `specification.txt`, `repair_description.txt`, `context_codes.txt`, `initial_block.txt`

**Outputs**: `LLM_experiments/prompts/<kind>/sample_X/`
- `prompt_P1.txt` … `prompt_P21.txt` — one file per paper prompt (single-turn)
- `prompt_P7/prompt_P7_1.txt`, `prompt_P7_2.txt` — Chain-of-Thought turns for P7
- `prompt_P8/prompt_P8_1.txt`, `prompt_P8_2.txt` — CoT turns for P8
- `meta_info.json` — for each P<n>: `{ "buggy_code_type": "block"|"function", "token_count": int }`

**Extra CLI**:
- `--fewshot-sample sample_6` — which sample's `fixed_block` is used as the P6 few-shot example (default `sample_6`; if the current sample is the same as the fewshot sample, falls back to `sample_4`).

**Example**:
```bash
python3 Codes/02_generate_prompts.py --sample-filter sample_1 --kind real --overwrite
```

---

### 03 — Generate LLM responses

Three sibling scripts provided, one per LLM.

**Inputs**: `LLM_experiments/prompts/<kind>/sample_X/prompt_P<n>.txt` (or `prompt_P<n>/` CoT dir)

**Outputs**: `LLM_experiments/responses/<llm>/<kind>/sample_X/`
- `prompt_P<n>.txt` — the final LLM response (after the full turn sequence)
- `prompt_P<n>_reasoning.txt` — a follow-up explanation call ("explain the reasoning in 500 words")
- For CoT prompts, each turn's raw response is additionally saved under `prompt_P<n>/prompt_P<n>_<k>.txt`

#### `03_generate_responses_openai.py`
- Model: `o3-mini`
- Skips P2 and P3 (we cannot control temperature fo openai-03-mini)
- Reads `openai_key` from `api_config.json` at LogicEval root, or `OPENAI_API_KEY` env var

#### `03_generate_responses_llama.py`
- Model: `meta-llama/Meta-Llama-3.1-70B-Instruct`
- Reads `hf_token` from `api_config.json` or `HF_TOKEN` env var

#### `03_generate_responses_qwen.py`
- Model: `Qwen/Qwen2.5-Coder-32B-Instruct`

**Example**:
```bash
python3 Codes/03_generate_responses_openai.py --sample-filter sample_1 --kind real
```

---

### 04 — Extract and graft patches

**Script**: `04_generate_patches.py`

**Inputs**:
- `LLM_experiments/responses/<llm>/<kind>/sample_X/prompt_P<n>.txt`
- `LogicDS/<kind>/sample_X/{inputConfig.json, fixed_source_code_file.txt}`
- `LLM_experiments/prompts/<kind>/sample_X/meta_info.json` (for `buggy_code_type` lookup)

**Outputs**: `LLM_experiments/patches/<kind>/sample_X/llms/<llm>/`
- `patch_P<n>.txt` — full-source patch: `fixed_source_code_file.txt` with the line range replaced by the content of `<repair>…</repair>` extracted from the response.
- `patch_P<n>_reasoning.txt` — natural-language reasoning for the patch, copied from `prompt_P<n>_reasoning.txt` in the responses dir (written only when that file exists). Picked up by reasoning-dependent metrics in 05 via the sibling-file convention (see below).

**Extra CLI**:
- `--llm llama|qwen|openai|all` (required) — which LLM to graft from
- `--patches-dir PATH` — output root (default `LLM_experiments/patches/`)
- `--code-type block|function` — fallback when `meta_info.json` doesn't resolve P<n> (default `block`)

**Example**:
```bash
python3 Codes/04_generate_patches.py --llm all --sample-filter sample_1
```

---

### 05 — NLP evaluation (pluggable metrics)

**Script**: `05_evaluate_nlp.py`

**Inputs**:
- `<patches_root>/<kind>_samples/sample_X/[<any_subpath>]/<patch_file>.txt` (default root `LLM_experiments/patches/`; user-overridable)
- `LogicDS/<kind>_samples/sample_X/fixed_block.txt` or `fixed_function.txt` (reference, selected via meta_info)
- (Reasoning-dependent metrics) `<patch_file>_reasoning.txt` — a plain-text file **sibling** to each `<patch_file>.txt`, same directory, same stem, suffixed with `_reasoning.txt`. If absent, the patch is silently skipped by reasoning metrics. Files matching `*_reasoning.txt` are not themselves scored as patches.

**Outputs**:
- `LLM_experiments/evaluation/<kind>_samples/sample_X/[<any_subpath>]/<patch_file>_evaluation.json` — mirror path; each metric merged via fcntl-locked read-modify-write (safe to run concurrently with `06`).
- For judge metrics, GT reasoning is generated once per judge per sample and cached at `LLM_experiments/evaluation/<kind>_samples/sample_X/gt_reasoning_<judge>.txt`.

**Available metrics** (auto-discovered from `Codes/metrics/*.py`):

| Metric | Requires LLM | Description |
|---|---|---|
| `rouge_l` | — | ROUGE-L F1 vs reference (stdlib fallback) |
| `patch_cosine_similarity` | — | Sentence-BERT cosine between patch and reference (bag-of-words fallback) |
| `reasoning_cosine_similarity` | — | Cosine between candidate and GT reasoning (GT generated once per sample by openai) |
| `judge_openai` | OpenAI API | Binary similarity judgment |
| `judge_llama` | GPU (llama) | Binary similarity judgment |
| `judge_qwen` | GPU (qwen) | Binary similarity judgment |

**Extra CLI**:
- `--patches-dir PATH` (default `LLM_experiments/patches/`) — root to walk
- `--metrics rouge_l,patch_cosine_similarity` or `--metrics all` (default: `rouge_l,patch_cosine_similarity`)
- `--code-type block|function` — fallback when meta_info can't resolve P<n> (default `block`)

**Adding a new metric**: drop a new `.py` in `Codes/metrics/` with this shape:
```python
METRIC_NAME = "my_metric"
NEEDS_LLM_JUDGE = False
def compute(reference: str, candidate: str, ctx: dict) -> dict:
    return {METRIC_NAME: <value>}
```
Auto-discovered on next run; enable via `--metrics my_metric`.

**Example**:
```bash
# Smoke test with no LLM — quick
python3 Codes/05_evaluate_nlp.py --patches-dir Patches --sample-filter sample_1 \
    --metrics rouge_l,patch_cosine_similarity

# Full suite including LLM judges (costs API tokens and/or GPU time)
python3 Codes/05_evaluate_nlp.py --sample-filter sample_1 --metrics all
```

---

### 06 — Compile + test evaluation

**Script**: `06_evaluate_compile_test.py`

**Inputs**:
- `<patches_root>/<kind>_samples/sample_X/[<any_subpath>]/<patch_file>.txt`
- `LogicDS/<kind>_samples/sample_X/{inputConfig.json, downloadBuggy.sh, compile.sh, testRunSingle.sh, Dockerfile if present}`

**Outputs**: Merged into the **same** `_evaluation.json` files produced by 05 (via `_locked_merge.py`) with fields:
- `Compilation`: `"Success"` | `"Fail"` | `"NotRun"`
- `TestSingle`: `"Pass"` | `"Fail"` | `"NA"`
- `TestAll`: `"Pass"` | `"Fail"` | `"NA"`
- `reason`: (only when not run — e.g. `"docker-not-available"`)

**Pipeline** (per patch):
1. `bash downloadFixed.sh ./_scratch_projects` — fresh source tree
2. Overwrite `<vul_code_file_rel_path>` with the patch text
3. `bash compile.sh` (or `autoCompile.sh` for synthetic) — parse output for error markers
4. If compile succeeds and a `testRunSingle.sh` exists, run it — parse for pass/fail markers
5. Merge result into eval JSON

**Extra CLI**:
- `--patches-dir PATH` (same as 05)

**Example**:
```bash
python3 Codes/06_evaluate_compile_test.py --patches-dir Patches --sample-filter sample_1
```

---

## Evaluating patches from an external repair framework

Steps 05 and 06 work on **any** patches root — they don't require the patches to have been produced by this pipeline's 04. To evaluate patches generated by your own tool:

1. **Lay the patches out** under a root directory (any name and location within `LogicEval/`, e.g. `my_tool_patches/`) using the same sample-addressed structure as `Patches/`:

   ```
   my_tool_patches/
   ├── real_samples/
   │   ├── sample_1/
   │   │   └── <any_subdirs>/<patch_name>.txt      # e.g. mytool/patch_1.txt, mytool/run_A/patch_a.txt
   │   └── sample_2/
   │       └── …
   └── synthetic_samples/
       └── sample_X_synthetic/
           └── …
   ```

   The structure under each `sample_X/` may be arbitrary depth — 05 and 06 recursively find every `.txt` file. Patch files can contain either:
   - A raw code block (treated as-is as the candidate), or
   - Text with a `<repair>…</repair>` tag (the scripts extract the tag contents as the candidate).

   **(Optional) Reasoning files.** To score reasoning-dependent metrics (`reasoning_cosine_similarity`, `judge_*`), drop a plain-text sibling next to each patch with the same stem and a `_reasoning.txt` suffix — e.g. `mytool/patch_1_reasoning.txt` next to `mytool/patch_1.txt`. 05 looks these up by filename convention; patches without a sibling are silently skipped by reasoning metrics. Files ending in `_reasoning.txt` are not themselves treated as patches.

2. **Run the NLP evaluation** on your patches root:

   ```bash
   cd LogicEval
   python3 Codes/05_evaluate_nlp.py --patches-dir my_tool_patches \
       --metrics rouge_l,patch_cosine_similarity,reasoning_cosine_similarity
   ```

   Pass `--metrics all` to include LLM-judge metrics, or pick a subset. If a patch has no sibling `<patch>_reasoning.txt`, reasoning-dependent metrics are skipped gracefully for that patch.

3. **Run the compile+test evaluation** (requires Docker and the sample's `compile.sh` / `testRunSingle.sh`):

   ```bash
   python3 Codes/06_evaluate_compile_test.py --patches-dir my_tool_patches
   ```

4. **Inspect the results.** For each patch `my_tool_patches/<kind>_samples/sample_X/<subpath>/<patch>.txt`, the merged evaluation JSON is written to the mirror path:

   ```
   LLM_experiments/evaluation/<kind>_samples/sample_X/<subpath>/<patch>_evaluation.json
   ```

   Each JSON holds the union of fields from every metric that ran, plus `Compilation` / `TestSingle` / `TestAll` from 06:

   ```json
   {
     "rouge_l": 0.42,
     "patch_cosine_similarity": 0.73,
     "reasoning_similar_judge_openai": true,
     "Compilation": "Success",
     "TestSingle": "Pass",
     "TestAll": "Pass"
   }
   ```

5. **Reference selection.** For each patch, 05 picks the reference text from `LogicDS/<kind>_samples/sample_X/fixed_block.txt` by default. If your framework produces function-level patches, pass `--code-type function` to use `fixed_function.txt` instead. If individual patches mix block-level and function-level, encode the choice in the patch's subpath using the same `llms/<llm>/patch_P<n>.txt` convention — 05 will read `meta_info.json` to pick the right reference per patch.

---

## Adding a new evaluation or reasoning metric

Metrics live in `Codes/metrics/` as one-file modules. The runner (`05_evaluate_nlp.py`) auto-discovers every `.py` file in that directory, so adding a metric is a drop-in.

### 1. Create `Codes/metrics/<my_metric>.py`

Each module must define three symbols:

```python
# Codes/metrics/bleu.py
METRIC_NAME = "bleu"          # field name(s) prefix used in the eval JSON + --metrics CLI
NEEDS_LLM_JUDGE = False       # True if the metric calls an LLM (informational)

def compute(reference: str, candidate: str, ctx: dict) -> dict:
    """
    reference  — GT text (usually fixed_block or fixed_function)
    candidate  — the patch text (with <repair> extracted if present)
    ctx        — dict with extras: sample_dir, kind, sample, patch_path, rel,
                 llm, pid, code_type.
                 For reasoning metrics, also gt_reasoning and candidate_reasoning.
    Returns    — {field_name: value} to merge into <patch>_evaluation.json.
                 Return an empty dict {} to skip (e.g. when required input missing).
    """
    from some_bleu_lib import sentence_bleu
    return {"bleu": sentence_bleu(reference, candidate)}
```

### 2. (If it's a reasoning metric) use `ctx['gt_reasoning']` + `ctx['candidate_reasoning']`

The runner populates these automatically for every sample the first time a reasoning metric is requested. GT reasoning is generated once per judge LLM and cached at `LLM_experiments/evaluation/<kind>_samples/sample_X/gt_reasoning_<judge>.txt`. Candidate reasoning is read from a sibling file next to the patch: for a patch at `<…>/<stem>.txt`, 05 looks up `<…>/<stem>_reasoning.txt` in the same directory.

If either is empty, return `{}` so the metric is silently skipped for that patch (baseline-tool patches typically have no matching reasoning file).

```python
# Codes/metrics/reasoning_overlap.py
METRIC_NAME = "reasoning_overlap"
NEEDS_LLM_JUDGE = False

def compute(reference, candidate, ctx):
    gt = ctx.get("gt_reasoning", "")
    cand = ctx.get("candidate_reasoning", "")
    if not gt or not cand:
        return {}
    overlap = len(set(gt.split()) & set(cand.split())) / max(len(set(gt.split())), 1)
    return {"reasoning_overlap": overlap}
```

### 3. (If it's a judge metric that calls an LLM) import the LLM helper lazily

Import inside `compute()` so users who don't enable the metric don't pay the import cost:

```python
# Codes/metrics/judge_my_llm.py
METRIC_NAME = "judge_my_llm"
NEEDS_LLM_JUDGE = True

def compute(reference, candidate, ctx):
    gt = ctx.get("gt_reasoning", "")
    cand = ctx.get("candidate_reasoning", "")
    if not gt or not cand:
        return {}
    import sys, os
    sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    from interact_with_my_llm import evaluate_reasoning
    return {"reasoning_similar_judge_my_llm": bool(evaluate_reasoning(gt, cand))}
```

The runner uses `METRIC_NAME` as the prefix when deciding whether to skip the metric on re-runs (it checks whether any key starting with that prefix already exists in the eval JSON). Return richer field names that begin with `METRIC_NAME` (e.g. `reasoning_similar_judge_my_llm`, `bleu_score`, `bleu_brevity_penalty`) if you want multiple outputs per metric.

### 4. Run the pipeline with your metric enabled

```bash
python3 Codes/05_evaluate_nlp.py --patches-dir my_tool_patches \
    --metrics rouge_l,patch_cosine_similarity,bleu,reasoning_overlap,judge_my_llm
```

Or enable everything at once:

```bash
python3 Codes/05_evaluate_nlp.py --patches-dir my_tool_patches --metrics all
```

No changes to `05_evaluate_nlp.py` itself are required — the module drop-in is the whole integration.
