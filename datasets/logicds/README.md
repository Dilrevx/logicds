# LogicDS normalized inventory

This directory adapts the original dataset for GCA/embed benchmark loading.
Original files remain unchanged under `LogicDS/`, including the author's
annotations, buggy/fixed samples, scripts and documentation.

| File | Role |
| --- | --- |
| `cases.jsonl` | 122 normalized records; source requests separated from evaluator-only annotations |
| `source.json` | Original commit, raw input hashes and transformation provenance |
| `summary.json` | Inventory counts and data preparation status, not experimental results |

There are **61 real cases** (53 CVE records and 8 srsRAN research cases, 28
author-provided project names) and **61 paired synthetic Java cases**. Each real/synthetic pair belongs
to one case group. Report the two partitions separately. The original task is
automated repair; retrieval relevance and static audit findings are different
evaluation tasks.

`real_projects` counts the author's `project_name` labels, not an independently
deduplicated repository inventory. Repository aliases may refer to one project;
sample 30's author label says `dnsmasq` although its download script and source path
refer to ProFTPD. The original metadata is preserved rather than silently corrected.

The real source requests preserve literal commits from the author's download
scripts. A short commit must be resolved to a full SHA with an acquisition receipt
before use; normalization does not substitute a branch tip. Three fixed-source
scripts provide no commit, so their fixed versions remain unresolved. Their buggy
source requests are present and this does not remove cases from recall evaluation.

Normalization does not execute target code, download/build scripts, or PoCs.
Author-provided locations, patches, descriptions and specifications are
evaluator-only, not retrieval queries or audit prompts. A fixed sample is not
certified globally vulnerability-free.

To regenerate or verify, use the adapter in the embed checkout:

```sh
cd /path/to/embed
PYTHONPATH=src python3 -m embedbench.logicds normalize \
  --benchmark-checkout /path/to/logicds
PYTHONPATH=src python3 -m embedbench.logicds normalize \
  --benchmark-checkout /path/to/logicds --verify
```

The embed lock pins this fork's adaptation commit and all three derived file
hashes. Loading independently compares consumed raw files and embedded source
trees with original upstream commit `349b8ea75b7841bfe6bd82c50b34d6210d027442`.
Model runs and their results belong in embed, not this data repository.

Upstream: [SyNSec-den/LogicEval](https://github.com/SyNSec-den/LogicEval).
The root MIT license and bundled target-source licenses are retained; the fork
does not relicense third-party code.
