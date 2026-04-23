import os
import importlib
import pkgutil


def discover_metrics() -> dict:
    registry = {}
    pkg_path = os.path.dirname(__file__)
    for _, mod_name, _ in pkgutil.iter_modules([pkg_path]):
        if mod_name.startswith('_'):
            continue
        mod = importlib.import_module(f'metrics.{mod_name}')
        name = getattr(mod, 'METRIC_NAME', mod_name)
        registry[name] = mod
    return registry
