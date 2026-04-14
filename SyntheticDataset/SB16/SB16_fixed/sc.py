import sys
import shutil
from pathlib import Path
import re

def replace_in_file(path, substitutions, verbose=False):
    text = path.read_text(encoding='utf-8')
    new_text = text
    for old, new in substitutions.items():
        if old in new_text:
            for line in new_text.splitlines():
                if old in line:
                    if verbose:
                        print(f"[{path}] Replacing '{old}' -> '{new}' in line: {line.strip()}")
            new_text = new_text.replace(old, new)
    if new_text != text:
        path.write_text(new_text, encoding='utf-8')

def process_mode(mode, project_root, verbose=False):
    src = project_root / 'src'
    buggy = src / 'buggy'
    fixed = src / 'fixed'
    main_dir = src / 'main'

    if mode == 'b':
        if fixed.exists():
            print(f"Deleting fixed directory: {fixed}")
            shutil.rmtree(fixed)
        else:
            print(f"No fixed directory to delete: {fixed}")
        if buggy.exists():
            print(f"Renaming buggy -> main: {buggy} -> {main_dir}")
            buggy.rename(main_dir)
        else:
            print(f"Buggy directory not found: {buggy}")
        subs = {'buggy.': 'main.', 'buggy;' : 'main;', 'fixed.': 'main.', 'fixed;' : 'main;'}
    elif mode == 'f':
        if buggy.exists():
            print(f"Deleting buggy directory: {buggy}")
            shutil.rmtree(buggy)
        else:
            print(f"No buggy directory to delete: {buggy}")
        if fixed.exists():
            print(f"Renaming fixed -> main: {fixed} -> {main_dir}")
            fixed.rename(main_dir)
        else:
            print(f"Fixed directory not found: {fixed}")
        subs = {'buggy.': 'main.', 'buggy;' : 'main;', 'fixed.': 'main.', 'fixed;' : 'main;'}
    else:
        print("Invalid mode! Use 'b' or 'f'.")
        sys.exit(1)

    for folder in [main_dir, project_root / 'test']:
        if folder.exists():
            for path in folder.rglob('*.java'):
                if verbose:
                    print(f"Processing Java file: {path}")
                replace_in_file(path, subs, verbose)
        else:
            print(f"Directory not found: {folder}")

def process_scripts(y_value, project_root, verbose=False):
    # Prepare case-sensitive substitutions for scripts
    y_upper = y_value.upper()
    subs = {
        'sa3': f's{y_value}',
        'SA3': f'S{y_upper}',
        'a3': y_value,
        'A3': y_upper,
    }
    manual = project_root.parent / 'manual_inputs'

    for base in [project_root, manual]:
        if base.exists():
            for path in base.rglob('*.sh'):
                if verbose:
                    print(f"Processing script for replacement: {path}")
                replace_in_file(path, subs, verbose)
        else:
            if verbose:
                print(f"Directory not found: {base}")

def process_tests(project_root, verbose=False):
    test_dir = project_root / 'test'
    java_files = [p for p in test_dir.glob('*.java')
                  if p.name not in ('AllTestsRunner.java', 'SingleJUnitTestRunner.java')]
    if len(java_files) != 1:
        print(f"Expected one test Java file, found {len(java_files)}: {java_files}")
        sys.exit(1)
    test_file = java_files[0]
    class_name = test_file.stem
    if verbose:
        print(f"Found test file: {test_file}, class name: {class_name}")

    content = test_file.read_text(encoding='utf-8').splitlines()
    test_name = None
    for i, line in enumerate(content):
        if '@Test' in line:
            for j in range(i+1, len(content)):
                match = re.search(r'public void ([^(]+)\(', content[j])
                if match:
                    test_name = match.group(1)
                    break
            if test_name:
                break
    if not test_name:
        print(f"No test method found in {test_file}")
        sys.exit(1)
    if verbose:
        print(f"First test method: {test_name}")

    manual = project_root.parent / 'manual_inputs'
    bases = [project_root, manual]

    # Replace class identifier and test method in all .sh scripts
    subs1 = {'CipherInitializerTest': class_name}
    subs2 = {'testEvpCipherInitInternal_CipherProvNotNull': test_name}
    for base in bases:
        if base.exists():
            for path in base.rglob('*.sh'):
                if verbose:
                    print(f"Updating script: {path}")
                replace_in_file(path, subs1, verbose)
                replace_in_file(path, subs2, verbose)
        else:
            if verbose:
                print(f"Directory not found: {base}")


def main():
    if len(sys.argv) != 3:
        print("Usage: python swap_and_patch.py <b|f> <y_value> e.g. 'b20'")
        sys.exit(1)

    mode = sys.argv[1]
    y_value = sys.argv[2]
    project_root = Path(__file__).parent.resolve()

    print(f"Starting with mode='{mode}', y_value='{y_value}' at: {project_root}")
    process_mode(mode, project_root, verbose=True)
    process_scripts(y_value, project_root, verbose=True)
    process_tests(project_root, verbose=True)
    print("All done.")

if __name__ == '__main__':
    main()

