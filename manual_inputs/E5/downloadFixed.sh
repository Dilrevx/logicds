#!/bin/bash

set -euo pipefail

SOURCE_DIR="./SyntheticDataset/5g_fixed"
TARGET_DIR="./projects"

# Clean up target if it exists
if [ -d "$TARGET_DIR" ]; then
  echo "Removing existing '$TARGET_DIR' directory..."
  rm -rf "$TARGET_DIR"
fi

# Create target directory
mkdir -p "$TARGET_DIR"

# Copy contents from the source folder
echo "Copying contents from '$SOURCE_DIR' to '$TARGET_DIR'..."
cp -r "$SOURCE_DIR/"* "$TARGET_DIR/"

echo "fixed project copied to '$TARGET_DIR'."

cp -r "../5G_test" "$TARGET_DIR/5G_test"


