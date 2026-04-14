# LogicEval Pipeline and LogicDS Dataset

Code and dataset for the paper *LogicEval: A Systematic Framework for Evaluating Automated Repair
Techniques for Logical Vulnerabilities in Real-World Software*.
## Setup

```bash
# 1. Install dependencies
conda env create -f llm_env.yml
conda activate llm_env

# 2. Configure OpenAI key

# 3. Configure HuggingFace token (for llama/qwen local inference)
# Replace YOUR_HF_TOKEN_HERE in:
#   interact_with_llama.py, interact_with_qwen.py,
#   generate_prompt_results_llama.py, generate_prompt_results_qwen.py
```

All scripts must be run from the release root — paths are relative to that directory.

## Pipeline

Pick an LLM `{llama | openai | qwen}` and a vulnerability ID `<id>` (e.g. `A1`, `SB7`, `E3`). Run the 4 stages in order for each `(llm, id)` pair.

### LogicDS

The `manual_inputs/` folder contains the real-world samples of LogicDS.

| File | Purpose |
|---|---|
| `inputConfig.json` | LogicDS localization: buggy/fixed function & block line ranges, context lines, source-file rel-path |
| `vulnerability_description.txt` | CVE description; fed into prompts and LLM-judge |
| `specification.txt` | Relevant RFC / specification section (if any) |
| `downloadBuggy.sh` | Clones / copies the buggy project into `./projects/` |
| `downloadFixed.sh` | Clones / copies the fixed project into `./projects/` |
| `compile.sh` | Builds the project |
| `testRunSingle.sh` | Runs the single-test validator on the built project (if available) |
| `Dockerfile` | Containerized build environment (per-vulnerability, ubuntu-based) |

All stages read `inputConfig.json` + the download / compile / test scripts directly from here; no other source is consulted for localization or build recipes.

### Stage 1: Generate Prompts for Off-the-Shelf LLMs

```bash
python3 prepare_llm_prompts_<llm>.py <id>
```

Reads the staging files in `llm_outputs_<llm>/<id>/` and the manual inputs; writes prompts for experiment variants to `llm_outputs_<llm>/<id>/prompts/expt{1..5}/` (each containing `prompt_*.txt` and `meta_info.json`).

### Stage 2: Run LLM inference

```bash
python3 generate_prompt_results_<llm>.py <id>
```

Reads `llm_outputs_<llm>/<id>/prompts/expt*/` and calls the LLM. Writes raw completions to `llm_outputs_<llm>/<id>/prompt_responses/expt*/prompt_*.txt` (plus `_reasoning.txt` variants where applicable).

### Stage 3: Graft patches

```bash
python3 graft_patch.py <id> <llm>
```

Runs `manual_inputs/<id>/downloadFixed.sh` to clone the fixed project into `./projects/`, extracts `<repair>...</repair>` from each LLM response, and grafts it into the fixed file at the proper line range. Output: `llm_outputs_<llm>/<id>/suggested_patches/expt*/prompt_*.txt` : full patched source files.

### Stage 4: Evaluate

**Reasoning Metrics** (cosine, ROUGE-L, LLM-as-judge):

```bash
python3 nlp_evaluation_judge_<judge_llm>.py <id> <target_llm>
# Example: python3 nlp_evaluation_judge_openai.py A1 llama
#   -> openai acts as judge over patches produced by llama
```

Writes per-prompt JSONs under `llm_outputs_<target_llm>/<id>/evaluation_results/expt*/prompt_*.json` with fields like:
- `patch_cosine_similarity`, `patch_cosine_similarity_code`, `patch_rouge_l`
- `reasoning_cosine_similarity_judge_<llm>`, `reasoning_rouge_l_judge_<llm>`, `reasoning_similar_judge_<llm>`

Typically run the judge with each of the three LLMs (cross-judgement):

```bash
for judge in llama openai qwen; do
  python3 nlp_evaluation_judge_${judge}.py <id> <target_llm>
done
```

**Compilation and Testing Metrics** (compile + run tests in Docker):

```bash
python3 compile_and_test_patches.py <target_llm> <id> <skip_existing:true|false>
```

Adds `Compilation`, `TestSingle`, `TestAll`, `gt_fixed_compilation_success`, `gt_fixed_test_pass`, diff-count fields to the same result JSONs, plus writes `*_make_output.txt` and `*_test_output.txt` alongside.

### Final aggregation

```bash
python3 identify_plausible_patches.py <base_dir> <output_file>
# Example: python3 identify_plausible_patches.py llm_outputs_llama plausible_llama.json
```

Walks `<base_dir>/<id>/.../*.json` and writes a per-vulnerability summary JSON listing, for each `<id>`, which prompts had `Plausible`, `Plausible_Reasoning`, or `Compilation=Success` flags set.
### Full Example: Evaluate A1 with all 3 LLMs

```bash
for llm in llama openai qwen; do
  python3 prepare_llm_prompts_${llm}.py A1
  python3 generate_prompt_results_${llm}.py A1
  python3 graft_patch.py A1 ${llm}
  for judge in llama openai qwen; do
    python3 nlp_evaluation_judge_${judge}.py A1 ${llm}
  done
  python3 compile_and_test_patches.py ${llm} A1 false
done
python3 identify_plausible_patches.py llm_outputs_llama plausible_llama.json
python3 identify_plausible_patches.py llm_outputs_openai plausible_openai.json
python3 identify_plausible_patches.py llm_outputs_qwen plausible_qwen.json
```

## Mapping from Paper prompt IDs (P1–P21) to Experiment / Prompt file in Code

The paper refers to 21 distinct prompts `P1`–`P21`. Each maps to a specific `(experiment, prompt file)` under `llm_outputs_<llm>/<id>/{prompts,prompt_responses,suggested_patches,evaluation_results}/`:

| Paper Prompt ID | Experiment | Prompt file   |
|----------|-----------|---------------|
| P1       | expt2     | prompt_1      |
| P2       | expt2     | prompt_1_2    |
| P3       | expt2     | prompt_1_3    |
| P4       | expt2     | prompt_1_4    |
| P5       | expt3     | prompt_1      |
| P6       | expt3     | prompt_7      |
| P7       | expt3     | prompt_3      |
| P8       | expt3     | prompt_4      |
| P9       | expt4     | prompt_1      |
| P10      | expt4     | prompt_5      |
| P11      | expt4     | prompt_3      |
| P12      | expt5     | prompt_1      |
| P13      | expt5     | prompt_2      |
| P14      | expt5     | prompt_3      |
| P15      | expt5     | prompt_4      |
| P16      | expt5     | prompt_5      |
| P17      | expt1     | prompt_1      |
| P18      | expt1     | prompt_2      |
| P19      | expt1     | prompt_3      |
| P20      | expt1     | prompt_4      |
| P21      | expt1     | prompt_5      |


Example: `P5` refers to `llm_outputs_<llm>/<id>/prompts/expt3/prompt_1.txt` on the prompt side, and to `.../evaluation_results/expt3/prompt_1.json` on the results side.
