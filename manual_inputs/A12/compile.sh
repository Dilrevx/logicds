#!/bin/bash

set -euo pipefail

CUR_DIR="$(pwd)"

echo "Building CVE-2022-25640 test: ..."
# First clear previous output
rm -f "$CUR_DIR/make_output.txt"

IMAGE_NAME="a12"
CONTAINER_NAME="a12"

rm -f make_output.txt


if ! docker image inspect "$IMAGE_NAME" > /dev/null 2>&1; then
    echo "Building Docker image '$IMAGE_NAME'..."
    # Build Docker image
    docker-compose build >/dev/null 2>&1 || {
        echo "Error: Docker build failed."
        echo "Error: Build failed." > "${CUR_DIR}/make_output.txt"
        exit 1
    }
    echo "Docker image '$IMAGE_NAME' built successfully."
else
    echo "Docker image '$IMAGE_NAME' already exists."
    docker compose down >/dev/null 2>&1 || true
    docker rmi "$IMAGE_NAME"  >/dev/null 2>&1 || {
        echo "Warning: Failed to remove existing image '$IMAGE_NAME'."
        exit 1
    }
    echo "Building Docker image '$IMAGE_NAME'..."
    # Build Docker image
    docker-compose build >/dev/null 2>&1 || {
        echo "Error: Docker build failed."
        echo "Error: Build failed." > "${CUR_DIR}/make_output.txt"
        exit 1
    }
    echo "Docker image '$IMAGE_NAME' built successfully."
fi


# Run test and save output
docker compose down >/dev/null 2>&1 || true
docker-compose up --abort-on-container-exit 2>&1 | tee -a "${CUR_DIR}/docker_output.log" || {
    echo "Error: Docker execution failed."
    echo "Error: Build failed." > "${CUR_DIR}/make_output.txt"
    exit 1
}

echo "Build succcessful."
echo "PASS: Build succcessful." > "${CUR_DIR}/make_output.txt"
