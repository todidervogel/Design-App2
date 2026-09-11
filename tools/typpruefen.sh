#!/bin/sh
# Lokale Typpruefung fuer Relock.
#
# Uebersetzt den kompletten App-Code mit dem echten Kotlin-Compiler und dem
# Compose-Compiler-Plugin gegen Compose Multiplatform von Maven Central.
# Compose Multiplatform benutzt dieselben androidx.compose.*-Pakete wie
# Android, deshalb findet das hier echte Typ- und Ueberladungsfehler -
# nicht nur Syntax.
#
# Voraussetzung: tools/holen.sh einmal ausfuehren.
# Aufruf: tools/typpruefen.sh
set -e

HIER=$(cd "$(dirname "$0")" && pwd)
WURZEL=$(dirname "$HIER")
LIB="${RELOCK_LIB:-$HIER/.lib}"

if [ ! -d "$LIB" ]; then
    echo "Bibliotheken fehlen. Erst tools/holen.sh ausfuehren." >&2
    exit 2
fi

COMPILER=$(ls "$LIB"/kotlin-compiler-embeddable-*.jar)
PLUGIN=$(ls "$LIB"/kotlin-compose-compiler-plugin-*.jar)
COMPILER_CP=$(ls "$LIB"/kotlin-compiler-embeddable-*.jar "$LIB"/kotlin-stdlib-*.jar \
    "$LIB"/kotlinx-coroutines-core-jvm-*.jar "$LIB"/annotations-*.jar "$LIB"/trove4j-*.jar \
    | tr '\n' ':')
ZIEL_CP=$(ls "$LIB"/*.jar | grep -v compose-compiler-plugin | tr '\n' ':')

AUS=$(mktemp -d)
trap 'rm -rf "$AUS"' EXIT

QUELLEN=$(find "$WURZEL/app/src/main/java" "$HIER/stubs" -name '*.kt' | sort)
ANZAHL=$(echo "$QUELLEN" | wc -l)

java -cp "$COMPILER_CP" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler \
    -no-stdlib -no-reflect \
    -Xplugin="$PLUGIN" \
    -cp "$ZIEL_CP" \
    -d "$AUS" \
    -nowarn \
    $QUELLEN 2>&1 | grep -v 'JAVA_TOOL_OPTIONS' || true

ERGEBNIS=$?
echo "---"
echo "$ANZAHL Dateien typgeprueft (inkl. Stubs aus tools/stubs)."
exit $ERGEBNIS
