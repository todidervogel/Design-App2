package de.kopfgeld.app.data

/*
 * Datenmodelle fuer die UI. Phase 1 hat keine Datenbank und kein Netz,
 * alle Instanzen kommen aus FakeData. Die Modelle sind trotzdem so
 * geschnitten, dass Phase 2 sie uebernehmen kann.
 */

// --- Faecher und Themen ----------------------------------------------------

data class Fach(
    val id: String,
    val name: String,
    /** Kuerzel fuer das SubjectBadge, z. B. "M". */
    val kuerzel: String,
    val istPruefungsfach: Boolean,
    val aktiv: Boolean = true,
)

enum class Themastand {
    /** leerer Kreis */
    Neu,

    /** halb gefuellt */
    Gelernt,

    /** voll */
    Sitzt,
}

data class Thema(
    val id: String,
    val fachId: String,
    val name: String,
    val stand: Themastand,
    val anzahlAufgaben: Int,
    val anzahlKarten: Int,
)

// --- Aufgaben --------------------------------------------------------------

enum class Aufgabenquelle(val anzeige: String) {
    Buch("Buch"),
    Arbeitsblatt("Arbeitsblatt"),
    AlteArbeit("Alte Arbeit"),
    EigeneFrage("Eigene Frage"),
}

enum class Aufgabenstand(val anzeige: String) {
    Neu("neu"),
    Geuebt("geübt"),
    Fehler("Fehler"),
    Sitzt("sitzt"),
}

data class Aufgabe(
    val id: String,
    val fachId: String,
    val themaIds: List<String>,
    val quelle: Aufgabenquelle,
    /** "Buch S. 84, Nr. 3a" */
    val quellenangabe: String,
    /** Erster Satz des transkribierten Textes. */
    val text: String,
    /** Aufgabentyp, z. B. "Textaufgabe". Fuer "Aufgabentypen mischen". */
    val typ: String,
    val stand: Aufgabenstand,
    /** Loesung liegt vor, bleibt in Zyklen aber verborgen. */
    val hatLoesung: Boolean,
    val geschaetzteMinuten: Int,
)

// --- Zyklen und Sessions ---------------------------------------------------

enum class Zyklustyp(val anzeige: String, val erklaerung: String) {
    Blurting(
        "Blurting",
        "Heft zu, alles aufschreiben, was du noch weißt. Danach vergleichen und Lücken ergänzen.",
    ),
    Lernen(
        "Lernen",
        "Stoff durcharbeiten, danach Aufgaben zur Kontrolle.",
    ),
    Aufgaben(
        "Aufgaben",
        "Aufgaben ohne Blick auf die Lösung.",
    ),
    LautErklaeren(
        "Laut erklären",
        "Erklär den Stoff laut, als würdest du ihn jemandem beibringen. Stichpunkte aufschreiben.",
    ),
    Karteikarten(
        "Karteikarten",
        "Fällige Karten der gewählten Themen.",
    ),
    ;

    /** Karteikarten laufen in der App, nicht in der Box. */
    val hatBoxAufgabenphase: Boolean get() = this != Karteikarten
}

data class Zyklus(
    val id: String,
    val fachId: String,
    val themaIds: List<String>,
    val typ: Zyklustyp,
    val lernphaseMinuten: Int,
    val aufgabenphaseMinuten: Int,
    /** true = automatisch aus dem Brain, false = selbst ausgewaehlt. */
    val aufgabenAutomatisch: Boolean = true,
    val aufgabenIds: List<String> = emptyList(),
    val typenMischen: Boolean = false,
    /** Freier Zusatz fuer die Typzeile, z. B. "+ Karten". */
    val zusatz: String = "",
) {
    val gesamtMinuten: Int get() = lernphaseMinuten + aufgabenphaseMinuten

    /** Zweite Zeile im CycleBlock: "Aufgaben, gemischt", "Lernen + Karten". */
    val typZeile: String
        get() = buildString {
            append(typ.anzeige)
            if (typenMischen) append(", gemischt")
            if (zusatz.isNotBlank()) {
                append(' ')
                append(zusatz)
            }
        }
}

data class Sessionplan(
    val name: String,
    val zyklen: List<Zyklus>,
    val pauseVonMinuten: Int = 5,
    val pauseBisMinuten: Int = 15,
    val speicherzeitMinuten: Int = 30,
) {
    /** Pausen gehen mit der Mitte ihres Bereichs in die Schaetzung ein. */
    val pauseMitteMinuten: Int get() = (pauseVonMinuten + pauseBisMinuten) / 2

    val gesamtMinuten: Int
        get() = zyklen.sumOf { it.gesamtMinuten } +
            (zyklen.size - 1).coerceAtLeast(0) * pauseMitteMinuten
}

data class Vorlage(
    val id: String,
    val name: String,
    /** "3 Zyklen, 70 Min" */
    val beschreibung: String,
    val plan: Sessionplan,
)

// --- Scans und Analyse -----------------------------------------------------

enum class Scanstand(val anzeige: String) {
    Wartet("wartet"),
    Analysiert("analysiert"),
    Fertig("fertig"),
}

data class Scan(
    val id: String,
    val seitennummer: Int,
    val stand: Scanstand,
    /** Kurze Herkunft, z. B. "Mathe, Zyklus 1". */
    val herkunft: String = "",
)

/** Zustaende des Brain-Servers, DESIGN.md 5 (ServerStatusChip). */
sealed interface Serverstand {
    data object Verbunden : Serverstand
    data class NichtErreichbar(val wartendeScans: Int) : Serverstand
    data class Analysiert(
        val fertig: Int,
        val gesamt: Int,
        /** Optionaler Zusatz, z. B. "Seiten" fuer "Analysiert 1 von 3 Seiten". */
        val einheit: String = "",
    ) : Serverstand
}

/** Zustaende der Sperre, DESIGN.md 5 (LockStatusLine). */
sealed interface Sperrstand {
    data object Gesperrt : Sperrstand
    data class Speicherzeit(val restMinuten: Int, val gesamtMinuten: Int) : Sperrstand
    data class Frei(val bisUhrzeit: String) : Sperrstand
}

// --- Korrektur und Verbesserung --------------------------------------------

enum class Urteil(val anzeige: String) {
    Richtig("Richtig"),
    Fehler("Fehler"),
    Luecke("Lücke"),
}

data class Korrekturposten(
    val id: String,
    val aufgabenNummer: String,
    val quellenangabe: String,
    val aufgabentext: String,
    /** Was die KI vorschlaegt. Ist vorausgewaehlt und als Vorschlag markiert. */
    val vorschlag: Urteil,
    /** Die Randnotiz der KI, in Caveat und Korrekturrot. */
    val notiz: String,
    /** Auf welcher gescannten Seite der Fehler steckt. */
    val seitennummer: Int,
    /** Markierungen auf dem Scan-Platzhalter, 0..1 relativ zur Seite. */
    val markierungen: List<Markierung> = emptyList(),
    val wirdZuKarte: Boolean = false,
)

data class Markierung(
    val nummer: Int,
    val xAnteil: Float,
    val yAnteil: Float,
    val breiteAnteil: Float,
    val art: Markierungsart,
)

enum class Markierungsart { Kreis, Unterstrich }

data class OffeneVerbesserung(
    val id: String,
    val titel: String,
    val meta: String,
    val anzahlAufgaben: Int,
    val bereit: Boolean,
)

// --- Karteikarten ----------------------------------------------------------

data class Karteikarte(
    val id: String,
    val fachId: String,
    val themaId: String,
    val vorderseite: String,
    val rueckseite: String,
    val faellig: Boolean,
    /** Woher die Karte kommt, z. B. "aus Fehler vom 8. September". */
    val herkunft: String = "",
)

/** Die vier Bewertungen mit ihrem naechsten Intervall. */
data class Wiederholungswahl(
    val anzeige: String,
    val intervall: String,
)

// --- Brain: Fehler, Podcasts -----------------------------------------------

data class Fehlerposten(
    val id: String,
    val fachId: String,
    val themaId: String,
    val beschreibung: String,
    val datum: String,
    val anzahl: Int,
    val hatKarte: Boolean,
)

enum class Podcaststand(val anzeige: String) {
    PaketBereit("Paket bereit zum Kopieren"),
    WartetAufImport("wartet auf Import"),
    Importiert("importiert"),
}

data class Podcast(
    val id: String,
    val titel: String,
    val fachId: String,
    val themen: String,
    val stand: Podcaststand,
    val dauer: String,
)

// --- Probearbeit -----------------------------------------------------------

enum class Probearbeitstand(val anzeige: String) {
    Geplant("geplant"),
    Laeuft("läuft"),
    WartetAufBewertung("wartet auf Bewertung"),
    Bewertet("bewertet"),
}

data class Rasterzeile(
    val kriterium: String,
    val punkte: Int,
    val maximum: Int,
)

data class Probearbeit(
    val id: String,
    val titel: String,
    val fachId: String,
    val dauerMinuten: Int,
    val stand: Probearbeitstand,
    val datum: String,
    val seiten: Int,
    /** Geschaetzte Note 1 bis 6, null solange nicht bewertet. */
    val geschaetzteNote: Double? = null,
    /** Spaeter eingetragene echte Note. */
    val echteNote: Double? = null,
    val raster: List<Rasterzeile> = emptyList(),
    val kommentar: String = "",
)

// --- Fortschritt -----------------------------------------------------------

data class Notenpunkt(
    val datum: String,
    val kurz: String,
    val note: Double,
)

data class Notenverlauf(
    val fachId: String,
    val punkte: List<Notenpunkt>,
    val zielnote: Double,
)

data class Tagesbilanz(
    val tag: String,
    val lernMinuten: Int,
    val scrollMinuten: Int,
)

data class Ziel(
    val titel: String,
    val meta: String,
    val erreicht: Int,
    val gesamt: Int,
)

// --- Tagesplan -------------------------------------------------------------

enum class Planart { Blurting, Karteikarten, Aufgaben, Verbesserung, Schlafenszeit }

data class Planposten(
    val id: String,
    val titel: String,
    /** Zweite Zeile, z. B. "Heute im Unterricht: Brüche". */
    val meta: String,
    val dauer: String,
    val erledigt: Boolean,
    val art: Planart,
)

// --- Onboarding und Einstellungen ------------------------------------------

data class Stundenplanfeld(
    val tag: Int,
    val stunde: Int,
    val fachId: String?,
)

data class AppEintrag(
    val name: String,
    val gesperrt: Boolean,
)

data class Berechtigung(
    val name: String,
    val erklaerung: String,
    val erteilt: Boolean,
)

sealed interface Verbindungstest {
    data object Ungeprueft : Verbindungstest
    data object Prueft : Verbindungstest
    data object Verbunden : Verbindungstest
    data object NichtErreichbar : Verbindungstest
}

// --- Testphase -------------------------------------------------------------

data class Testphase(
    val fachId: String,
    val fachName: String,
    val tageBisArbeit: Int,
) {
    val bannerText: String
        get() = "$fachName-Arbeit in $tageBisArbeit Tagen. Testphase: nur noch testen."
}
