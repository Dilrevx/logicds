#!/usr/bin/env bash
set -euo pipefail

# CONFIGURATION
PROJECT_ID="openssl"
BUG_ID="CVE-2014-0224"
FILEPATH_X="ssl/s3_srvr.c"

IMAGE_NAME="a14"
CONTAINER_NAME="a14"

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
docker run -v ./:/openssl -d --name "$CONTAINER_NAME" "$IMAGE_NAME" tail -f /dev/null

# # 5) Copy local test.py into the container’s bug directory
# echo "Copying test.py into container..."
# docker cp test.py "${CONTAINER_NAME}":/test.py || true

echo "Compile openssl..."
docker exec "$CONTAINER_NAME" bash -c "
  ./config && make -j32 && make install
" > make_compile_output.txt 2>&1


echo "Starting the test..."
docker exec -it "$CONTAINER_NAME" bash -c "
  chmod +x /tmp/test.py && /tmp/test.py
" > make_test_output.txt 2>&1

echo "Test output written to make_test_output.txt"

# 10) Cleanup
# echo "Stopping and removing container..."
# docker stop "$CONTAINER_NAME" > /dev/null 2>&1 || true

# docker rm "$CONTAINER_NAME" > /dev/null 2>&1 || true

echo "Done."
