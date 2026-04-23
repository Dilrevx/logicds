#!/bin/bash

set -e

# -----------------------------------
# Initialize required submodules
# -----------------------------------
git config --global --add safe.directory /mbedtls
git submodule update --init --recursive


# -----------------------------------
# Create and enter build directory
# -----------------------------------
mkdir -p build
cd build

# -----------------------------------
# Configure with CMake
# -----------------------------------
cmake ..

# -----------------------------------
# Build the library
# -----------------------------------
make -j$(nproc)

