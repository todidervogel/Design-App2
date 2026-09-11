package de.kopfgeld.app.ui.nav

/**
 * Screen-Katalog, DESIGN.md 6: "Debug, langer Druck auf den Titel 'Heute':
 * Liste aller Screens und Zustaende."
 *
 * In Phase 1 ist das der eigentliche Pruefpfad. Ohne Logik erreicht man viele
 * Zustaende sonst gar nicht - etwa "Server nicht erreichbar" oder
 * "Zyklus unterbrochen".
 */
data class Katalogeintrag(
    val titel: String,
    val route: String,
    /** Schluessel fuer einen bestimmten Zustand, siehe Demozustand. */
    val zustand: String? = null,
    /** Steht so im Auftrag oder wurde abgeleitet? */
    val abgeleitet: Boolean = false,
    /** Abschnittsnummer aus DESIGN.md, wenn vorhanden. */
    val quelle: String = "",
)

data class Kataloggruppe(
    val name: String,
    val eintraege: List<Katalogeintrag>,
)

object Katalog {

    val gruppen: List<Kataloggruppe> = listOf(
        Kataloggruppe(
            "Onboarding",
            listOf(
                Katalogeintrag("1 Idee", Ziel.ONBOARDING, "onboarding.0", quelle = "7.1"),
                Katalogeintrag("2 Fächer und Stundenplan", Ziel.ONBOARDING, "onboarding.1", quelle = "7.1"),
                Katalogeintrag("3 Apps", Ziel.ONBOARDING, "onboarding.2", quelle = "7.1"),
                Katalogeintrag("4 Brain-Server", Ziel.ONBOARDING, "onboarding.3", quelle = "7.1"),
                Katalogeintrag("5 Berechtigungen", Ziel.ONBOARDING, "onboarding.4", quelle = "7.1"),
            ),
        ),
        Kataloggruppe(
            "Heute",
            listOf(
                Katalogeintrag("Normal", Ziel.HEUTE, "heute.normal", quelle = "7.2"),
                Katalogeintrag("Session läuft gerade", Ziel.HEUTE, "heute.laeuft", quelle = "7.2"),
                Katalogeintrag("Alles erledigt", Ziel.HEUTE, "heute.erledigt", quelle = "7.2"),
                Katalogeintrag("Server nicht erreichbar", Ziel.HEUTE, "heute.offline", quelle = "7.2"),
            ),
        ),
        Kataloggruppe(
            "Lernen und Planung",
            listOf(
                Katalogeintrag("Lernen", Ziel.LERNEN, quelle = "7.3"),
                Katalogeintrag("Session planen", Ziel.SESSION_PLANEN, quelle = "7.4"),
                Katalogeintrag("Aufgabenauswahl", Ziel.AUFGABENAUSWAHL, quelle = "7.6"),
            ),
        ),
        Kataloggruppe(
            "Session-Flow",
            listOf(
                Katalogeintrag("Box aufstellen: sucht Fläche", Ziel.BOX_AUFSTELLEN, "box.sucht", quelle = "7.7"),
                Katalogeintrag("Box aufstellen: Fläche gefunden", Ziel.BOX_AUFSTELLEN, "box.gefunden", quelle = "7.7"),
                Katalogeintrag("Box aufstellen: keine Fläche", Ziel.BOX_AUFSTELLEN, "box.nichterkannt", quelle = "7.7"),
                Katalogeintrag("Handy hineinlegen", Ziel.HINEINLEGEN, "hineinlegen.warte", quelle = "7.8"),
                Katalogeintrag("Countdown 3", Ziel.HINEINLEGEN, "hineinlegen.countdown", quelle = "7.8"),
                Katalogeintrag("In der Box: läuft", Ziel.IN_DER_BOX, "inbox.laeuft", quelle = "7.9"),
                Katalogeintrag("In der Box: unterbrochen", Ziel.IN_DER_BOX, "inbox.unterbrochen", quelle = "7.9"),
                Katalogeintrag("In der Box: fertig", Ziel.IN_DER_BOX, "inbox.fertig", quelle = "7.9"),
                Katalogeintrag("Aufgaben erhalten", Ziel.AUFGABEN_ERHALTEN, quelle = "7.10"),
                Katalogeintrag("Scannen", Ziel.SCANNEN, quelle = "7.11"),
                Katalogeintrag("Weitermachen oder aufhören", Ziel.WEITER_ODER_AUFHOEREN, quelle = "7.12"),
                Katalogeintrag("Pause: Analyse läuft", Ziel.PAUSE, "pause.analyse", quelle = "7.13"),
                Katalogeintrag("Pause: Verbesserung bereit", Ziel.PAUSE, "pause.bereit", abgeleitet = true),
                Katalogeintrag("Verbesserung", Ziel.VERBESSERUNG, abgeleitet = true),
                Katalogeintrag("Session-Ende, Speicherzeit", Ziel.SESSION_ENDE, abgeleitet = true),
                Katalogeintrag("Sperrbildschirm", Ziel.SPERRBILDSCHIRM, abgeleitet = true),
            ),
        ),
        Kataloggruppe(
            "Brain",
            listOf(
                Katalogeintrag("Brain-Übersicht", Ziel.BRAIN, abgeleitet = true),
                Katalogeintrag("Fach", Ziel.BRAIN_FACH, abgeleitet = true),
                Katalogeintrag("Thema", Ziel.BRAIN_THEMA, abgeleitet = true),
                Katalogeintrag("Scans", Ziel.BRAIN_SCANS, abgeleitet = true),
                Katalogeintrag("Karteikarten", Ziel.BRAIN_KARTEN, abgeleitet = true),
                Katalogeintrag("Fehler", Ziel.BRAIN_FEHLER, abgeleitet = true),
                Katalogeintrag("Podcasts", Ziel.BRAIN_PODCASTS, abgeleitet = true),
            ),
        ),
        Kataloggruppe(
            "Import",
            listOf(
                Katalogeintrag("Scannen", Ziel.IMPORT_SCANNEN, abgeleitet = true),
                Katalogeintrag("Warteschlange", Ziel.IMPORT_WARTESCHLANGE, abgeleitet = true),
                Katalogeintrag("Prüfen", Ziel.IMPORT_PRUEFEN, abgeleitet = true),
            ),
        ),
        Kataloggruppe(
            "Probearbeit",
            listOf(
                Katalogeintrag("Einrichten", Ziel.PROBEARBEIT_EINRICHTEN, abgeleitet = true),
                Katalogeintrag("Läuft", Ziel.PROBEARBEIT_LAEUFT, abgeleitet = true),
                Katalogeintrag("Wartet auf Bewertung", Ziel.PROBEARBEIT_SCANNEN, abgeleitet = true),
                Katalogeintrag("Ergebnis", Ziel.PROBEARBEIT_ERGEBNIS, abgeleitet = true),
            ),
        ),
        Kataloggruppe(
            "Weiteres",
            listOf(
                Katalogeintrag("Karteikarten wiederholen", Ziel.KARTEIKARTEN, abgeleitet = true),
                Katalogeintrag("Fortschritt", Ziel.FORTSCHRITT, abgeleitet = true),
                Katalogeintrag("Einstellungen", Ziel.EINSTELLUNGEN, abgeleitet = true),
            ),
        ),
    )

    val anzahlEintraege: Int get() = gruppen.sumOf { it.eintraege.size }
}
