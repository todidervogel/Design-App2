package de.relock.app.ui.nav

/**
 * Alle Ziele der App, DESIGN.md 5.
 *
 * Relock benutzt kein Navigation Compose, sondern einen Zustandsautomaten:
 * eine versiegelte Schnittstelle plus ein Stapel im App-Zustand. Zwei Gruende.
 *
 * Erstens ist der Graph klein und flach, und der Screen-Katalog soll jedes
 * Ziel direkt anspringen koennen. Zweitens laesst sich das vollstaendig
 * lokal typpruefen - Navigation Compose waere die einzige groessere Flaeche,
 * die nur gegen einen selbst geschriebenen Stub geprueft wuerde.
 */
sealed interface Ziel {

    /** Die drei Ziele der Bottom Navigation. */
    sealed interface Tab : Ziel

    data object Heute : Tab
    data object Brain : Tab
    data object Statistik : Tab

    // --- Vollbild, ohne Navigation ---

    data object Onboarding : Ziel
    data object Einstellungen : Ziel
    data object Katalog : Ziel
    data object Sperrbildschirm : Ziel

    data object HausaufgabeDetail : Ziel
    data object SessionPlanen : Ziel
    data object BoxAufstellen : Ziel
    data object HandyEinlegen : Ziel
    data object InDerBox : Ziel
    data object AufgabenErhalten : Ziel
    data object Scannen : Ziel
    data object WeiterOderAufhoeren : Ziel
    data object Pause : Ziel
    data object Verbesserung : Ziel
    data object SessionEnde : Ziel
    data object Nachsperre : Ziel

    data object BuchDetail : Ziel
    data object SeitenDetail : Ziel
    data object Import : Ziel

    companion object {
        val tabs: List<Tab> = listOf(Heute, Brain, Statistik)
    }
}

/** Die Sheets. Sie liegen ueber dem aktuellen Ziel, statt es zu ersetzen. */
sealed interface Sheet {
    data object Freischalten : Sheet
    data object HausaufgabeAnlegen : Sheet
    data object ZyklusBearbeiten : Sheet
    data object Typauswahl : Sheet
    data object Update : Sheet
    data object Notausgang : Sheet
}

/** Beschriftung eines Tabs in der Bottom Navigation. */
fun tabName(tab: Ziel.Tab): String = when (tab) {
    Ziel.Heute -> "Heute"
    Ziel.Brain -> "Brain"
    Ziel.Statistik -> "Statistik"
}
