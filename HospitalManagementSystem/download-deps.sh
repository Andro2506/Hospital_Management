#!/bin/sh
# Helper script to download required JAR dependencies into WEB-INF/lib/
set -e

LIB_DIR="$(dirname "$0")/WebContent/WEB-INF/lib"
JAR_NAME="sqlite-jdbc-3.27.2.jar"
JAR_URL="https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2/${JAR_NAME}"

mkdir -p "$LIB_DIR"

if [ -f "$LIB_DIR/$JAR_NAME" ]; then
  echo "[OK] $JAR_NAME already present in $LIB_DIR"
else
  echo "[..] Downloading $JAR_NAME from Maven Central"
  if command -v curl >/dev/null 2>&1; then
    curl -L -o "$LIB_DIR/$JAR_NAME" "$JAR_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$LIB_DIR/$JAR_NAME" "$JAR_URL"
  else
    echo "[ERROR] Neither curl nor wget is available. Please download manually:"
    echo "        $JAR_URL"
    exit 1
  fi
  echo "[OK] Saved to $LIB_DIR/$JAR_NAME"
fi
