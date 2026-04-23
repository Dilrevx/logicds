#!/bin/bash

# Script to clone netmaker and checkout fixed commit

REPO_URL="https://github.com/gravitl/netmaker"
COMMIT_HASH="1621c27c1d176b639e9768b2acad7693e387fd51"
TARGET_DIR="${1:-./projects}"

if [ -d "$TARGET_DIR" ]; then
  echo "Removing existing directory: $TARGET_DIR"
  rm -rf "$TARGET_DIR"
fi

echo "Cloning netmaker into: $TARGET_DIR"
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

echo "Checked out fixed commit $COMMIT_HASH in '$TARGET_DIR'."
