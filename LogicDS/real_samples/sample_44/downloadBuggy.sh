#!/bin/bash

# Script to clone openssh-portable and checkout buggy (pre-fix) commit

REPO_URL="https://github.com/openssh/openssh-portable"
COMMIT_HASH="aaed635e3a401cfcc4cc97f33788179c458901c3"
TARGET_DIR="${1:-./projects}"

if [ -d "$TARGET_DIR" ]; then
  echo "Removing existing directory: $TARGET_DIR"
  rm -rf "$TARGET_DIR"
fi

echo "Cloning openssh-portable into: $TARGET_DIR"
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
