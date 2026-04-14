#!/usr/bin/python3
import subprocess
import time
import os

# Directory where scripts are located
BASE_DIR = "/5GTest/5G_test"

def run_script(script_path, outfile_name: str):
    """Run a script directly"""
    full_path = os.path.join(BASE_DIR, script_path)
    return subprocess.Popen(f"cd {BASE_DIR} && {script_path} > /5GTest/{outfile_name} 2>&1", shell=True)
    # return subprocess.Popen(f"cd {BASE_DIR} && {script_path}", shell=True)

def reset_and_insert(file_path, line):
    """
    Truncate the given file and insert a single line into it.
    """
    # Ensure the directory exists
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    # Open in write mode to delete all existing content, then write the new line
    with open(file_path, 'w') as f:
        f.write(line + "\n")

# Starting scripts
print("Starting 5G test environment...")

log_path = os.path.join(BASE_DIR, "DIKEUE_Log_executor", "result.log")
os.system(f"rm -f {log_path}")

# Before starting the target, reset the input file and insert the desired line
input_filepath = os.path.join(BASE_DIR, "DIKEUE_Log_executor", "input")
insert_line = "INFO: [enable_s1 auth_request_plain_text nas_sm_cmd id_request_plain_text/ attach_request1]"
input_file = open(input_filepath, 'w')
input_file.write(insert_line + "\n")
input_file.close()
# reset_and_insert(input_file, insert_line)

run_script("./script/start_o5gs.sh", "o5gs_output.log")
time.sleep(4)

run_script("./script/start_srs_gnb.sh", "srs_gnb_output.log")
time.sleep(4)

run_script("./script/run_log_executor.sh", "log_executor_output.log")
time.sleep(8)



# Now start the target
run_script("./script/start_target.sh", "target_output.log")

time.sleep(10)

while not os.path.exists(log_path):
    print("Waiting for log file to be created...")
    time.sleep(2)

try:
    with open(log_path, 'r') as log_file:
        log_content = log_file.read()
        if "FAIL" in log_content:
            print("TEST FAIL")
        else:
            print("TEST PASS")
except FileNotFoundError:
    print(f"Error: Log file not found at {log_path}")
except Exception as e:
    print(f"Error reading log file: {e}")
