# Benchmark maintenance

- Preserve upstream raw files, embedded target sources, attribution and licenses.
  New normalized metadata belongs in `datasets/logicds/`.
- Keep real and paired synthetic cases separate. Never describe 122 records as
  122 independent real vulnerabilities.
- Author-provided patches, vulnerability descriptions and locations are
  evaluator-only. Do not turn them into retrieval queries or audit hints.
- Do not execute target downloads, builds, tests or exploits merely to inspect
  the dataset. Keep source acquisition and model evaluation receipts in embed.
- Regenerate derived files through the embed adapter; verify their original raw
  mapping and fixed hashes before updating the benchmark lock.
- A timeout, 403 or 429 alone is not proof that source acquisition is impossible.
  Record sanitized failures and bounded retries. For unclassified timeouts print
  `maybe due to quota, please wait`; do not expose credentials or rotate identities.
