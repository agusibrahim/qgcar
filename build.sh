#!/usr/bin/env bash
# Builds qgcar as a stand-alone .jar file (just classes.jar, no resources).
# The library has no resources so this is sufficient for embedding into apps
# that build with d8/apksigner directly.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
PLATFORM="$SDK/platforms/android-29/android.jar"

OUT="$ROOT/build"
CLASSES="$OUT/classes"
rm -rf "$OUT"
mkdir -p "$CLASSES"

JAVA_FILES=$(find "$ROOT/src" -name '*.java')
javac -source 1.8 -target 1.8 \
    -bootclasspath "$PLATFORM" \
    -classpath "$PLATFORM" \
    -d "$CLASSES" \
    $JAVA_FILES

( cd "$CLASSES" && jar cf "$OUT/qgcar.jar" . )
echo "Built: $OUT/qgcar.jar"
ls -lh "$OUT/qgcar.jar"
