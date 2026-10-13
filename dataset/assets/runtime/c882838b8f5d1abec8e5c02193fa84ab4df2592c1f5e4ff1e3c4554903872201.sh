#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status
set -u  # Treat unset variables as an error

# Get absolute path to the project root (even inside Docker)
export CUR_DIR="$(cd "$(dirname "$0")" && pwd)"

# Ensure build directory exists (may be a mounted volume)
BUILD_DIR="${CUR_DIR}/build"
mkdir -p "${BUILD_DIR}"
cd "${BUILD_DIR}"

echo "Running cmake"
cmake ../

echo "Building srsRAN..."
make -j"$(nproc)" 2>&1 | tee -a "${CUR_DIR}/make_output.txt" || {
    echo "Error: Build failed. Check ${CUR_DIR}/make_output.txt for details."
    exit 1
}


echo "Build log: ${CUR_DIR}/make_output.txt"

