import fcntl
import json
import os


def merge_into_eval_json(results_path: str, new_fields: dict) -> None:
    dirpath = os.path.dirname(results_path)
    if dirpath:
        os.makedirs(dirpath, exist_ok=True)
    with open(results_path, 'a+') as f:
        fcntl.flock(f.fileno(), fcntl.LOCK_EX)
        try:
            f.seek(0)
            content = f.read()
            existing = json.loads(content) if content.strip() else {}
            existing.update(new_fields)
            f.seek(0)
            f.truncate()
            json.dump(existing, f, indent=2)
            f.flush()
            os.fsync(f.fileno())
        finally:
            fcntl.flock(f.fileno(), fcntl.LOCK_UN)
