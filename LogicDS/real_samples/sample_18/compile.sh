#!/bin/bash

# Exit on error, undefined variables, and error in a pipeline
set -euo pipefail

# -------------------------------------------------------------------------
# Configurable variables for gnutls
# -------------------------------------------------------------------------
IMAGE_NAME="b4"
CONTAINER_NAME="b4"
HOST_VOLUME="$(pwd)/gnutls"   # Host directory to mount (should contain compile.sh)
CONTAINER_VOLUME="/gnutls"     # Container directory to mount to
WORKDIR_IN_CONTAINER="/gnutls"
COMPILE_SCRIPT="./compile_helper.sh"

# -------------------------------------------------------------------------
# Step 1: Build the Docker image
# -------------------------------------------------------------------------
echo "[+] Building Docker image '${IMAGE_NAME}'..."
docker build --force-rm -t "${IMAGE_NAME}" .

# -------------------------------------------------------------------------
# Step 2: Run the container with mounted volume and invoke the compile script
# -------------------------------------------------------------------------
echo "[+] Starting Docker container '${CONTAINER_NAME}' to compile the project..."
docker run -it --privileged \
    --name "${CONTAINER_NAME}" \
    -v "${HOST_VOLUME}:${CONTAINER_VOLUME}" \
    -w "${WORKDIR_IN_CONTAINER}" \
    "${IMAGE_NAME}" 
    # "${IMAGE_NAME}" \
    "${COMPILE_SCRIPT}"

echo "[✓] Compilation complete."
