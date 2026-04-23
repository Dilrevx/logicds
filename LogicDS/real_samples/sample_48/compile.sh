#!/bin/bash
set -e
set -u

CUR_DIR="$(cd "$(dirname "$0")" && pwd)"

# TODO: Update compile command for memos
cd "$CUR_DIR"
go build ./... 2>&1 | tee -a "${CUR_DIR}/make_output.txt"
