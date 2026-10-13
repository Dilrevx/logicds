# LogicDS Dataset

LogicDS contains 122 vulnerability samples organized into real and their corresponding synthetic vulnerabilities:

```
LogicDS/
├── real_samples/              # 61 real-world CVE samples (sample_1 … sample_61)
├── synthetic_samples/         # 61 synthetic Java mimics (sample_N_synthetic, 1:1 index with real)
└── _srsran_shared/            # shared source trees for the 8 srsRAN (5G) real samples
    ├── 5g_buggy/              # buggy srsRAN source tree
    └── 5g_fixed/              # fixed srsRAN source tree
```

The full project-to-sample inventory and sample-to-CVE mapping lives in [`SAMPLES.md`](SAMPLES.md).

Synthetic samples are self-contained Java 1.7 projects with JUnit 4.13.2 tests (to be compatible with the compared baselines) simplified mimics of the corresponding real-world logical vulnerability, designed to compile and run quickly inside Docker.

## Samples

The list of upstream projects, per-project sample assignment, and sample-to-CVE mapping are provided in **[SAMPLES.md](SAMPLES.md)**.

## Per-sample structure

### Real samples (`LogicDS/real_samples/sample_X/`)

| File | Description | Required |
|---|---|---|
| `inputConfig.json` | Metadata: language, `vul_code_file_rel_path`, line ranges for the buggy/fixed function + block, context lines, CVE description. | ✅ |
| `downloadBuggy.sh` | Shell script that clones or copies the buggy source tree into `./projects/`. Most real samples use `git clone` + `git checkout <commit>`; the 8 srsRAN samples copy from `_srsran_shared/5g_buggy/`. | ✅ |
| `downloadFixed.sh` | Same pattern, for the fixed source tree. | ✅ |
| `buggy_source_code_file.txt` | Full text of the vulnerable source file at the buggy commit. | ✅ (regenerable from `01_generate_code_snippets.py`) |
| `buggy_function.txt` | The vulnerable function extracted by the line range in `inputConfig.json`. | ✅ |
| `buggy_block.txt` | The vulnerable code block within that function. | ✅ |
| `fixed_source_code_file.txt` | Full source file at the fixed commit. | ✅ |
| `fixed_function.txt` | Fixed-version function. | ✅ |
| `fixed_block.txt` | Fixed-version block. | ✅ |
| `vulnerability_description.txt` | Description of the vulnerability (usually obtained from CVE description, stripped any version numbering or fix description). | ✅ |
| `specification.txt` | Relevant RFC / standard spec excerpt (if any) | Optional |
| `compile.sh` | Builds the downloaded source. Some invoke Docker (`docker build && docker run …`), some are native `cmake && make` / `./configure && make`. | ✅ |
| `testRunSingle.sh` | Runs the single regression test that distinguishes buggy vs fixed behaviour. Present for samples with upstream test suites. | Optional |
| `Dockerfile` | Present whenever `compile.sh` references `docker …`. | Optional, but preferable for portability |
| `repair_description.txt` | Plain-English description of the repair logic. | Optional |

### Synthetic samples (`LogicDS/synthetic_samples/sample_X_synthetic/`)

Each synthetic sample is a *self-contained* Java 1.7 project that mimics the real-world vulnerability structure without depending on external repositories.

```
sample_X_synthetic/
├── inputConfig.json            # same fields as real, but points into buggy_version/
├── downloadBuggy.sh            # copies buggy_version/ → ./projects
├── downloadFixed.sh            # copies fixed_version/ → ./projects
├── vulnerability_description.txt
├── specification.txt           # (optional) 1:1 with the real counterpart
├── buggy_function.txt, buggy_block.txt
├── fixed_function.txt, fixed_block.txt
├── buggy_version/              # complete Java project with the vulnerability
│   ├── src/{main,commons}/*.java
│   ├── test/*.java             # JUnit 4.13.2 tests
│   ├── compile.sh              # native javac + wget for JUnit+Hamcrest jars
│   ├── testRunSingle.sh, testRunAll.sh
│   ├── autoCompile.sh          # Docker wrapper (invariant: auto* ≡ Docker)
│   ├── autoTestRunSingle.sh, autoTestRunAll.sh
│   └── Dockerfile              # openjdk:7 base image
└── fixed_version/              # same layout, with the fix applied
```

**Invariants for synthetic samples:**
- `compile.sh`, `testRunSingle.sh`, `testRunAll.sh` → native (non-Docker) execution
- `autoCompile.sh`, `autoTestRunSingle.sh`, `autoTestRunAll.sh` → Docker-based (uses the sample's `Dockerfile`)
- Every synthetic sample ships its own `Dockerfile`
- Tests use JUnit 4.13.2 + Hamcrest 1.3, downloaded by `compile.sh` via `wget` from Maven Central

**Please note that the tests provided along with the synthetic examples are not complete nor sound, they are best effort mimic generation of the corresponding real cases to facilitate comparison with baseline tools that only work with java codes. Thus, and may still lead to false positives even if all tests render correct output. We are open to any extra suggestion or addition/modification of our tests for these samples which would make the testing more comprehensive.**

## srsRAN Samples

The 8 srsRAN samples (`sample_36` … `sample_43`) share the srsRAN 5G source trees (buggy + fixed). These samples are self-contained within LogicEval — no external downloads needed.

---

# Adding a new sample to LogicDS

The pipeline is designed so that dropping in a new manual-annotation directory plus a line-range `inputConfig.json` is enough to generate everything else. The steps below assume you are at `LogicEval/` cwd.

## 1. Choose the next slot

Pick the next free `sample_N` index. For real samples use `LogicDS/real_samples/sample_N/`; for synthetic use `LogicDS/synthetic_samples/sample_N_synthetic/`. If your synthetic is a 1:1 mimic of an existing real sample, match the indices (e.g. real `sample_62` ⇄ synth `sample_62_synthetic`).

## 2. Author the required manual inputs

Create the directory and populate these files (see existing samples for reference):

| File | What to put in it |
|---|---|
| `inputConfig.json` | Metadata — provide: `vul_id` (set to `"sample_N"` to match folder name), `project_name`, `language`, `vul_code_file_rel_path` (relative file path inside the cloned repo), the 4 line-range pairs for buggy+fixed function/block, `context_lines` (relevant code lines for context eg preamble code or function prologue), `vul_code_initial_block_start/_end` (function signature lines). Follow any existing `inputConfig.json` for the exact format. |
| `downloadBuggy.sh` | Script that places the buggy source at `${1:-./projects}/`. Typically `git clone <url> $1 && cd $1 && git checkout <buggy_commit>`. For synthetic, use `cp -r ${SCRIPT_DIR}/buggy_version/* $1/`. Make executable. |
| `downloadFixed.sh` | Same pattern for the fixed source. |
| `vulnerability_description.txt` |Description of the vulnerability (aim for ≤200 words). Used directly in prompt templates. If from CVE you can use CVE description, but strip any fix suggestion or version numbering for fairness.|
| `compile.sh` | Script that builds the code in `${1:-./projects}/`. |
| `testRunSingle.sh` | If available, script that executes functional and regression test(s) distinguishing buggy vs fixed behaviour. The pipeline's 06 compile-test script depends on this. |
| `Dockerfile` | Required if `compile.sh` / `testRunSingle.sh` invoke `docker …`. |
| `specification.txt` | (Optional) relevant RFC/spec excerpt. |

**Synthetic-specific additions.** If the new sample is synthetic, also populate `buggy_version/` and `fixed_version/` subdirs with the full Java project plus invariant `compile.sh`, `testRunSingle.sh`, `testRunAll.sh` (native), `autoCompile.sh`, `autoTestRunSingle.sh`, `autoTestRunAll.sh` (Docker), and `Dockerfile`. See `sample_1_synthetic/` for a minimal reference.

## 3. Generate the derived code snippets

Run [`Codes/01_generate_code_snippets.py`](../Codes/01_generate_code_snippets.py):

```bash
python3 Codes/01_generate_code_snippets.py --sample-filter sample_N --kind real
```

This will:
1. Invoke `downloadBuggy.sh` into a scratch dir
2. Read `inputConfig.json` line ranges
3. Write `buggy_source_code_file.txt`, `buggy_function.txt`, `buggy_block.txt` into the sample directory
4. Repeat for the fixed source → `fixed_*.txt`
5. Clean up the scratch dir

## 4. Verify the sample compiles and tests can run

```bash
# For synthetic — exercises the bundled Java project
cd LogicDS/synthetic_samples/sample_N_synthetic/buggy_version
bash compile.sh
bash testRunSingle.sh     # expects failure on the buggy side
cd ../fixed_version
bash compile.sh
bash testRunSingle.sh     # expects success

# For real — exercises the cloned upstream source
cd LogicDS/real_samples/sample_N
bash downloadBuggy.sh
bash compile.sh
bash testRunSingle.sh
```

## 5. Integrate with the pipeline

Once the sample is in place and `01` has populated the derived code snippets, the rest of the LogicEval pipeline picks it up automatically whenever `--sample-filter sample_N` is passed (or with no filter, to process all samples):

- [`Codes/02_generate_prompts.py`](../Codes/02_generate_prompts.py) — builds prompts
- [`Codes/03_generate_responses_{openai,llama,qwen}.py`](../Codes/) — calls the LLMs
- [`Codes/04_generate_patches.py`](../Codes/04_generate_patches.py) — extracts patches
- [`Codes/05_evaluate_nlp.py`](../Codes/05_evaluate_nlp.py) — computes NLP metrics
- [`Codes/06_evaluate_compile_test.py`](../Codes/06_evaluate_compile_test.py) — compiles and runs the regression test

See [`Codes/README.md`](../Codes/README.md) for full documentation of the codes.
