#!/bin/sh
# Holt die Bibliotheken fuer die lokale Typpruefung von Maven Central.
# Einmal ausfuehren, danach liegt alles in tools/.lib (nicht im Repo).
set -e

HIER=$(cd "$(dirname "$0")" && pwd)
LIB="${RELOCK_LIB:-$HIER/.lib}"
mkdir -p "$LIB"
cd "$LIB"

K=2.0.21
C=1.7.3
Z=https://repo1.maven.org/maven2

hole() {
    ziel=$(basename "$1")
    [ -f "$ziel" ] && return 0
    echo "  $ziel"
    curl -sSLf -o "$ziel" "$1"
}

echo "Kotlin-Compiler und Compose-Compiler-Plugin:"
hole "$Z/org/jetbrains/kotlin/kotlin-compiler-embeddable/$K/kotlin-compiler-embeddable-$K.jar"
hole "$Z/org/jetbrains/kotlin/kotlin-stdlib/$K/kotlin-stdlib-$K.jar"
hole "$Z/org/jetbrains/kotlin/kotlin-compose-compiler-plugin-embeddable/$K/kotlin-compose-compiler-plugin-embeddable-$K.jar"
hole "$Z/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.8.1/kotlinx-coroutines-core-jvm-1.8.1.jar"
hole "$Z/org/jetbrains/annotations/24.1.0/annotations-24.1.0.jar"
hole "$Z/org/jetbrains/intellij/deps/trove4j/1.0.20200330/trove4j-1.0.20200330.jar"

echo "Compose Multiplatform $C (dieselben androidx.compose.*-Pakete):"
for pfad in \
    "runtime/runtime-desktop" "runtime/runtime-saveable-desktop" \
    "ui/ui-desktop" "ui/ui-graphics-desktop" "ui/ui-text-desktop" \
    "ui/ui-unit-desktop" "ui/ui-geometry-desktop" "ui/ui-util-desktop" \
    "foundation/foundation-desktop" "foundation/foundation-layout-desktop" \
    "material3/material3-desktop" "material/material-icons-core-desktop" \
    "animation/animation-desktop" "animation/animation-core-desktop"
do
    name=$(basename "$pfad")
    hole "$Z/org/jetbrains/compose/$pfad/$C/$name-$C.jar"
done
hole "$Z/org/jetbrains/skiko/skiko-awt/0.8.18/skiko-awt-0.8.18.jar"

echo "Fertig. $(ls *.jar | wc -l) Jars in $LIB"
