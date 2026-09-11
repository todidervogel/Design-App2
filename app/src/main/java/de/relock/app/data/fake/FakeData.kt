package de.relock.app.data.fake

import de.relock.app.data.AppEintrag
import de.relock.app.data.Arbeitsblatt
import de.relock.app.data.Aufgabe
import de.relock.app.data.Berechtigung
import de.relock.app.data.Buch
import de.relock.app.data.Buchseite
import de.relock.app.data.Buchung
import de.relock.app.data.Fach
import de.relock.app.data.Fachbilanz
import de.relock.app.data.Faelligkeit
import de.relock.app.data.Hausaufgabe
import de.relock.app.data.Karte
import de.relock.app.data.Kartenstufe
import de.relock.app.data.Korrekturposten
import de.relock.app.data.Markierung
import de.relock.app.data.Scan
import de.relock.app.data.Scanstand
import de.relock.app.data.Schwaeche
import de.relock.app.data.Seitenbezug
import de.relock.app.data.Seitenstand
import de.relock.app.data.Serverzustand
import de.relock.app.data.Sessionplan
import de.relock.app.data.Sicherheit
import de.relock.app.data.Sperrzustand
import de.relock.app.data.Tagesbilanz
import de.relock.app.data.Thema
import de.relock.app.data.Updatezustand
import de.relock.app.data.Urteil
import de.relock.app.data.Zyklus
import de.relock.app.data.Zyklustyp

/*
 * Alle Daten der App in Version 1. Keine Datenbank, kein Netz.
 *
 * DESIGN.md 7 nennt die Karte ausdruecklich: Max Mustermann, seit 09/26,
 * Bronze, 42 Min verfuegbar, 128 Std. 40 Min gesammelt. Der Rest von 7 fehlt
 * im uebermittelten Text; die uebrigen Werte stehen deshalb so, wie sie in
 * den Wireframes von Abschnitt 6 vorkommen - "2 von 3 Hausaufgaben",
 * "212 von 248 Seiten", "Noch 21 Std. bis Silber" und so weiter.
 */
object FakeData {

    // --- Karte -------------------------------------------------------------

    const val NAME = "Max Mustermann"
    const val SEIT = "09/26"
    const val GUTHABEN_MINUTEN = 42
    const val GESAMT_MINUTEN = 7720 // 128 Std. 40 Min

    val karte = Karte(
        name = NAME,
        seit = SEIT,
        stufe = Kartenstufe.Bronze,
        guthabenMinuten = GUTHABEN_MINUTEN,
        gesamtMinuten = GESAMT_MINUTEN,
    )

    val buchungen = listOf(
        Buchung("10.09.", "Mathe, 2 Zyklen", 20),
        Buchung("10.09.", "Hausaufgabe Englisch", 10),
        Buchung("09.09.", "YouTube freigeschaltet", -15),
        Buchung("09.09.", "Deutsch, Blurting", 10),
        Buchung("09.09.", "Probearbeit Mathe", 30),
        Buchung("08.09.", "Instagram freigeschaltet", -10),
        Buchung("08.09.", "Mathe, 3 Zyklen", 30),
        Buchung("07.09.", "Hausaufgabe Bio", 10),
    )

    /** Kontoauszug auf der Kartenrueckseite: die letzten sechs. */
    val letzteBuchungen: List<Buchung> get() = buchungen.take(6)

    // --- Faecher -----------------------------------------------------------

    val faecher = listOf(
        Fach("d", "Deutsch", "D"),
        Fach("m", "Mathe", "M"),
        Fach("e", "Englisch", "E"),
        Fach("bio", "Biologie", "Bio"),
        Fach("ph", "Physik", "Ph"),
        Fach("g", "Geschichte", "G"),
    )

    fun fach(id: String): Fach = faecher.first { it.id == id }

    fun fachOderNull(id: String?): Fach? = faecher.firstOrNull { it.id == id }

    val themen = listOf(
        Thema("m1", "m", "Bruchrechnung", 7),
        Thema("m2", "m", "Prozent und Zinsen", 4),
        Thema("m3", "m", "Lineare Funktionen", 1),
        Thema("e1", "e", "Present Perfect", 6),
        Thema("e2", "e", "Unit 3 Vokabeln", 2),
        Thema("d1", "d", "Inhaltsangabe", 3),
        Thema("d2", "d", "Kommaregeln", 5),
        Thema("bio1", "bio", "Die Zelle", 2),
        Thema("ph1", "ph", "Stromkreis", 1),
        Thema("g1", "g", "Weimarer Republik", 3),
    )

    fun themenVon(fachId: String): List<Thema> = themen.filter { it.fachId == fachId }

    // --- Heute -------------------------------------------------------------

    const val DATUM = "Donnerstag, 10. September"

    /** Tagesziel, DESIGN.md 6.2. */
    const val ZIEL_HAUSAUFGABEN = 3
    const val ZIEL_HAUSAUFGABEN_ERLEDIGT = 2
    const val ZIEL_MINUTEN = 45
    const val ZIEL_MINUTEN_ERREICHT = 30

    val hausaufgaben = listOf(
        Hausaufgabe(
            id = "h1", fachId = "m",
            aufgabe = "Buch S. 84, Nr. 3a–d",
            faellig = Faelligkeit.Morgen,
            bezug = Seitenbezug("Mathe live 10", 84, 4),
        ),
        Hausaufgabe(
            id = "h2", fachId = "e",
            aufgabe = "Workbook p. 32, 1–4",
            faellig = Faelligkeit.Morgen,
            bezug = Seitenbezug("English G Access 6 Workbook", 32, 4),
        ),
        Hausaufgabe(
            id = "h3", fachId = "d",
            aufgabe = "Inhaltsangabe Kapitel 3",
            faellig = Faelligkeit.Ueberfaellig,
            hatFoto = true,
        ),
        Hausaufgabe(
            id = "h4", fachId = "bio",
            aufgabe = "Arbeitsblatt Zelle",
            faellig = Faelligkeit.Morgen,
            erledigt = true,
            ergebnis = "3 richtig, 1 Fehler",
        ),
    )

    val offeneHausaufgaben: List<Hausaufgabe> get() = hausaufgaben.filterNot { it.erledigt }

    // --- Zustandszeilen ----------------------------------------------------

    val sperreGesperrt: Sperrzustand = Sperrzustand.Gesperrt
    val sperreFrei: Sperrzustand = Sperrzustand.Frei("17:40")
    val sperreNachsperre: Sperrzustand = Sperrzustand.Nachsperre(18)

    val serverVerbunden: Serverzustand = Serverzustand.Verbunden
    val serverOffline: Serverzustand = Serverzustand.Offline(4)
    val serverKorrigiert: Serverzustand = Serverzustand.Korrigiert(2, 5)

    // --- Session -----------------------------------------------------------

    /**
     * Die Session aus DESIGN.md 6.6: Gesamtzeit 1 Std. 5 Min.
     * 20 + 15 + 20 Minuten Zyklen plus zwei Pausen zu je 5 Minuten (Mitte
     * des Bereichs 5 bis 15 waeren 10 - hier steht die Pause deshalb
     * ausdruecklich auf 5 bis 5, damit die Zahl im Wireframe aufgeht).
     */
    val sessionplan = Sessionplan(
        zyklen = listOf(
            Zyklus("z1", Zyklustyp.Hausaufgabe, "m", "Buch S. 84, Nr. 3a–d", 0, 20),
            Zyklus("z2", Zyklustyp.Blurting, "e", "Present Perfect", 10, 5),
            Zyklus("z3", Zyklustyp.Aufgaben, "m", "Bruchrechnung", 0, 20, aufgabenAnzahl = 6),
        ),
        pauseVonMinuten = 5,
        pauseBisMinuten = 5,
    )

    /** "Hausaufgaben starten" macht aus jeder offenen Hausaufgabe einen Zyklus. */
    val hausaufgabenSession: Sessionplan
        get() = Sessionplan(
            zyklen = offeneHausaufgaben.mapIndexed { index, hausaufgabe ->
                Zyklus(
                    id = "ha$index",
                    typ = Zyklustyp.Hausaufgabe,
                    fachId = hausaufgabe.fachId,
                    thema = hausaufgabe.aufgabe,
                    lernMinuten = 0,
                    aufgabenMinuten = 20,
                )
            },
        )

    // --- Aufgaben ----------------------------------------------------------

    val aufgaben = listOf(
        Aufgabe(
            id = "a1", quelle = "Mathe live 10, S. 84, Nr. 3a", nummer = "3a",
            text = "Kürze den Bruch 18/24 so weit wie möglich.",
            thema = "Bruchrechnung",
            loesung = "3/4, gekürzt durch 6",
        ),
        Aufgabe(
            id = "a2", quelle = "Mathe live 10, S. 84, Nr. 3b", nummer = "3b",
            text = "Kürze den Bruch 45/60 so weit wie möglich.",
            thema = "Bruchrechnung",
            loesung = "3/4, gekürzt durch 15",
        ),
        Aufgabe(
            id = "a3", quelle = "Mathe live 10, S. 84, Nr. 3c", nummer = "3c",
            text = "Kürze den Bruch 84/126 so weit wie möglich.",
            thema = "Bruchrechnung",
            loesung = "2/3, gekürzt durch 42",
        ),
        Aufgabe(
            id = "a4", quelle = "Mathe live 10, S. 84, Nr. 3d", nummer = "3d",
            text = "Ordne die gekürzten Brüche der Größe nach und begründe.",
            thema = "Bruchrechnung",
        ),
        Aufgabe(
            id = "a5", quelle = "Mathe live 10, S. 85, Nr. 7", nummer = "7",
            text = "Lies aus dem Diagramm ab, welcher Anteil auf Sport entfällt.",
            thema = "Bruchrechnung",
            hatBild = true,
        ),
        Aufgabe(
            id = "a6", quelle = "Arbeitsblatt Prozent, Nr. 2", nummer = "2",
            text = "Ein Pullover kostet nach 30 % Rabatt noch 42 Euro. Wie teuer war er vorher?",
            thema = "Prozent und Zinsen",
        ),
    )

    // --- Scans und Korrektur -----------------------------------------------

    val scans = listOf(
        Scan("s1", 1, Scanstand.Fertig),
        Scan("s2", 2, Scanstand.Laeuft),
        Scan("s3", 3, Scanstand.Wartet),
    )

    val scansMitFehler = listOf(
        Scan("s1", 1, Scanstand.Fertig),
        Scan("s2", 2, Scanstand.Unscharf),
    )

    val korrektur = listOf(
        Korrekturposten(
            id = "k1", nummer = "3a", quelle = "Mathe live 10, S. 84, Nr. 3a",
            deineAntwort = "3/4",
            kommentar = "Sauber gekürzt, der Rechenweg steht dabei.",
            sicherheit = Sicherheit.Sicher,
            vorschlag = Urteil.Richtig,
            loesung = "3/4, gekürzt durch 6",
            seite = 1,
        ),
        Korrekturposten(
            id = "k2", nummer = "3b", quelle = "Mathe live 10, S. 84, Nr. 3b",
            deineAntwort = "9/12",
            kommentar = "Nur durch 5 gekürzt. Da geht noch mehr, schau auf die 3.",
            sicherheit = Sicherheit.Sicher,
            vorschlag = Urteil.Fehler,
            loesung = "3/4, gekürzt durch 15",
            seite = 1,
        ),
        Korrekturposten(
            id = "k3", nummer = "3c", quelle = "Mathe live 10, S. 84, Nr. 3c",
            deineAntwort = "",
            kommentar = "Hier steht nichts. Aufgabe vergessen?",
            sicherheit = Sicherheit.Sicher,
            vorschlag = Urteil.Luecke,
            loesung = "2/3, gekürzt durch 42",
            seite = 2,
        ),
        Korrekturposten(
            id = "k4", nummer = "3d", quelle = "Mathe live 10, S. 84, Nr. 3d",
            deineAntwort = "2/3 < 3/4 weil 2/3 kleiner ist",
            kommentar = "Ergebnis stimmt, die Begründung ist noch dünn.",
            sicherheit = Sicherheit.Unsicher,
            vorschlag = Urteil.Richtig,
            seite = 2,
        ),
    )

    val markierungen = listOf(
        Markierung(1, 0.20f, 0.24f, richtig = true),
        Markierung(2, 0.24f, 0.45f, richtig = false),
        Markierung(3, 0.18f, 0.66f, richtig = true),
    )

    // --- Brain -------------------------------------------------------------

    val buecher = listOf(
        Buch(
            id = "b1", titel = "Mathe live 10", fachId = "m",
            seitenErfasst = 212, seitenGesamt = 248, aufgaben = 1340,
            seiten = beispielseiten(),
        ),
        Buch(
            id = "b2", titel = "English G Access 6", fachId = "e",
            seitenErfasst = 96, seitenGesamt = 210, aufgaben = 640,
        ),
        Buch(
            id = "b3", titel = "Deutschbuch 10", fachId = "d",
            seitenErfasst = 40, seitenGesamt = 264, aufgaben = 180,
        ),
    )

    /** Ein Ausschnitt eines Buchs fuers PageGrid, Seite 81 bis 104. */
    private fun beispielseiten(): List<Buchseite> = (81..104).map { nummer ->
        val stand = when {
            nummer == 95 -> Seitenstand.Abweichung
            nummer == 99 || nummer == 100 -> Seitenstand.Fehlt
            nummer % 7 == 0 -> Seitenstand.Importiert
            else -> Seitenstand.Erkannt
        }
        Buchseite(
            nummer = nummer,
            stand = stand,
            aufgaben = if (stand == Seitenstand.Erkannt) 4 + nummer % 5 else 0,
        )
    }

    val arbeitsblaetter = listOf(
        Arbeitsblatt("ab1", "Prozent und Zinsen", "m", "8. September", 12),
        Arbeitsblatt("ab2", "Die Zelle", "bio", "5. September", 9),
        Arbeitsblatt("ab3", "Kommaregeln", "d", "2. September", 14),
        Arbeitsblatt("ab4", "Present Perfect", "e", "28. August", 11),
    )

    // --- Import ------------------------------------------------------------

    const val IMPORT_HOCHGELADEN = 48
    const val IMPORT_GESAMT = 120

    val importWarteschlange = listOf(
        Scan("i1", 81, Scanstand.Fertig),
        Scan("i2", 82, Scanstand.Fertig),
        Scan("i3", 83, Scanstand.Laeuft),
        Scan("i4", 84, Scanstand.Wartet),
        Scan("i5", 85, Scanstand.Wartet),
        Scan("i6", 86, Scanstand.Wartet),
    )

    /** Abweichungen beim Pruefen, DESIGN.md 6.21. */
    data class Abweichung(val bild: Int, val erkannt: Int, val erwartet: Int)

    val abweichungen = listOf(
        Abweichung(bild = 12, erkannt = 96, erwartet = 95),
        Abweichung(bild = 31, erkannt = 116, erwartet = 114),
    )

    // --- Statistik ---------------------------------------------------------

    const val BIS_SILBER_STUNDEN = 21

    val woche = listOf(
        Tagesbilanz("Mo", 65, 48),
        Tagesbilanz("Di", 40, 72),
        Tagesbilanz("Mi", 0, 95),
        Tagesbilanz("Do", 95, 30),
        Tagesbilanz("Fr", 55, 25),
        Tagesbilanz("Sa", 125, 0),
        Tagesbilanz("So", 0, 0),
    )

    const val HAUSAUFGABEN_PUENKTLICH = 18
    const val HAUSAUFGABEN_GESAMT = 20

    val fachbilanzen = listOf(
        Fachbilanz("m", richtig = 84, fehler = 19, luecken = 6),
        Fachbilanz("e", richtig = 61, fehler = 12, luecken = 3),
        Fachbilanz("d", richtig = 38, fehler = 14, luecken = 5),
        Fachbilanz("bio", richtig = 27, fehler = 5, luecken = 1),
    )

    val schwaechen = listOf(
        Schwaeche("m1", "m", "Bruchrechnung", 7),
        Schwaeche("e1", "e", "Present Perfect", 6),
        Schwaeche("d2", "d", "Kommaregeln", 5),
    )

    const val NOTAUSGANG_WOCHE = 1
    const val SPERRE_AUS_MINUTEN = 0

    // --- Apps und Berechtigungen -------------------------------------------

    val apps = listOf(
        AppEintrag("Telefon", erlaubt = true, fest = true),
        AppEintrag("WhatsApp", erlaubt = true),
        AppEintrag("Family Link", erlaubt = true, fest = true),
    )

    val berechtigungen = listOf(
        Berechtigung("Bedienungshilfe", "Damit Relock gesperrte Apps schließen kann.", erteilt = false),
        Berechtigung("Geräte-Admin", "Damit Relock sich nicht einfach deinstallieren lässt.", erteilt = false),
        Berechtigung("Bitte nicht stören", "Damit dein Handy in Sessions still ist.", erteilt = false),
        Berechtigung("Nutzungszugriff", "Damit Relock merkt, welche App vorne ist.", erteilt = true),
        Berechtigung("Kamera", "Für die Box und zum Scannen deiner Seiten.", erteilt = true),
        Berechtigung("Benachrichtigungen", "Für Timer und fertige Korrekturen.", erteilt = true),
        Berechtigung("Akku-Optimierung aus", "Damit Timer und Sperre im Hintergrund laufen.", erteilt = false),
    )

    const val SERVER_ADRESSE = "192.168.178.20:8000"

    // --- Einstellungen -----------------------------------------------------

    const val STANDARD_ZYKLUS_MINUTEN = 25
    const val PAUSE_VON = 5
    const val PAUSE_BIS = 15
    const val NACHSPERRE_MINUTEN = 30
    const val VERSION = "1.0.0"

    // --- Update ------------------------------------------------------------

    val updateVerfuegbar: Updatezustand = Updatezustand.Verfuegbar(
        version = "1.1.0",
        groesse = "18 MB",
        aenderungen = listOf(
            "Karten drehen sich flüssiger",
            "Import erkennt Seitenzahlen zuverlässiger",
            "Nachsperre lässt sich pausieren, wenn du angerufen wirst",
            "Statistik zeigt jetzt auch die Scrollzeit pro App",
        ),
    )
}
