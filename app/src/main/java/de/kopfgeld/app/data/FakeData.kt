package de.kopfgeld.app.data

/*
 * Alle Daten der App in Phase 1. Keine Datenbank, kein Netz.
 *
 * Die Beispiele aus DESIGN.md 7 sind hier woertlich hinterlegt, damit die
 * Screens genau das zeigen, was in den Wireframes steht:
 *   Donnerstag, 10. September, Guthaben 42 Min, Speicherzeit noch 18 Min,
 *   Englisch-Arbeit in 9 Tagen, 38 faellige Karten, 2 offene Korrekturen.
 */
object FakeData {

    // --- Faecher -----------------------------------------------------------

    val faecher = listOf(
        Fach("d", "Deutsch", "D", istPruefungsfach = true),
        Fach("m", "Mathe", "M", istPruefungsfach = true),
        Fach("e", "Englisch", "E", istPruefungsfach = true),
        Fach("ph", "Physik", "Ph", istPruefungsfach = false),
        Fach("g", "Geschichte", "G", istPruefungsfach = false),
        Fach("bio", "Biologie", "Bio", istPruefungsfach = false),
    )

    fun fach(id: String): Fach = faecher.first { it.id == id }

    fun fachOderNull(id: String?): Fach? = faecher.firstOrNull { it.id == id }

    // --- Themen ------------------------------------------------------------

    val themen = listOf(
        // Mathe
        Thema("m1", "m", "Brüche", Themastand.Gelernt, 14, 9),
        Thema("m2", "m", "Prozent", Themastand.Neu, 11, 6),
        Thema("m3", "m", "Zinsrechnung", Themastand.Neu, 7, 4),
        Thema("m4", "m", "Lineare Funktionen", Themastand.Sitzt, 16, 12),
        Thema("m5", "m", "Satz des Pythagoras", Themastand.Gelernt, 9, 5),
        // Englisch
        Thema("e1", "e", "Unit 3 Vokabeln", Themastand.Gelernt, 4, 38),
        Thema("e2", "e", "Simple Past", Themastand.Sitzt, 12, 8),
        Thema("e3", "e", "Present Perfect", Themastand.Gelernt, 10, 11),
        Thema("e4", "e", "Reported Speech", Themastand.Neu, 8, 7),
        // Deutsch
        Thema("d1", "d", "Erörterung", Themastand.Gelernt, 6, 5),
        Thema("d2", "d", "Gedichtanalyse", Themastand.Neu, 5, 9),
        Thema("d3", "d", "Kommaregeln", Themastand.Sitzt, 13, 14),
        Thema("d4", "d", "Inhaltsangabe", Themastand.Gelernt, 7, 4),
        // Physik
        Thema("ph1", "ph", "Elektrischer Stromkreis", Themastand.Gelernt, 9, 6),
        Thema("ph2", "ph", "Optik", Themastand.Neu, 6, 3),
        // Geschichte
        Thema("g1", "g", "Weimarer Republik", Themastand.Neu, 8, 21),
        Thema("g2", "g", "Industrialisierung", Themastand.Gelernt, 6, 13),
        // Biologie
        Thema("bio1", "bio", "Genetik", Themastand.Gelernt, 10, 17),
        Thema("bio2", "bio", "Ökosystem See", Themastand.Neu, 5, 8),
    )

    fun themenVon(fachId: String): List<Thema> = themen.filter { it.fachId == fachId }

    fun thema(id: String): Thema = themen.first { it.id == id }

    fun themenNamen(ids: List<String>): String =
        ids.mapNotNull { id -> themen.firstOrNull { it.id == id }?.name }.joinToString(", ")

    // --- Aufgaben ----------------------------------------------------------

    val aufgaben = listOf(
        Aufgabe(
            id = "a1", fachId = "m", themaIds = listOf("m1"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 84, Nr. 3a–d",
            text = "Kürze die Brüche so weit wie möglich und gib jeweils an, mit welcher Zahl du gekürzt hast.",
            typ = "Rechnen", stand = Aufgabenstand.Fehler, hatLoesung = true, geschaetzteMinuten = 4,
        ),
        Aufgabe(
            id = "a2", fachId = "m", themaIds = listOf("m1"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 84, Nr. 4",
            text = "Ordne die Brüche der Größe nach und begründe deine Reihenfolge in einem Satz.",
            typ = "Begründen", stand = Aufgabenstand.Neu, hatLoesung = true, geschaetzteMinuten = 5,
        ),
        Aufgabe(
            id = "a3", fachId = "m", themaIds = listOf("m2"),
            quelle = Aufgabenquelle.Arbeitsblatt,
            quellenangabe = "Arbeitsblatt Prozent, Aufgabe 2",
            text = "Ein Pullover kostet nach 30 % Rabatt noch 42 Euro. Wie teuer war er vorher?",
            typ = "Textaufgabe", stand = Aufgabenstand.Fehler, hatLoesung = false, geschaetzteMinuten = 4,
        ),
        Aufgabe(
            id = "a4", fachId = "m", themaIds = listOf("m2"),
            quelle = Aufgabenquelle.EigeneFrage,
            quellenangabe = "Eigene Frage",
            text = "Wie rechnet man den Grundwert aus?",
            typ = "Verstehen", stand = Aufgabenstand.Neu, hatLoesung = false, geschaetzteMinuten = 3,
        ),
        Aufgabe(
            id = "a5", fachId = "m", themaIds = listOf("m2", "m3"),
            quelle = Aufgabenquelle.AlteArbeit,
            quellenangabe = "Arbeit 12.03., Aufgabe 5",
            text = "Berechne die Zinsen für 800 Euro bei 2,5 % in sieben Monaten.",
            typ = "Rechnen", stand = Aufgabenstand.Geuebt, hatLoesung = true, geschaetzteMinuten = 4,
        ),
        Aufgabe(
            id = "a6", fachId = "m", themaIds = listOf("m1"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 86, Nr. 1b",
            text = "Wandle die gemischten Zahlen in unechte Brüche um.",
            typ = "Rechnen", stand = Aufgabenstand.Sitzt, hatLoesung = true, geschaetzteMinuten = 2,
        ),
        Aufgabe(
            id = "a7", fachId = "m", themaIds = listOf("m5"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 112, Nr. 7",
            text = "Prüfe, ob das Dreieck rechtwinklig ist, und schreibe deinen Rechenweg auf.",
            typ = "Begründen", stand = Aufgabenstand.Neu, hatLoesung = true, geschaetzteMinuten = 5,
        ),
        Aufgabe(
            id = "a8", fachId = "e", themaIds = listOf("e1"),
            quelle = Aufgabenquelle.Arbeitsblatt,
            quellenangabe = "Arbeitsblatt Unit 3, Aufgabe 1",
            text = "Fill in the missing words. Use each word only once.",
            typ = "Lücken", stand = Aufgabenstand.Geuebt, hatLoesung = true, geschaetzteMinuten = 4,
        ),
        Aufgabe(
            id = "a9", fachId = "e", themaIds = listOf("e3"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 61, Nr. 2",
            text = "Write five sentences about your weekend. Use the present perfect.",
            typ = "Schreiben", stand = Aufgabenstand.Fehler, hatLoesung = false, geschaetzteMinuten = 6,
        ),
        Aufgabe(
            id = "a10", fachId = "e", themaIds = listOf("e4"),
            quelle = Aufgabenquelle.AlteArbeit,
            quellenangabe = "Arbeit 05.05., Aufgabe 3",
            text = "Turn the direct speech into reported speech.",
            typ = "Umformen", stand = Aufgabenstand.Neu, hatLoesung = true, geschaetzteMinuten = 5,
        ),
        Aufgabe(
            id = "a11", fachId = "d", themaIds = listOf("d1"),
            quelle = Aufgabenquelle.EigeneFrage,
            quellenangabe = "Eigene Frage",
            text = "Wie baue ich eine Erörterung auf, wenn ich beide Seiten zeigen soll?",
            typ = "Verstehen", stand = Aufgabenstand.Neu, hatLoesung = false, geschaetzteMinuten = 4,
        ),
        Aufgabe(
            id = "a12", fachId = "d", themaIds = listOf("d3"),
            quelle = Aufgabenquelle.Arbeitsblatt,
            quellenangabe = "Arbeitsblatt Kommas, Aufgabe 4",
            text = "Setze die fehlenden Kommas und nenne jeweils die Regel.",
            typ = "Regeln", stand = Aufgabenstand.Geuebt, hatLoesung = true, geschaetzteMinuten = 5,
        ),
        Aufgabe(
            id = "a13", fachId = "ph", themaIds = listOf("ph1"),
            quelle = Aufgabenquelle.Buch,
            quellenangabe = "Buch S. 38, Nr. 2",
            text = "Zeichne den Stromkreis und berechne die Stromstärke.",
            typ = "Zeichnen", stand = Aufgabenstand.Neu, hatLoesung = true, geschaetzteMinuten = 6,
        ),
        Aufgabe(
            id = "a14", fachId = "g", themaIds = listOf("g1"),
            quelle = Aufgabenquelle.AlteArbeit,
            quellenangabe = "Arbeit 21.01., Aufgabe 2",
            text = "Nenne drei Gründe für das Scheitern der Weimarer Republik.",
            typ = "Nennen", stand = Aufgabenstand.Fehler, hatLoesung = false, geschaetzteMinuten = 5,
        ),
    )

    fun aufgabenVon(fachId: String): List<Aufgabe> = aufgaben.filter { it.fachId == fachId }

    fun aufgabenZuThemen(themaIds: List<String>): List<Aufgabe> =
        aufgaben.filter { aufgabe -> aufgabe.themaIds.any { it in themaIds } }

    /** Standardsortierung in der Aufgabenauswahl: Fehler zuerst. */
    fun fehlerZuerst(liste: List<Aufgabe>): List<Aufgabe> = liste.sortedBy {
        when (it.stand) {
            Aufgabenstand.Fehler -> 0
            Aufgabenstand.Neu -> 1
            Aufgabenstand.Geuebt -> 2
            Aufgabenstand.Sitzt -> 3
        }
    }

    // --- Zyklen und Sessions -----------------------------------------------

    /**
     * Die Session aus dem Wireframe in DESIGN.md 7.4.
     * 25 + 25 + 15 Minuten Zyklen, dazwischen zwei Pausen mit Mitte 10 Min.
     * Macht 85 Minuten, also "1 Std. 25 Min".
     */
    val beispielSession = Sessionplan(
        name = "Neue Session",
        zyklen = listOf(
            Zyklus(
                id = "z1", fachId = "m", themaIds = listOf("m1", "m2"),
                typ = Zyklustyp.Aufgaben,
                lernphaseMinuten = 5, aufgabenphaseMinuten = 20,
                aufgabenAutomatisch = false,
                aufgabenIds = listOf("a1", "a2", "a3", "a4", "a5", "a6"),
                typenMischen = true,
            ),
            Zyklus(
                id = "z2", fachId = "e", themaIds = listOf("e1"),
                typ = Zyklustyp.Lernen,
                lernphaseMinuten = 15, aufgabenphaseMinuten = 10,
                zusatz = "+ Karten",
            ),
            Zyklus(
                id = "z3", fachId = "d", themaIds = listOf("d1"),
                typ = Zyklustyp.LautErklaeren,
                lernphaseMinuten = 0, aufgabenphaseMinuten = 15,
            ),
        ),
    )

    val vorlagen = listOf(
        Vorlage(
            id = "v1", name = "Nach der Schule",
            beschreibung = "3 Zyklen, 70 Min",
            plan = beispielSession.copy(name = "Nach der Schule"),
        ),
        Vorlage(
            id = "v2", name = "Mathe-Marathon",
            beschreibung = "4 × 25",
            plan = Sessionplan(
                name = "Mathe-Marathon",
                zyklen = (1..4).map { nr ->
                    Zyklus(
                        id = "mm$nr", fachId = "m",
                        themaIds = listOf("m1", "m2", "m3", "m4")[nr - 1].let { listOf(it) },
                        typ = Zyklustyp.Aufgaben,
                        lernphaseMinuten = 0, aufgabenphaseMinuten = 25,
                        typenMischen = true,
                    )
                },
            ),
        ),
        Vorlage(
            id = "v3", name = "Englisch Testphase",
            beschreibung = "2 Zyklen, 55 Min",
            plan = Sessionplan(
                name = "Englisch Testphase",
                zyklen = listOf(
                    Zyklus(
                        id = "et1", fachId = "e", themaIds = listOf("e3", "e4"),
                        typ = Zyklustyp.Aufgaben,
                        lernphaseMinuten = 0, aufgabenphaseMinuten = 25,
                        typenMischen = true,
                    ),
                    Zyklus(
                        id = "et2", fachId = "e", themaIds = listOf("e1"),
                        typ = Zyklustyp.Karteikarten,
                        lernphaseMinuten = 20, aufgabenphaseMinuten = 0,
                    ),
                ),
            ),
        ),
    )

    // --- Tagesplan ---------------------------------------------------------

    const val heutigesDatum = "Donnerstag, 10. September"
    const val guthabenMinuten = 42
    const val faelligeKarten = 38
    const val offeneKorrekturen = 2
    const val schlafenszeit = "22:00"

    val tagesplan = listOf(
        Planposten(
            "p1", "Blurting Mathe", "Heute im Unterricht: Brüche",
            "10 Min", erledigt = false, art = Planart.Blurting,
        ),
        Planposten(
            "p2", "Blurting Englisch", "Heute im Unterricht: Present Perfect",
            "10 Min", erledigt = false, art = Planart.Blurting,
        ),
        Planposten(
            "p3", "Karteikarten", "38 fällig",
            "18 Min", erledigt = false, art = Planart.Karteikarten,
        ),
        Planposten(
            "p4", "Englisch Aufgaben", "Testphase: Übungsaufgaben Unit 3",
            "25 Min", erledigt = false, art = Planart.Aufgaben,
        ),
        Planposten(
            "p5", "2 Korrekturen offen", "von gestern, bereit zum Verbessern",
            "", erledigt = false, art = Planart.Verbesserung,
        ),
        Planposten(
            "p6", "Schlafenszeit ab $schlafenszeit", "",
            "", erledigt = false, art = Planart.Schlafenszeit,
        ),
    )

    val testphase = Testphase(fachId = "e", fachName = "Englisch", tageBisArbeit = 9)

    // --- Scans -------------------------------------------------------------

    val scansSession = listOf(
        Scan("s1", 1, Scanstand.Fertig, "Mathe, Zyklus 1"),
        Scan("s2", 2, Scanstand.Analysiert, "Mathe, Zyklus 1"),
        Scan("s3", 3, Scanstand.Wartet, "Mathe, Zyklus 1"),
    )

    val scansImport = listOf(
        Scan("i1", 84, Scanstand.Fertig, "Mathebuch"),
        Scan("i2", 85, Scanstand.Fertig, "Mathebuch"),
        Scan("i3", 86, Scanstand.Analysiert, "Mathebuch"),
        Scan("i4", 87, Scanstand.Wartet, "Mathebuch"),
        Scan("i5", 88, Scanstand.Wartet, "Mathebuch"),
    )

    val scansBrain = listOf(
        Scan("b1", 84, Scanstand.Fertig, "Mathebuch, 8. September"),
        Scan("b2", 85, Scanstand.Fertig, "Mathebuch, 8. September"),
        Scan("b3", 12, Scanstand.Fertig, "Arbeitsblatt Prozent, 9. September"),
        Scan("b4", 1, Scanstand.Wartet, "Arbeit 12.03., 9. September"),
        Scan("b5", 2, Scanstand.Wartet, "Arbeit 12.03., 9. September"),
    )

    // --- Verbesserung ------------------------------------------------------

    val korrekturposten = listOf(
        Korrekturposten(
            id = "k1", aufgabenNummer = "3a",
            quellenangabe = "Buch S. 84, Nr. 3a",
            aufgabentext = "Kürze den Bruch 18/24 so weit wie möglich.",
            vorschlag = Urteil.Richtig,
            notiz = "Sauber gekürzt, Rechenweg steht dran.",
            seitennummer = 1,
            markierungen = listOf(Markierung(1, 0.18f, 0.28f, 0.34f, Markierungsart.Unterstrich)),
        ),
        Korrekturposten(
            id = "k2", aufgabenNummer = "3b",
            quellenangabe = "Buch S. 84, Nr. 3b",
            aufgabentext = "Kürze den Bruch 45/60 so weit wie möglich.",
            vorschlag = Urteil.Fehler,
            notiz = "Nur durch 5 gekürzt. Geht noch weiter, schau auf die 3.",
            seitennummer = 1,
            markierungen = listOf(Markierung(2, 0.22f, 0.46f, 0.28f, Markierungsart.Kreis)),
            wirdZuKarte = true,
        ),
        Korrekturposten(
            id = "k3", aufgabenNummer = "3c",
            quellenangabe = "Buch S. 84, Nr. 3c",
            aufgabentext = "Kürze den Bruch 84/126 so weit wie möglich.",
            vorschlag = Urteil.Luecke,
            notiz = "Hier steht nichts. Aufgabe vergessen?",
            seitennummer = 2,
        ),
        Korrekturposten(
            id = "k4", aufgabenNummer = "2",
            quellenangabe = "Arbeitsblatt Prozent, Aufgabe 2",
            aufgabentext = "Ein Pullover kostet nach 30 % Rabatt noch 42 Euro. Wie teuer war er vorher?",
            vorschlag = Urteil.Fehler,
            notiz = "42 durch 0,7, nicht mal 1,3. Grundwert gesucht.",
            seitennummer = 2,
            markierungen = listOf(
                Markierung(3, 0.14f, 0.34f, 0.46f, Markierungsart.Kreis),
                Markierung(4, 0.14f, 0.52f, 0.30f, Markierungsart.Unterstrich),
            ),
            wirdZuKarte = true,
        ),
    )

    val offeneVerbesserungen = listOf(
        OffeneVerbesserung(
            "ov1", "Mathe: Brüche, Prozent", "gestern, 3 Seiten, 6 Aufgaben",
            anzahlAufgaben = 6, bereit = true,
        ),
        OffeneVerbesserung(
            "ov2", "Englisch: Present Perfect", "gestern, 2 Seiten, 4 Aufgaben",
            anzahlAufgaben = 4, bereit = true,
        ),
    )

    // --- Karteikarten ------------------------------------------------------

    val karteikarten = listOf(
        Karteikarte("kk1", "e", "e1", "to argue", "streiten, argumentieren", faellig = true),
        Karteikarte("kk2", "e", "e1", "reliable", "zuverlässig", faellig = true),
        Karteikarte("kk3", "e", "e1", "to achieve", "erreichen, schaffen", faellig = true),
        Karteikarte(
            "kk4", "m", "m2", "Wie rechnet man den Grundwert aus?",
            "Prozentwert geteilt durch Prozentsatz, also W ÷ p",
            faellig = true, herkunft = "aus Fehler vom 8. September",
        ),
        Karteikarte(
            "kk5", "m", "m1", "Wann ist ein Bruch vollständig gekürzt?",
            "Wenn Zähler und Nenner keinen gemeinsamen Teiler außer 1 mehr haben",
            faellig = true, herkunft = "aus Fehler vom 9. September",
        ),
        Karteikarte("kk6", "g", "g1", "Wann wurde die Weimarer Verfassung beschlossen?", "1919", faellig = true),
        Karteikarte("kk7", "d", "d3", "Komma vor „und“?", "Nur bei Hauptsatz + Hauptsatz mit eigenem Subjekt", faellig = false),
        Karteikarte("kk8", "bio", "bio1", "Was ist ein Genotyp?", "Die Gesamtheit der Erbanlagen eines Lebewesens", faellig = false),
    )

    val faelligeKarteikarten: List<Karteikarte> get() = karteikarten.filter { it.faellig }

    val wiederholungswahlen = listOf(
        Wiederholungswahl("Nochmal", "in 10 Min"),
        Wiederholungswahl("Schwer", "in 1 Tag"),
        Wiederholungswahl("Gut", "in 2 Tagen"),
        Wiederholungswahl("Leicht", "in 6 Tagen"),
    )

    // --- Fehler und Podcasts -----------------------------------------------

    val fehler = listOf(
        Fehlerposten("f1", "m", "m2", "Grundwert und Prozentwert verwechselt", "9. September", 3, hatKarte = true),
        Fehlerposten("f2", "m", "m1", "Nicht vollständig gekürzt", "9. September", 2, hatKarte = true),
        Fehlerposten("f3", "e", "e3", "Present Perfect mit Zeitangabe der Vergangenheit", "8. September", 4, hatKarte = false),
        Fehlerposten("f4", "d", "d3", "Komma vor erweitertem Infinitiv vergessen", "5. September", 2, hatKarte = false),
        Fehlerposten("f5", "g", "g1", "Jahreszahlen vertauscht", "2. September", 1, hatKarte = true),
    )

    val podcasts = listOf(
        Podcast("pc1", "Brüche und Prozent", "m", "Brüche, Prozent", Podcaststand.Importiert, "11:24"),
        Podcast("pc2", "Weimarer Republik", "g", "Weimarer Republik", Podcaststand.WartetAufImport, "–"),
        Podcast("pc3", "Erörterung aufbauen", "d", "Erörterung", Podcaststand.PaketBereit, "–"),
    )

    // --- Probearbeiten -----------------------------------------------------

    val probearbeiten = listOf(
        Probearbeit(
            id = "pa1", titel = "Alte Klassenarbeit Mathe", fachId = "m",
            dauerMinuten = 60, stand = Probearbeitstand.Bewertet, datum = "3. September", seiten = 4,
            geschaetzteNote = 3.0, echteNote = 3.0,
            raster = listOf(
                Rasterzeile("Rechenwege", 14, 20),
                Rasterzeile("Ergebnisse", 11, 18),
                Rasterzeile("Darstellung", 7, 10),
            ),
            kommentar = "Die Textaufgaben sitzen noch nicht. Rechenwege sind sauber.",
        ),
        Probearbeit(
            id = "pa2", titel = "Deutsch-Aufsatz: Erörterung", fachId = "d",
            dauerMinuten = 90, stand = Probearbeitstand.WartetAufBewertung, datum = "gestern", seiten = 5,
        ),
        Probearbeit(
            id = "pa3", titel = "Abschlussprüfung Englisch 2024", fachId = "e",
            dauerMinuten = 120, stand = Probearbeitstand.Bewertet, datum = "28. August", seiten = 6,
            geschaetzteNote = 2.0, echteNote = null,
            raster = listOf(
                Rasterzeile("Leseverstehen", 17, 20),
                Rasterzeile("Wortschatz", 12, 15),
                Rasterzeile("Schreiben", 18, 25),
                Rasterzeile("Grammatik", 13, 15),
            ),
            kommentar = "Schreiben braucht mehr Struktur, sonst stabil.",
        ),
    )

    /** Vorschlaege beim Einrichten einer Probearbeit. */
    val probearbeitVorlagen = listOf(
        "Alte Klassenarbeit" to 60,
        "Deutsch-Aufsatz" to 90,
        "Alte Abschlussprüfung" to 120,
    )

    // --- Fortschritt -------------------------------------------------------

    val ziele = listOf(
        Ziel("Abschluss 2027", "Schnitt in den Prüfungsfächern", 3, 6),
        Ziel("Diese Woche lernen", "5 Stunden geplant", 194, 300),
        Ziel("Karten ohne Rückstand", "38 von 38 fällig heute", 0, 38),
    )

    val notenverlaeufe = listOf(
        Notenverlauf(
            "m",
            listOf(
                Notenpunkt("12.03.", "Arbeit", 4.0),
                Notenpunkt("28.04.", "Arbeit", 3.0),
                Notenpunkt("17.06.", "Test", 3.0),
                Notenpunkt("03.09.", "Probe", 3.0),
            ),
            zielnote = 2.0,
        ),
        Notenverlauf(
            "e",
            listOf(
                Notenpunkt("05.05.", "Arbeit", 3.0),
                Notenpunkt("21.06.", "Test", 2.0),
                Notenpunkt("28.08.", "Probe", 2.0),
            ),
            zielnote = 2.0,
        ),
        Notenverlauf(
            "d",
            listOf(
                Notenpunkt("14.02.", "Arbeit", 3.0),
                Notenpunkt("19.05.", "Arbeit", 3.0),
                Notenpunkt("30.06.", "Test", 4.0),
            ),
            zielnote = 3.0,
        ),
    )

    val wochenbilanz = listOf(
        Tagesbilanz("Mo", 55, 72),
        Tagesbilanz("Di", 40, 95),
        Tagesbilanz("Mi", 0, 130),
        Tagesbilanz("Do", 75, 48),
        Tagesbilanz("Fr", 24, 88),
        Tagesbilanz("Sa", 0, 165),
        Tagesbilanz("So", 0, 0),
    )

    // --- Onboarding und Einstellungen --------------------------------------

    val wochentage = listOf("Mo", "Di", "Mi", "Do", "Fr")

    /** Stundenplan Mo–Fr, sechs Stunden. null heisst frei. */
    val stundenplan: List<Stundenplanfeld> = buildList {
        val raster = listOf(
            listOf("m", "d", "e", "ph", "g", null),
            listOf("e", "e", "m", "bio", "d", null),
            listOf("d", "m", "g", "e", "ph", "bio"),
            listOf("m", "e", "d", "bio", "g", null),
            listOf("ph", "d", "m", "e", null, null),
        )
        raster.forEachIndexed { tag, stunden ->
            stunden.forEachIndexed { stunde, fachId ->
                add(Stundenplanfeld(tag, stunde, fachId))
            }
        }
    }

    val gesperrteApps = listOf(
        AppEintrag("TikTok", gesperrt = true),
        AppEintrag("Instagram", gesperrt = true),
        AppEintrag("YouTube", gesperrt = true),
        AppEintrag("Snapchat", gesperrt = true),
    )

    val erlaubteApps = listOf(
        AppEintrag("WhatsApp", gesperrt = false),
        AppEintrag("WebUntis", gesperrt = false),
        AppEintrag("Telefon", gesperrt = false),
    )

    const val serverAdresse = "192.168.178.20:8000"

    val berechtigungen = listOf(
        Berechtigung("Bedienungshilfe", "Damit die App gesperrte Apps schließen kann.", erteilt = false),
        Berechtigung("Kamera", "Für die Box und zum Scannen deiner Seiten.", erteilt = true),
        Berechtigung("Bewegungssensoren", "Damit die App merkt, wenn du das Handy herausnimmst.", erteilt = true),
        Berechtigung("Akku-Optimierung aus", "Damit Timer und Sperre auch im Hintergrund laufen.", erteilt = false),
    )

    // --- Zustaende fuer die Statuszeilen ------------------------------------

    val sperrstandHeute: Sperrstand = Sperrstand.Speicherzeit(restMinuten = 18, gesamtMinuten = 30)
    val serverstandHeute: Serverstand = Serverstand.Verbunden
    val serverstandWartend: Serverstand = Serverstand.NichtErreichbar(wartendeScans = 4)
    val serverstandAnalyse: Serverstand = Serverstand.Analysiert(fertig = 2, gesamt = 5)
}
