# LogicDS

122 records: 61 real cases and their 61 paired synthetic Java cases, grouped into 61 independent cases. Primary labels are the 122 core code blocks; function locations are auxiliary and are not unioned into core-block recall. The data are from Rashid et al., [LogicEval: A Systematic Framework for Evaluating Automated Repair Techniques for Logical Vulnerabilities in Real-World Software](https://aclanthology.org/2026.acl-long.2136/); this fork is a benchmark adaptation, not a repair-baseline reproduction.

## Data

- [`dataset/cases.jsonl`](dataset/cases.jsonl): the shared `bench-dataset-v1` records: identity/group, split, origin, language, identifiers, source versions, labels, patches, runtime references and provenance.
- [`dataset/manifest.json`](dataset/manifest.json): counts, original file receipts, asset hashes, modes and license associations.
- `dataset/assets/`: complete embedded source trees, original configurations, documents, tests, evidence and legal texts. Author repair configurations and answer locations are oracle material, not model inputs.

All 7,062 original files under `LogicDS/` are byte-preserved assets. External real-project source requests retain their original version declarations; three author fixed versions were not pinned and two declarations remain short hashes. Real and synthetic records must be evaluated separately, not counted as 122 independent real vulnerabilities.

Original files and the author's repair framework/results remain available at [the fixed pre-migration commit](https://github.com/Dilrevx/logicds/tree/43aea8b47db5132474d7a053aff276d30053b3ab). Original records and meaningful annotations are translated into the common cases, not used as an alternative runtime format. Models, experiments and results belong in [embed](https://github.com/Dilrevx/embed).

## Use

Use embed's [`benchmarks/logicds/dataset-lock.json`](https://github.com/Dilrevx/embed/blob/spec-audit-ab/benchmarks/logicds/dataset-lock.json) and [common runner](https://github.com/Dilrevx/embed/blob/spec-audit-ab/docs/method-runner.md). From the embed checkout:

```sh
PYTHONPATH=src python -m embedbench validate-dataset \
  --benchmark-checkout ../logicds \
  --lock benchmarks/logicds/dataset-lock.json
```

Validation checks all cases, references, asset bytes and file modes without executing targets. Labels are location/assessment evidence, not proof of exploitation; a fixed version is not a blanket safe-repository label. Missing original version bindings remain explicitly recorded.

## Rights

Original attribution and licensing boundaries are retained in the root legal files and `manifest.license`; nested legal texts are byte-preserved assets with their original scopes. No blanket relicensing of third-party target code or data is asserted.
