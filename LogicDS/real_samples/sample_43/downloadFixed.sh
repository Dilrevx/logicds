#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SOURCE_DIR="${SCRIPT_DIR}/../_srsran_shared/5g_fixed"
TARGET_DIR="${1:-./projects}"

if [ -d "$TARGET_DIR" ]; then
  echo "Removing existing '$TARGET_DIR' directory..."
  rm -rf "$TARGET_DIR"
fi
mkdir -p "$TARGET_DIR"

echo "Copying fixed source from '$SOURCE_DIR' to '$TARGET_DIR'..."
cp -r "$SOURCE_DIR/"* "$TARGET_DIR/"

echo "Fixed project copied to '$TARGET_DIR'."
