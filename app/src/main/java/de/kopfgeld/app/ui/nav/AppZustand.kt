package de.kopfgeld.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.kopfgeld.app.data.Aufgabenquelle
import de.kopfgeld.app.data.Aufgabenstand
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.data.Sperrstand
import de.kopfgeld.app.data.Urteil
import de.kopfgeld.app.data.Verbindungstest
import de.kopfgeld.app.data.Zyklus
import de.kopfgeld.app.data.Zyklustyp
import de.kopfgeld.app.feature.brain.BrainFachUiState
import de.kopfgeld.app.feature.brain.BrainFehlerUiState
import de.kopfgeld.app.feature.brain.BrainKartenUiState
import de.kopfgeld.app.feature.brain.BrainPodcastsUiState
import de.kopfgeld.app.feature.brain.BrainScansUiState
import de.kopfgeld.app.feature.brain.BrainThemaUiState
import de.kopfgeld.app.feature.brain.BrainUiState
import de.kopfgeld.app.feature.einstellungen.EinstellungenUiState
import de.kopfgeld.app.feature.fortschritt.FortschrittUiState
import de.kopfgeld.app.feature.heute.HeuteUiState
import de.kopfgeld.app.feature.heute.LaufendeSession
import de.kopfgeld.app.feature.importieren.ImportPruefenUiState
import de.kopfgeld.app.feature.importieren.ImportScannenUiState
import de.kopfgeld.app.feature.importieren.ImportWarteschlangeUiState
import de.kopfgeld.app.feature.karteikarten.KarteikartenUiState
import de.kopfgeld.app.feature.lernen.LernenUiState
import de.kopfgeld.app.feature.onboarding.OnboardingUiState
import de.kopfgeld.app.feature.probearbeit.ProbearbeitEinrichtenUiState
import de.kopfgeld.app.feature.probearbeit.ProbearbeitErgebnisUiState
import de.kopfgeld.app.feature.probearbeit.ProbearbeitLaeuftUiState
import de.kopfgeld.app.feature.probearbeit.ProbearbeitWartetUiState
import de.kopfgeld.app.feature.session.AufgabenErhaltenUiState
import de.kopfgeld.app.feature.session.AufgabenauswahlUiState
import de.kopfgeld.app.feature.session.BoxAufstellenUiState
import de.kopfgeld.app.feature.session.Boxstand
import de.kopfgeld.app.feature.session.Flaechenstand
import de.kopfgeld.app.feature.session.HineinlegenUiState
import de.kopfgeld.app.feature.session.InDerBoxUiState
import de.kopfgeld.app.feature.session.PauseUiState
import de.kopfgeld.app.feature.session.ScannenUiState
import de.kopfgeld.app.feature.session.SessionEndeUiState
import de.kopfgeld.app.feature.session.SessionPlanenUiState
import de.kopfgeld.app.feature.session.SperrbildschirmUiState
import de.kopfgeld.app.feature.session.VerbesserungUiState
import de.kopfgeld.app.feature.session.WeiterUiState
import de.kopfgeld.app.feature.session.ZyklusSheetUiState

/**
 * Der vorlaeufige Zustandshalter fuer Phase 1.
 *
 * Er spielt vorruebergehend den ViewModel: die Screens bleiben zustandslos
 * (siehe UiState-Pattern), aber die Buttons tun trotzdem etwas. In Phase 2
 * wird diese Klasse durch echte ViewModels ersetzt, ohne dass ein einziger
 * Screen angefasst werden muss.
 *
 * Bewusst keine Logik hier drin, nur Umschalten von Anzeigezustaenden.
 */
class AppZustand {

    // --- Zustand pro Screen ------------------------------------------------

    var onboarding by mutableStateOf(OnboardingUiState())
    var heute by mutableStateOf(HeuteUiState())
    var lernen by mutableStateOf(LernenUiState())
    var planen by mutableStateOf(SessionPlanenUiState())
    var aufgabenauswahl by mutableStateOf(AufgabenauswahlUiState())
    var boxAufstellen by mutableStateOf(BoxAufstellenUiState())
    var hineinlegen by mutableStateOf(HineinlegenUiState())
    var inDerBox by mutableStateOf(InDerBoxUiState())
    var aufgabenErhalten by mutableStateOf(AufgabenErhaltenUiState())
    var scannen by mutableStateOf(ScannenUiState())
    var weiter by mutableStateOf(WeiterUiState())
    var pause by mutableStateOf(PauseUiState())
    var verbesserung by mutableStateOf(VerbesserungUiState())
    var sessionEnde by mutableStateOf(SessionEndeUiState())
    var sperrbildschirm by mutableStateOf(SperrbildschirmUiState())
    var brain by mutableStateOf(BrainUiState())
    var brainFach by mutableStateOf(BrainFachUiState())
    var brainThema by mutableStateOf(BrainThemaUiState())
    var brainScans by mutableStateOf(BrainScansUiState())
    var brainKarten by mutableStateOf(BrainKartenUiState())
    var brainFehler by mutableStateOf(BrainFehlerUiState())
    var brainPodcasts by mutableStateOf(BrainPodcastsUiState())
    var importScannen by mutableStateOf(ImportScannenUiState())
    var importWarteschlange by mutableStateOf(ImportWarteschlangeUiState())
    var importPruefen by mutableStateOf(ImportPruefenUiState())
    var probearbeitEinrichten by mutableStateOf(ProbearbeitEinrichtenUiState())
    var probearbeitLaeuft by mutableStateOf(ProbearbeitLaeuftUiState())
    var probearbeitWartet by mutableStateOf(ProbearbeitWartetUiState())
    var probearbeitErgebnis by mutableStateOf(ProbearbeitErgebnisUiState())
    var karteikarten by mutableStateOf(KarteikartenUiState())
    var fortschritt by mutableStateOf(FortschrittUiState())
    var einstellungen by mutableStateOf(EinstellungenUiState())

    /** Offenes Zyklus-Sheet, null wenn keins offen ist. */
    var zyklusSheet by mutableStateOf<ZyklusSheetUiState?>(null)

    /**
     * Welche Phase gerade in der Box laeuft. Der Zyklus hat zwei:
     * erst die Lernphase, dann - nach "Aufgaben erhalten" - die Aufgabenphase.
     * Danach wird gescannt.
     */
    var boxPhaseIstLernphase by mutableStateOf(true)

    /** Zuletzt geloeschter Zyklus, fuer "Rueckgaengig". */
    private var geloeschterZyklus: Pair<Int, Zyklus>? = null

    // --- Onboarding --------------------------------------------------------

    fun onboardingSeite(seite: Int) {
        onboarding = onboarding.copy(seite = seite.coerceIn(0, onboarding.letzteSeite))
    }

    fun onboardingWeiter() = onboardingSeite(onboarding.seite + 1)

    fun onboardingZurueck() = onboardingSeite(onboarding.seite - 1)

    fun fachUmschalten(fachId: String) {
        onboarding = onboarding.copy(
            faecher = onboarding.faecher.map {
                if (it.id == fachId) it.copy(aktiv = !it.aktiv) else it
            },
        )
    }

    fun pruefungsfachUmschalten(fachId: String) {
        onboarding = onboarding.copy(
            faecher = onboarding.faecher.map {
                if (it.id == fachId) it.copy(istPruefungsfach = !it.istPruefungsfach) else it
            },
        )
    }

    /** Tippt man auf ein Stundenplanfeld, rueckt es ein Fach weiter. */
    fun stundenplanfeld(tag: Int, stunde: Int) {
        val reihenfolge = listOf<String?>(null) + onboarding.faecher.map { it.id }
        onboarding = onboarding.copy(
            stundenplan = onboarding.stundenplan.map { feld ->
                if (feld.tag == tag && feld.stunde == stunde) {
                    val jetzt = reihenfolge.indexOf(feld.fachId).coerceAtLeast(0)
                    feld.copy(fachId = reihenfolge[(jetzt + 1) % reihenfolge.size])
                } else {
                    feld
                }
            },
        )
    }

    fun appUmschalten(name: String) {
        onboarding = onboarding.copy(
            gesperrteApps = onboarding.gesperrteApps.map {
                if (it.name == name) it.copy(gesperrt = !it.gesperrt) else it
            },
            erlaubteApps = onboarding.erlaubteApps.map {
                if (it.name == name) it.copy(gesperrt = !it.gesperrt) else it
            },
        )
    }

    fun serverAdresse(adresse: String) {
        onboarding = onboarding.copy(
            serverAdresse = adresse,
            verbindungstest = Verbindungstest.Ungeprueft,
        )
    }

    /** Mock-Zustaende: prueft, verbunden, nicht erreichbar - im Kreis. */
    fun verbindungTesten() {
        onboarding = onboarding.copy(
            verbindungstest = when (onboarding.verbindungstest) {
                Verbindungstest.Ungeprueft -> Verbindungstest.Prueft
                Verbindungstest.Prueft -> Verbindungstest.Verbunden
                Verbindungstest.Verbunden -> Verbindungstest.NichtErreichbar
                Verbindungstest.NichtErreichbar -> Verbindungstest.Prueft
            },
        )
    }

    fun berechtigungUmschalten(name: String) {
        onboarding = onboarding.copy(
            berechtigungen = onboarding.berechtigungen.map {
                if (it.name == name) it.copy(erteilt = !it.erteilt) else it
            },
        )
    }

    // --- Heute -------------------------------------------------------------

    fun planpostenAnhaken(id: String) {
        heute = heute.copy(
            plan = heute.plan.map {
                if (it.id == id) it.copy(erledigt = !it.erledigt) else it
            },
        )
    }

    // --- Session planen ----------------------------------------------------

    fun zyklusSheetOeffnen(zyklus: Zyklus) {
        zyklusSheet = ZyklusSheetUiState(zyklus)
    }

    fun zyklusSheetSchliessen() {
        zyklusSheet = null
    }

    private fun sheetAendern(block: (Zyklus) -> Zyklus) {
        val offen = zyklusSheet ?: return
        zyklusSheet = offen.copy(zyklus = block(offen.zyklus))
    }

    fun sheetFach(fachId: String) = sheetAendern {
        it.copy(fachId = fachId, themaIds = emptyList())
    }

    fun sheetThemaUmschalten(themaId: String) = sheetAendern { zyklus ->
        val neu = if (themaId in zyklus.themaIds) {
            zyklus.themaIds - themaId
        } else {
            zyklus.themaIds + themaId
        }
        zyklus.copy(themaIds = neu)
    }

    fun sheetTyp(typ: Zyklustyp) = sheetAendern { zyklus ->
        zyklus.copy(
            typ = typ,
            // Karteikarten laufen in der App, ohne Aufgabenphase in der Box.
            aufgabenphaseMinuten = if (typ.hatBoxAufgabenphase) {
                zyklus.aufgabenphaseMinuten
            } else {
                0
            },
        )
    }

    fun sheetTypenMischen(an: Boolean) = sheetAendern { it.copy(typenMischen = an) }

    fun sheetLernphase(minuten: Int) = sheetAendern { it.copy(lernphaseMinuten = minuten) }

    fun sheetAufgabenphase(minuten: Int) = sheetAendern {
        it.copy(aufgabenphaseMinuten = minuten)
    }

    fun sheetAufgabenAutomatisch(automatisch: Boolean) = sheetAendern {
        it.copy(aufgabenAutomatisch = automatisch)
    }

    fun sheetUebernehmen() {
        val offen = zyklusSheet ?: return
        val neueZyklen = planen.plan.zyklen.map { vorhanden ->
            if (vorhanden.id == offen.zyklus.id) offen.zyklus else vorhanden
        }
        planen = planen.copy(plan = planen.plan.copy(zyklen = neueZyklen))
        zyklusSheet = null
    }

    fun zyklusHinzufuegen() {
        val neu = Zyklus(
            id = "z${System.currentTimeMillis()}",
            fachId = FakeData.faecher.first { it.aktiv }.id,
            themaIds = emptyList(),
            typ = Zyklustyp.Aufgaben,
            lernphaseMinuten = 0,
            aufgabenphaseMinuten = einstellungen.standardZyklusMinuten,
        )
        planen = planen.copy(plan = planen.plan.copy(zyklen = planen.plan.zyklen + neu))
        zyklusSheetOeffnen(neu)
    }

    fun zyklusLoeschen(id: String) {
        val index = planen.plan.zyklen.indexOfFirst { it.id == id }
        if (index < 0) return
        geloeschterZyklus = index to planen.plan.zyklen[index]
        planen = planen.copy(
            plan = planen.plan.copy(zyklen = planen.plan.zyklen.filterNot { it.id == id }),
        )
    }

    fun zyklusLoeschenRueckgaengig() {
        val (index, zyklus) = geloeschterZyklus ?: return
        val liste = planen.plan.zyklen.toMutableList()
        liste.add(index.coerceIn(0, liste.size), zyklus)
        planen = planen.copy(plan = planen.plan.copy(zyklen = liste))
        geloeschterZyklus = null
    }

    fun zyklusVerschieben(von: Int, nach: Int) {
        val liste = planen.plan.zyklen.toMutableList()
        if (von !in liste.indices || nach !in liste.indices) return
        liste.add(nach, liste.removeAt(von))
        planen = planen.copy(plan = planen.plan.copy(zyklen = liste))
    }

    fun vorlageUebernehmen(planName: String) {
        planen = planen.copy(plan = planen.plan.copy(name = planName))
    }

    // --- Aufgabenauswahl ---------------------------------------------------

    fun aufgabeUmschalten(id: String) {
        val jetzt = aufgabenauswahl.ausgewaehlt
        aufgabenauswahl = aufgabenauswahl.copy(
            ausgewaehlt = if (id in jetzt) jetzt - id else jetzt + id,
        )
    }

    fun quellenfilter(quelle: Aufgabenquelle) {
        val jetzt = aufgabenauswahl.quellenfilter
        aufgabenauswahl = aufgabenauswahl.copy(
            quellenfilter = if (quelle in jetzt) jetzt - quelle else jetzt + quelle,
        )
    }

    fun standfilter(stand: Aufgabenstand) {
        val jetzt = aufgabenauswahl.standfilter
        aufgabenauswahl = aufgabenauswahl.copy(
            standfilter = if (stand in jetzt) jetzt - stand else jetzt + stand,
        )
    }

    fun sortierungUmschalten() {
        aufgabenauswahl = aufgabenauswahl.copy(fehlerZuerst = !aufgabenauswahl.fehlerZuerst)
    }

    // --- Box ---------------------------------------------------------------

    fun boxVerschieben(dx: androidx.compose.ui.unit.Dp, dy: androidx.compose.ui.unit.Dp) {
        boxAufstellen = boxAufstellen.copy(
            versatzX = boxAufstellen.versatzX + dx,
            versatzY = boxAufstellen.versatzY + dy,
        )
    }

    fun boxGroesse(dx: androidx.compose.ui.unit.Dp, dy: androidx.compose.ui.unit.Dp) {
        boxAufstellen = boxAufstellen.copy(
            boxBreite = (boxAufstellen.boxBreite + dx).coerceIn(
                androidx.compose.ui.unit.Dp(90f),
                androidx.compose.ui.unit.Dp(320f),
            ),
            boxHoehe = (boxAufstellen.boxHoehe + dy).coerceIn(
                androidx.compose.ui.unit.Dp(70f),
                androidx.compose.ui.unit.Dp(320f),
            ),
        )
    }

    fun flaechenstandWechseln() {
        boxAufstellen = boxAufstellen.copy(
            stand = when (boxAufstellen.stand) {
                Flaechenstand.Sucht -> Flaechenstand.Gefunden
                Flaechenstand.Gefunden -> Flaechenstand.NichtErkannt
                Flaechenstand.NichtErkannt -> Flaechenstand.Sucht
            },
        )
    }

    fun countdownStarten() {
        hineinlegen = hineinlegen.copy(erkannt = true, countdown = 5)
    }

    fun countdownWeiter(): Boolean {
        val jetzt = hineinlegen.countdown ?: return false
        return if (jetzt <= 1) {
            hineinlegen = hineinlegen.copy(erkannt = false, countdown = null)
            true
        } else {
            hineinlegen = hineinlegen.copy(countdown = jetzt - 1)
            false
        }
    }

    fun boxstand(stand: Boxstand) {
        inDerBox = inDerBox.copy(stand = stand)
    }

    // --- Scannen und danach -------------------------------------------------

    fun scanHinzufuegen() {
        val naechste = (scannen.scans.maxOfOrNull { it.seitennummer } ?: 0) + 1
        scannen = scannen.copy(
            scans = scannen.scans + de.kopfgeld.app.data.Scan(
                id = "s$naechste-${System.currentTimeMillis()}",
                seitennummer = naechste,
                stand = de.kopfgeld.app.data.Scanstand.Wartet,
                herkunft = "Mathe, Zyklus 1",
            ),
        )
    }

    fun scanEntfernen(id: String) {
        scannen = scannen.copy(scans = scannen.scans.filterNot { it.id == id })
    }

    fun pausenwahl(minuten: Int) {
        weiter = weiter.copy(pausenwahl = minuten)
    }

    // --- Verbesserung ------------------------------------------------------

    fun urteilSetzen(postenId: String, urteil: Urteil) {
        verbesserung = verbesserung.copy(urteile = verbesserung.urteile + (postenId to urteil))
    }

    fun karteUmschalten(postenId: String) {
        val jetzt = verbesserung.zuKarte
        verbesserung = verbesserung.copy(
            zuKarte = if (postenId in jetzt) jetzt - postenId else jetzt + postenId,
        )
    }

    fun verbesserungWeiter() {
        verbesserung = verbesserung.copy(
            index = (verbesserung.index + 1).coerceAtMost(verbesserung.posten.lastIndex),
        )
    }

    fun verbesserungZuruecksetzen() {
        verbesserung = verbesserung.copy(index = 0)
    }

    // --- Karteikarten ------------------------------------------------------

    fun karteUmdrehen() {
        karteikarten = karteikarten.copy(umgedreht = !karteikarten.umgedreht)
    }

    fun karteBewerten() {
        karteikarten = karteikarten.copy(
            index = karteikarten.index + 1,
            erledigt = karteikarten.erledigt + 1,
            umgedreht = false,
        )
    }

    fun karteikartenZuruecksetzen() {
        karteikarten = KarteikartenUiState()
    }

    // --- Brain -------------------------------------------------------------

    fun brainFachWaehlen(fachId: String) {
        brainFach = BrainFachUiState(FakeData.fach(fachId))
    }

    fun brainThemaWaehlen(themaId: String) {
        brainThema = BrainThemaUiState(FakeData.thema(themaId))
    }

    fun kartenfilterUmschalten() {
        brainKarten = brainKarten.copy(nurFaellige = !brainKarten.nurFaellige)
    }

    // --- Import ------------------------------------------------------------

    fun importWeiter() {
        importPruefen = importPruefen.copy(
            index = (importPruefen.index + 1).coerceAtMost(importPruefen.posten.size),
        )
    }

    fun importText(text: String) {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.map {
                if (it.id == posten.id) it.copy(text = text) else it
            },
        )
    }

    fun importFach(fachId: String) {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.map {
                if (it.id == posten.id) it.copy(fachId = fachId, themaIds = emptyList()) else it
            },
        )
    }

    fun importThemaUmschalten(themaId: String) {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.map {
                if (it.id != posten.id) {
                    it
                } else {
                    val neu = if (themaId in it.themaIds) {
                        it.themaIds - themaId
                    } else {
                        it.themaIds + themaId
                    }
                    it.copy(themaIds = neu)
                }
            },
        )
    }

    fun importTyp(typ: String) {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.map {
                if (it.id == posten.id) it.copy(typ = typ) else it
            },
        )
    }

    fun importLoesungUmschalten() {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.map {
                if (it.id == posten.id) it.copy(istLoesung = !it.istLoesung) else it
            },
        )
    }

    fun importVerwerfen() {
        val posten = importPruefen.aktuelle ?: return
        importPruefen = importPruefen.copy(
            posten = importPruefen.posten.filterNot { it.id == posten.id },
        )
    }

    // --- Probearbeit -------------------------------------------------------

    fun probearbeitVorlage(name: String, minuten: Int) {
        probearbeitEinrichten = probearbeitEinrichten.copy(titel = name, dauerMinuten = minuten)
    }

    fun echteNote(note: Double) {
        probearbeitErgebnis = probearbeitErgebnis.copy(
            arbeit = probearbeitErgebnis.arbeit.copy(echteNote = note),
        )
    }

    // --- Einstellungen -----------------------------------------------------

    fun pauseBereich(von: Int, bis: Int) {
        einstellungen = einstellungen.copy(pauseVon = von, pauseBis = bis)
        planen = planen.copy(
            plan = planen.plan.copy(pauseVonMinuten = von, pauseBisMinuten = bis),
        )
    }

    fun speicherzeit(minuten: Int) {
        einstellungen = einstellungen.copy(speicherzeitMinuten = minuten)
        planen = planen.copy(plan = planen.plan.copy(speicherzeitMinuten = minuten))
        sessionEnde = sessionEnde.copy(
            speicherzeitGesamtMinuten = minuten,
            speicherzeitRestSekunden = minuten * 60,
        )
    }

    fun standardZyklus(minuten: Int) {
        einstellungen = einstellungen.copy(standardZyklusMinuten = minuten)
    }

    // --- Demozustaende aus dem Screen-Katalog -------------------------------

    /**
     * Setzt einen bestimmten Zustand, damit der Screen-Katalog auch
     * Situationen zeigen kann, die man ohne Logik nie erreicht.
     */
    fun demozustandSetzen(schluessel: String?) {
        val s = schluessel ?: return
        when (s) {
            "heute.normal" -> heute = HeuteUiState()
            "heute.laeuft" -> heute = HeuteUiState(
                laufendeSession = LaufendeSession(2, 3, "12:40"),
            )

            "heute.erledigt" -> heute = HeuteUiState(
                plan = FakeData.tagesplan.map {
                    if (it.art == de.kopfgeld.app.data.Planart.Schlafenszeit) {
                        it
                    } else {
                        it.copy(erledigt = true)
                    }
                },
                sperre = Sperrstand.Speicherzeit(restMinuten = 6, gesamtMinuten = 30),
            )

            "heute.offline" -> heute = HeuteUiState(
                server = Serverstand.NichtErreichbar(wartendeScans = 4),
                sperre = Sperrstand.Gesperrt,
            )

            "box.sucht" -> boxAufstellen = boxAufstellen.copy(stand = Flaechenstand.Sucht)
            "box.gefunden" -> boxAufstellen = boxAufstellen.copy(stand = Flaechenstand.Gefunden)
            "box.nichterkannt" ->
                boxAufstellen = boxAufstellen.copy(stand = Flaechenstand.NichtErkannt)

            "hineinlegen.warte" -> hineinlegen = HineinlegenUiState()
            "hineinlegen.countdown" ->
                hineinlegen = HineinlegenUiState(erkannt = true, countdown = 3)

            "inbox.laeuft" -> inDerBox = inDerBox.copy(stand = Boxstand.Laeuft)
            "inbox.unterbrochen" -> inDerBox = inDerBox.copy(stand = Boxstand.Unterbrochen)
            "inbox.fertig" -> inDerBox = inDerBox.copy(stand = Boxstand.Fertig)

            "pause.analyse" -> pause = pause.copy(
                verbesserungBereit = false,
                server = Serverstand.Analysiert(2, 3, "Seiten"),
            )

            "pause.bereit" -> pause = pause.copy(
                verbesserungBereit = true,
                server = Serverstand.Verbunden,
            )

            else -> {
                if (s.startsWith("onboarding.")) {
                    val seite = s.removePrefix("onboarding.").toIntOrNull() ?: 0
                    onboardingSeite(seite)
                }
            }
        }
    }
}

@Composable
fun rememberAppZustand(): AppZustand = remember { AppZustand() }
