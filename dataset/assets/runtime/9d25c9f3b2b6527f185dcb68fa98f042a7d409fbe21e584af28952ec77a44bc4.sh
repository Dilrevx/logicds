#!/bin/bash

# Script to clone exporter-toolkit and checkout buggy (pre-fix) commit

REPO_URL="https://github.com/prometheus/exporter-toolkit"
COMMIT_HASH="c6a241567e08fcf9b226814562a9a07b24265271"
TARGET_DIR="${1:-./projects}"

if [ -d "$TARGET_DIR" ]; then
  echo "Removing existing directory: $TARGET_DIR"
  rm -rf "$TARGET_DIR"
fi

echo "Cloning exporter-toolkit into: $TARGET_DIR"
git clone "$REPO_URL" "$TARGET_DIR"

if [ $? -ne 0 ]; then
  echo "Failed to clone repository."
  exit 2
fi

cd "$TARGET_DIR" || { echo "Failed to change directory."; exit 3; }

git checkout "$COMMIT_HASH"

if [ $? -ne 0 ]; then
  echo "Failed to checkout commit $COMMIT_HASH."
  exit 4
fi

echo "Checked out buggy (pre-fix) commit $COMMIT_HASH in '$TARGET_DIR'."
