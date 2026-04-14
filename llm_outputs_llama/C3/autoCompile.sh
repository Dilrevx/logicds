#!/bin/bash

# Exit on error, undefined variables, and error in a pipeline
set -euo pipefail

# -------------------------------------------------------------------------
# Configurable variables for mosquitto
# -------------------------------------------------------------------------
IMAGE_NAME="c3"
CONTAINER_NAME="c3"
HOST_VOLUME="$(pwd)/mosquitto"   # Host directory to mount (should contain compile.sh)
CONTAINER_VOLUME="/mosquitto"     # Container directory to mount to
WORKDIR_IN_CONTAINER="/mosquitto"
COMPILE_SCRIPT="./compile.sh"

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
