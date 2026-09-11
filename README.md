# Kopfgeld – App-Oberfläche (Phase 1)

Lern-Fokus-App für Android. **Diese Phase ist reine Oberfläche.**

Umgesetzt nach `DESIGN.md` (Version 2). Die Wissensbasis zum Projekt liegt im
Repo `Brain2`, Einstieg über `00-Index/MOC-Kopfgeld.md`.

## Was die App ist

Eine persönliche App für einen Schüler der 10. Klasse Realschule,
Abschluss 2027. Sie verbindet drei Dinge:

1. **Sperre** – Scroll-Apps sind gesperrt. Während der Lernzyklen liegt das
   Handy in einer virtuellen Box auf dem Tisch.
2. **Lernsessions** aus frei konfigurierbaren Zyklen: Fach, Themen, Dauer,
   Aufgabentyp.
3. **Brain** – die persönliche Wissensbasis, aus der die Aufgaben kommen.

## Was Phase 1 ist und was nicht

| Drin | Draußen |
|---|---|
| Alle Screens und ihre Zustände | Datenbank |
| Alle 23 Komponenten aus DESIGN.md 5 | Netzwerkaufrufe |
| Vollständiges Theme (Heft und Tafel) | Services, Hintergrundarbeit |
| Navigation | Echte Berechtigungen |
| Daten aus `FakeData.kt` | Echte Kamera, ARCore, Sensoren |
| Buttons, die navigieren oder UI-State ändern | FSRS, echte Analyse |

Kamerabilder sind gezeichnete Platzhalter. Wo ohne Sensoren kein Übergang
möglich wäre, gibt es klar markierte Ersatzgriffe (`MockHinweis`).

## Zielgerät

Samsung Galaxy S9, **Android 9 (API 28)**. `minSdk = 28`, `targetSdk = 34`.
Layouts sind auf 360 dp Breite ausgelegt.

## Aufbau

```
app/src/main/java/de/kopfgeld/app/
  MainActivity.kt
  data/          Modelle, FakeData, Zeitformate
  ui/theme/      Farben, Schrift, Formen, Maße
  ui/components/ die 23 Komponenten aus DESIGN.md 5 plus Gerüstbausteine
  ui/nav/        Routen, Navigationsgraph, Screen-Katalog, AppZustand
  feature/       ein Paket je Bereich, jeweils Screen + UiState
```

### Das Muster

Jeder Screen bekommt eine eigene `UiState`-data-class und nimmt **nur State
und Lambdas** entgegen:

```kotlin
data class HeuteUiState(val datum: String, /* … */)

@Composable
fun HeuteScreen(
    state: HeuteUiState,
    beiEinstellungen: () -> Unit,
    /* … */
)
```

In Phase 2 wird über jeden Screen ein ViewModel gesetzt, ohne dass die UI
angefasst werden muss. Vorläufig füllt `ui/nav/AppZustand.kt` diese Rolle.

## Designsprache

**Heft und Tafel.** Hell ist kariertes Papier mit Füller-Königsblau,
Korrekturrot und Textmarker-Gelb. Dunkel ist Schiefergrün mit Kreideweiß.

Tafel ist **kein Dark Mode, sondern ein Zustand**: Box, laufende Phasen und
Pause sind immer im Tafel-Look. Dafür gibt es `TafelTheme`.

Rot bedeutet immer dasselbe: gesperrt, Fehler, Lücke, Box unterbrochen.
Nie als Deko.

## Alle Screens ansehen

Langer Druck auf das Datum im Tab **Heute** öffnet den **Screen-Katalog**:
eine Liste aller Screens und Zustände, jeder direkt anspringbar. Auch über
Einstellungen erreichbar. In Phase 1 ist das der eigentliche Prüfpfad – ohne
Logik erreicht man Zustände wie „Server nicht erreichbar" oder
„Zyklus unterbrochen" sonst nicht.

Einträge mit der Markierung **abgeleitet** stehen nicht im übermittelten
Auftragstext, sondern folgen aus den Abläufen und dem Komponentenkatalog.

## Bauen

```sh
./gradlew assembleDebug
```

Braucht das Android SDK und Zugriff auf Google Maven.

> **Hinweis:** Dieser Code wurde in einer Umgebung geschrieben, in der
> `dl.google.com` und `maven.google.com` durch die Netzwerk-Policy gesperrt
> waren. Er ist deshalb **nie kompiliert worden**. Geprüft wurde mit den
> Werkzeugen unter `tools/`: Kotlin-Parser für die Grammatik und ein eigener
> Referenzabgleich für Importe und Bezeichner. Siehe `tools/README.md`.
