# Stubs für die lokale Typprüfung

Diese Dateien werden **nie mit ausgeliefert**. Sie liegen unter `tools/` und
nicht unter `app/src/`.

Die Typprüfung (`tools/typpruefen.sh`) übersetzt den App-Code gegen Compose
Multiplatform von Maven Central — dieselben `androidx.compose.*`-Pakete wie
unter Android. Was es dort nicht gibt, weil es nur unter Android existiert,
steht hier mit derselben Signatur nachgebaut.

Was hier steht, ist nur so verlässlich wie die Signatur, die abgeschrieben
wurde. Der Gradle-Build in CI prüft danach gegen die echten Bibliotheken.
Deshalb ist die Stub-Fläche bewusst klein gehalten — und deshalb benutzt
Relock auch kein Navigation Compose, siehe `04-Architektur` im Brain.
