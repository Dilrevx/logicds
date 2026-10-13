#!/bin/bash
set -e
set -u

CUR_DIR="$(cd "$(dirname "$0")" && pwd)"

# TODO: Update compile command for spnego-http-auth-nginx-module
# Common patterns:
#   autotools: ./autogen.sh && ./configure && make -j$(nproc)
#   cmake:     mkdir build && cd build && cmake .. && make -j$(nproc)
#   make:      make -j$(nproc)

cd "$CUR_DIR"
make -j"$(nproc)" 2>&1 | tee -a "${CUR_DIR}/make_output.txt"
