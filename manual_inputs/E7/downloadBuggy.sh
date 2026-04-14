#!/usr/bin/env bash
set -e

# 1) Determine the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 2) Define the source and target directories
SOURCE_DIR="./SyntheticDataset/4g_buggy"
TARGET_DIR="./projects"

# 3) Verify that the source directory exists
if [ ! -d "$SOURCE_DIR" ]; then
  echo "Error: Source directory not found: $SOURCE_DIR"
  exit 1
fi

# 4) Create the target directory if it doesn't already exist
mkdir -p "$TARGET_DIR"

# 5) Copy everything (including hidden files) from SOURCE_DIR into TARGET_DIR
cp -a "$SOURCE_DIR/." "$TARGET_DIR/"

echo "✅  Contents of '4g' have been copied to: $TARGET_DIR"
