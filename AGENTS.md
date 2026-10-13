# Dataset maintenance

- Keep `dataset/cases.jsonl` as the only case format (`bench-dataset-v1`); use the shared embed validator and pinned `benchmarks/logicds/dataset-lock.json`.
- Preserve identities, pairing, original split/origin, version roles, label granularity, meaningful facts, original-byte receipts and license scopes. Keep the 61 real cases and their 61 paired synthetic cases separate; primary code-block labels must not be broadened with auxiliary function labels.
- Keep answer locations, repair/configuration data and advisory evidence separate from model-facing inputs. Source versions are inert data; do not run targets, builds, containers or exploits while maintaining this dataset.
- Models, training, experiments, acquisition receipts and method results belong in embed, not this repository. Importers are migration-only, not runtime fallbacks.
- Changes must retain fixed original Git references and byte-preserved source/attribution/legal assets. Do not blanket-relicense third-party code or data or infer negative labels from unannotated code.
- Do not overwrite other sessions' files or original paper/release repositories. Validate all cases and assets before updating locks or publishing.
- A timeout or HTTP 403/429 alone does not establish permanent unavailability. Record sanitized failures and bounded retries; for an unclassified timeout print `maybe due to quota, please wait`. Do not expose credentials or rotate identities to evade restrictions.
