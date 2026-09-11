package de.relock.app.data

/*
 * Datenmodelle fuer die Oberflaeche.
 *
 * Version 1 hat keine Datenbank und kein Netz, alle Instanzen kommen aus
 * FakeData. Die Modelle sind trotzdem so geschnitten, dass die naechste
 * Phase sie uebernehmen kann.
 */

// --- Karte -----------------------------------------------------------------

/** Die fuenf Kartenstufen. Wie sie aussehen, steht in ui/theme/CardMaterials.kt. */
enum class Kartenstufe(val anzeige: String) {
    Graphit("Graphit"),
    Bronze("Bronze"),
    Silber("Silber"),
    Gold("Gold"),
    Obsidian("Obsidian"),
}

/** Zustaende der Karte, DESIGN.md 4. */
sealed interface Kartenzustand {
    data object Normal : Kartenzustand

    /** Guthaben gedimmt, darunter steht, ab wann es wieder geht. */
    data class Nachsperre(val freiAb: String) : Kartenzustand

    /** Zeile "+10 Min vorgemerkt" unter dem Guthaben. */
    data class Vorgemerkt(val minuten: Int) : Kartenzustand

    /** Messing-Rand leuchtet, "frei bis 17:40". */
    data class Freigeschaltet(val bis: String) : Kartenzustand
}

data class Karte(
    val name: String,
    /** "09/26" */
    val seit: String,
    val stufe: Kartenstufe,
    val guthabenMinuten: Int,
    val gesamtMinuten: Int,
    val zustand: Kartenzustand = Kartenzustand.Normal,
)

/** Eine Zeile im Kontoauszug auf der Kartenrueckseite. */
data class Buchung(
    val datum: String,
    val text: String,
    /** positiv = verdient, negativ = ausgegeben */
    val minuten: Int,
)

// --- Faecher ---------------------------------------------------------------

data class Fach(
    val id: String,
    val name: String,
    /** Kuerzel fuer das SubjectMark: D, M, E, Bio ... */
    val kuerzel: String,
)

data class Thema(
    val id: String,
    val fachId: String,
    val name: String,
    val fehler: Int,
)

// --- Hausaufgaben ----------------------------------------------------------

enum class Faelligkeit(val anzeige: String) {
    Heute("bis heute"),
    Morgen("bis morgen"),
    Uebermorgen("bis übermorgen"),
    NaechsteWoche("nächste Woche"),
    Ueberfaellig("überfällig"),
}

/** Verknuepfung einer Hausaufgabe mit einer Seite im Brain. */
data class Seitenbezug(
    val buchTitel: String,
    val seite: Int,
    val aufgabenImBrain: Int,
)

data class Hausaufgabe(
    val id: String,
    val fachId: String,
    /** "Buch S. 84, Nr. 3a–d" */
    val aufgabe: String,
    val faellig: Faelligkeit,
    val erledigt: Boolean = false,
    val bezug: Seitenbezug? = null,
    val hatFoto: Boolean = false,
    /** Ergebnis der letzten Korrektur, z. B. "3 richtig, 1 Fehler". */
    val ergebnis: String? = null,
)

// --- Session ---------------------------------------------------------------

enum class Zyklustyp(val anzeige: String, val satz: String) {
    Hausaufgabe(
        "Hausaufgabe",
        "Eine deiner offenen Hausaufgaben.",
    ),
    Aufgaben(
        "Aufgaben",
        "Aufgaben aus dem Brain, ohne Lösung, gemischt.",
    ),
    Lernen(
        "Lernen",
        "Stoff durcharbeiten, danach Kontrollaufgaben.",
    ),
    Blurting(
        "Blurting",
        "Heft zu, alles aufschreiben, dann vergleichen.",
    ),
    LautErklaeren(
        "Laut erklären",
        "Erklär den Stoff, als würdest du ihn beibringen.",
    ),
}

data class Zyklus(
    val id: String,
    val typ: Zyklustyp,
    val fachId: String,
    /** Thema oder Aufgabentitel, je nach Typ. */
    val thema: String,
    val minuten: Int,
    /** Nur bei Typ Aufgaben belegt. */
    val aufgabenAnzahl: Int = 0,
)

data class Sessionplan(
    val zyklen: List<Zyklus>,
    val pauseVonMinuten: Int = 5,
    val pauseBisMinuten: Int = 15,
    val nachsperreMinuten: Int = 30,
) {
    val pauseMitteMinuten: Int get() = (pauseVonMinuten + pauseBisMinuten) / 2

    val gesamtMinuten: Int
        get() = zyklen.sumOf { it.minuten } +
            (zyklen.size - 1).coerceAtLeast(0) * pauseMitteMinuten
}

// --- Aufgaben aus dem Brain ------------------------------------------------

data class Aufgabe(
    val id: String,
    /** "Mathe live 10, S. 84, Nr. 3a" */
    val quelle: String,
    val nummer: String,
    val text: String,
    val thema: String,
    /** Aufgaben mit Diagramm zeigen ein kleines Bild statt nur Text. */
    val hatBild: Boolean = false,
    val loesung: String? = null,
)

// --- Brain -----------------------------------------------------------------

enum class Seitenstand(val anzeige: String) {
    Importiert("importiert"),
    Erkannt("erkannt"),
    Abweichung("Abweichung"),
    Fehlt("fehlt"),
}

data class Buchseite(
    val nummer: Int,
    val stand: Seitenstand,
    val aufgaben: Int = 0,
)

data class Buch(
    val id: String,
    val titel: String,
    val fachId: String,
    val seitenErfasst: Int,
    val seitenGesamt: Int,
    val aufgaben: Int,
    val seiten: List<Buchseite> = emptyList(),
)

data class Arbeitsblatt(
    val id: String,
    val titel: String,
    val fachId: String,
    val datum: String,
    val aufgaben: Int,
)

// --- Scans und Korrektur ---------------------------------------------------

enum class Scanstand(val anzeige: String) {
    Wartet("wartet"),
    Laeuft("wird gelesen"),
    Fertig("fertig"),
    Unscharf("unscharf"),
}

data class Scan(
    val id: String,
    val seite: Int,
    val stand: Scanstand,
)

enum class Urteil(val anzeige: String) {
    Richtig("Richtig"),
    Fehler("Fehler"),
    Luecke("Lücke"),
}

/** Wie sicher sich die Erkennung ist. Steht als kleines Wort am Kommentar. */
enum class Sicherheit(val anzeige: String) {
    Sicher("sicher"),
    Unsicher("unsicher"),
}

/** Eine nummerierte Markierung auf dem Scan-Platzhalter. */
data class Markierung(
    val nummer: Int,
    val xAnteil: Float,
    val yAnteil: Float,
    val richtig: Boolean,
)

data class Korrekturposten(
    val id: String,
    val nummer: String,
    val quelle: String,
    /** Der erkannte Text der eigenen Antwort. Antippen bearbeitet ihn. */
    val deineAntwort: String,
    val kommentar: String,
    val sicherheit: Sicherheit,
    /** Vorschlag der Erkennung, vorausgewaehlt. */
    val vorschlag: Urteil,
    val loesung: String? = null,
    val seite: Int = 1,
)

// --- Zustandszeilen --------------------------------------------------------

/** DESIGN.md 3, LockStatusLine. */
sealed interface Sperrzustand {
    data object Gesperrt : Sperrzustand
    data class Frei(val bis: String) : Sperrzustand
    data class Nachsperre(val restMinuten: Int) : Sperrzustand
    data object SessionLaeuft : Sperrzustand
}

/** DESIGN.md 3, ServerStatus. */
sealed interface Serverzustand {
    data object Verbunden : Serverzustand
    data class Offline(val wartendeScans: Int) : Serverzustand
    data class Korrigiert(val fertig: Int, val gesamt: Int, val einheit: String = "") : Serverzustand
}

// --- Statistik -------------------------------------------------------------

data class Tagesbilanz(
    val tag: String,
    val lernMinuten: Int,
    val scrollMinuten: Int,
)

data class Fachbilanz(
    val fachId: String,
    val richtig: Int,
    val fehler: Int,
    val luecken: Int,
)

data class Schwaeche(
    val themaId: String,
    val fachId: String,
    val thema: String,
    val fehler: Int,
)

// --- Onboarding und Einstellungen ------------------------------------------

data class AppEintrag(
    val name: String,
    val erlaubt: Boolean,
    /** Fest heisst: laesst sich nicht abschalten. */
    val fest: Boolean = false,
)

data class Berechtigung(
    val name: String,
    val wofuer: String,
    val erteilt: Boolean,
)

sealed interface Kopplung {
    data object Sucht : Kopplung
    data object Verbunden : Kopplung
    data object NichtGefunden : Kopplung
}

// --- Update ----------------------------------------------------------------

sealed interface Updatezustand {
    data object Aktuell : Updatezustand
    data class Verfuegbar(
        val version: String,
        val groesse: String,
        val aenderungen: List<String>,
    ) : Updatezustand

    data class Laedt(val prozent: Int) : Updatezustand
    data object BereitZumInstallieren : Updatezustand
    data object Fehlgeschlagen : Updatezustand
}
