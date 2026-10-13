#!/bin/bash
set -e
set -u

CUR_DIR="$(cd "$(dirname "$0")" && pwd)"

# TODO: Update install command for Radicale
cd "$CUR_DIR"
pip install -e . 2>&1 | tee -a "${CUR_DIR}/make_output.txt"
