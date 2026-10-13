#!/usr/bin/env bash
set -euo pipefail

# CONFIGURATION
PROJECT_ID="5G"
BUG_ID="e8"
# FILEPATH_X="ssl/s3_srvr.c"

IMAGE_NAME="e8"
CONTAINER_NAME="e8"

# 1) Build the Docker image if it doesn't already exist
if ! docker image inspect "$IMAGE_NAME" > /dev/null 2>&1; then
  echo "Building Docker image '$IMAGE_NAME'..."
  docker build -t "$IMAGE_NAME" .
else
  echo "Docker image '$IMAGE_NAME' already exists."
fi

# 2) Remove any existing container with the same name
if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  echo "Removing existing container '$CONTAINER_NAME'..."
  docker stop "$CONTAINER_NAME" > /dev/null 2>&1 || true
  docker rm "$CONTAINER_NAME" > /dev/null 2>&1 || true
fi

# 3) Start a new disposable container
echo "Starting container '$CONTAINER_NAME'..."
docker run -v "$(pwd)":/app -d --privileged --name "$CONTAINER_NAME" "$IMAGE_NAME" tail -f /dev/null


# 5) Copy local test.py into the container’s bug directory

echo "Compiling srsran..."
docker exec "$CONTAINER_NAME" bash -c "
 rm -rf build && mkdir build && cd build && cmake .. && make -j\"$(nproc)\"
" > make_output.txt 2>&1

# 10) Cleanup
echo "Stopping and removing container..."
docker stop "$CONTAINER_NAME" > /dev/null 2>&1 || true
docker rm "$CONTAINER_NAME" > /dev/null 2>&1 || true

echo "Done."
