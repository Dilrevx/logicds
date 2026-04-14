#!/bin/bash

set -euo pipefail

CUR_DIR="$(pwd)"

echo "Running CVE-2022-25640 test: ..."
# First clear previous output
rm -f "$CUR_DIR/make_test_output.txt"

IMAGE_NAME="a12"
CONTAINER_NAME="a12"

rm -f make_test_output.txt


echo "Starting the docker..."
docker start "$CONTAINER_NAME" >/dev/null 2>&1 || {
    echo "Error: Failed to start Docker container '$CONTAINER_NAME'."
    echo "Error: Test failed." > "${CUR_DIR}/make_test_output.txt"
    exit 1
}

echo "Starting the test..."
docker exec -it "$CONTAINER_NAME" bash -c "
  chmod +x /workspace/test.py && /workspace/test.py
" > make_test_output.txt 2>&1 || {
    echo "Error: Test execution failed."
    echo "Error: Test failed." > "${CUR_DIR}/make_test_output.txt"
    exit 1
}


# Check if the test output file contains success message
if [ -f "${CUR_DIR}/make_test_output.txt" ] && grep -q "PASS" "${CUR_DIR}/make_test_output.txt"; then
    echo "CVE-2022-25640 test completed successfully."
    echo "Patch verification: PASSED ✓"
    echo "Test log: ${CUR_DIR}/make_test_output.txt"
    exit 0
else
    echo "Error: Test failed. Check ${CUR_DIR}/make_test_output.txt for details."
    echo "Patch verification: FAILED ✗"
    exit 1
fi