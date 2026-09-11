# Werkzeuge

Diese drei Helfer sind entstanden, weil das Projekt in der Umgebung, in der es
geschrieben wurde, **nicht gebaut werden konnte**: `dl.google.com` und
`maven.google.com` sind dort per Netzwerk-Policy gesperrt, und Maven Central
führt weder das Android Gradle Plugin noch AndroidX-Artefakte.

Sie ersetzen keinen Compiler. Sie fangen aber genau die Fehlerklassen, die
sonst erst beim ersten Build auffallen.

## SyntaxCheck.java

Parst jede `.kt`-Datei mit dem echten Kotlin-Parser und meldet Syntaxfehler
mit Zeilennummer. Keine Typprüfung, keine Auflösung von Referenzen.

```sh
# Kotlin-Compiler von Maven Central holen
V=2.0.21
curl -sSLO https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-compiler-embeddable/$V/kotlin-compiler-embeddable-$V.jar
curl -sSLO https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$V/kotlin-stdlib-$V.jar
curl -sSLO https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.8.1/kotlinx-coroutines-core-jvm-1.8.1.jar
curl -sSLO https://repo1.maven.org/maven2/org/jetbrains/annotations/24.1.0/annotations-24.1.0.jar

javac -proc:none -cp kotlin-compiler-embeddable-$V.jar -d . SyntaxCheck.java
java -cp ".:kotlin-compiler-embeddable-$V.jar:kotlin-stdlib-$V.jar:kotlinx-coroutines-core-jvm-1.8.1.jar:annotations-24.1.0.jar" \
  SyntaxCheck ../app/src/main/java
```

## referenzen_pruefen.py

Sammelt alle Top-Level-Deklarationen je Paket und alle Importe je Datei und
meldet dann großgeschriebene Bezeichner, die weder importiert noch im eigenen
Paket deklariert sind. Meldet außerdem projektinterne Importe, die ins Leere
zeigen.

```sh
python3 referenzen_pruefen.py
```

Erwartete Restmeldung: `de.kopfgeld.app.R` – die R-Klasse wird erst beim Build
erzeugt.

## ungenutzte_importe.py

Meldet Importe, deren Name im Dateikörper nicht vorkommt.

```sh
python3 ungenutzte_importe.py          # nur melden
python3 ungenutzte_importe.py --fix    # entfernen
```

**Vorsicht mit `--fix`:** `getValue` und `setValue` werden für
`by mutableStateOf(...)` gebraucht, tauchen im Text aber nie auf. Diese
Meldungen sind falsch positiv und müssen stehen bleiben.

## Sobald ein echter Build möglich ist

Diese Werkzeuge sind ein Notbehelf. Mit Android SDK und Zugriff auf Google
Maven gilt wieder:

```sh
./gradlew assembleDebug
```
