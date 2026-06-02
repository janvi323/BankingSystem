#!/usr/bin/env sh
set -e

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

echo "Maven is required but was not found on PATH." >&2
exit 1
