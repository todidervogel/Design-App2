package de.kopfgeld.app.ui.nav

/**
 * Alle Routen der App, DESIGN.md 6.
 *
 * Die vier Tabs bekommen die Bottom Navigation, alle Vollbild-Flows nicht.
 * In Phase 1 tragen die Routen keine Argumente: was gerade ausgewaehlt ist,
 * haelt der App-Zustand in KopfgeldApp. So bleibt der Graph flach und der
 * Debug-Screen-Katalog kann jede Route direkt anspringen.
 */
object Ziel {
    // Vier Tabs
    const val HEUTE = "heute"
    const val LERNEN = "lernen"
    const val BRAIN = "brain"
    const val FORTSCHRITT = "fortschritt"

    val tabs = listOf(HEUTE, LERNEN, BRAIN, FORTSCHRITT)

    // Vollbild
    const val ONBOARDING = "onboarding"
    const val EINSTELLUNGEN = "einstellungen"
    const val KATALOG = "katalog"
    const val SPERRBILDSCHIRM = "sperrbildschirm"

    // Session planen
    const val SESSION_PLANEN = "session/planen"
    const val AUFGABENAUSWAHL = "session/aufgabenauswahl"

    // Session-Flow
    const val BOX_AUFSTELLEN = "session/box-aufstellen"
    const val HINEINLEGEN = "session/hineinlegen"
    const val IN_DER_BOX = "session/in-der-box"
    const val AUFGABEN_ERHALTEN = "session/aufgaben-erhalten"
    const val SCANNEN = "session/scannen"
    const val WEITER_ODER_AUFHOEREN = "session/weiter-oder-aufhoeren"
    const val PAUSE = "session/pause"
    const val VERBESSERUNG = "session/verbesserung"
    const val SESSION_ENDE = "session/ende"

    // Brain
    const val BRAIN_FACH = "brain/fach"
    const val BRAIN_THEMA = "brain/thema"
    const val BRAIN_SCANS = "brain/scans"
    const val BRAIN_KARTEN = "brain/karten"
    const val BRAIN_FEHLER = "brain/fehler"
    const val BRAIN_PODCASTS = "brain/podcasts"

    // Import
    const val IMPORT_SCANNEN = "import/scannen"
    const val IMPORT_WARTESCHLANGE = "import/warteschlange"
    const val IMPORT_PRUEFEN = "import/pruefen"

    // Probearbeit
    const val PROBEARBEIT_EINRICHTEN = "probearbeit/einrichten"
    const val PROBEARBEIT_LAEUFT = "probearbeit/laeuft"
    const val PROBEARBEIT_SCANNEN = "probearbeit/scannen"
    const val PROBEARBEIT_ERGEBNIS = "probearbeit/ergebnis"

    // Karteikarten
    const val KARTEIKARTEN = "karteikarten"
}
