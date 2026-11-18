#!/bin/bash
command -v jq >/dev/null || exit 0
cd "$(dirname "$0")/../.." || exit 0
FILE=$(jq -r '.tool_input.file_path')
case "$FILE" in
  *.java) ./mvnw -q spotless:apply -DspotlessFiles="\\Q$FILE\\E" ;;
esac
exit 0
