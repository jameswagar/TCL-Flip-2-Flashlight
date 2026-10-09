#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build/test-classes"
JAVA_HOME="${JAVA_HOME:-$(brew --prefix openjdk)/libexec/openjdk.jdk/Contents/Home}"
export JAVA_HOME PATH="$JAVA_HOME/bin:$PATH"
rm -rf "$OUT"
mkdir -p "$OUT"
javac -source 8 -target 8 -d "$OUT" \
  "$ROOT/src/com/dumbphone/flashlight/BrightnessPolicy.java" \
  "$ROOT/src/com/dumbphone/flashlight/ColorModePolicy.java" \
  "$ROOT/tests/com/dumbphone/flashlight/BrightnessPolicyTest.java" \
  "$ROOT/tests/com/dumbphone/flashlight/ColorModePolicyTest.java"
java -cp "$OUT" com.dumbphone.flashlight.BrightnessPolicyTest
java -cp "$OUT" com.dumbphone.flashlight.ColorModePolicyTest
echo 'screen-flashlight tests PASS'
