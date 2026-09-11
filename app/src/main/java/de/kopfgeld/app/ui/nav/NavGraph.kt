package de.kopfgeld.app.ui.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import de.kopfgeld.app.feature.brain.BrainFachScreen
import de.kopfgeld.app.feature.brain.BrainFehlerScreen
import de.kopfgeld.app.feature.brain.BrainKartenScreen
import de.kopfgeld.app.feature.brain.BrainPodcastsScreen
import de.kopfgeld.app.feature.brain.BrainScansScreen
import de.kopfgeld.app.feature.brain.BrainScreen
import de.kopfgeld.app.feature.brain.BrainThemaScreen
import de.kopfgeld.app.feature.einstellungen.EinstellungenScreen
import de.kopfgeld.app.feature.fortschritt.FortschrittScreen
import de.kopfgeld.app.feature.heute.HeuteScreen
import de.kopfgeld.app.feature.importieren.ImportPruefenScreen
import de.kopfgeld.app.feature.importieren.ImportScannenScreen
import de.kopfgeld.app.feature.importieren.ImportWarteschlangeScreen
import de.kopfgeld.app.feature.karteikarten.KarteikartenScreen
import de.kopfgeld.app.feature.katalog.KatalogScreen
import de.kopfgeld.app.feature.lernen.LernenScreen
import de.kopfgeld.app.feature.onboarding.OnboardingScreen
import de.kopfgeld.app.feature.probearbeit.ProbearbeitEinrichtenScreen
import de.kopfgeld.app.feature.probearbeit.ProbearbeitErgebnisScreen
import de.kopfgeld.app.feature.probearbeit.ProbearbeitLaeuftScreen
import de.kopfgeld.app.feature.probearbeit.ProbearbeitWartetScreen
import de.kopfgeld.app.feature.session.AufgabenErhaltenScreen
import de.kopfgeld.app.feature.session.AufgabenauswahlScreen
import de.kopfgeld.app.feature.session.BoxAufstellenScreen
import de.kopfgeld.app.feature.session.Boxstand
import de.kopfgeld.app.feature.session.HineinlegenScreen
import de.kopfgeld.app.feature.session.InDerBoxScreen
import de.kopfgeld.app.feature.session.PauseScreen
import de.kopfgeld.app.feature.session.ScannenScreen
import de.kopfgeld.app.feature.session.SessionEndeScreen
import de.kopfgeld.app.feature.session.SessionPlanenScreen
import de.kopfgeld.app.feature.session.SperrbildschirmScreen
import de.kopfgeld.app.feature.session.VerbesserungScreen
import de.kopfgeld.app.feature.session.WeiterOderAufhoerenScreen
import kotlinx.coroutines.delay

/*
 * Der Navigationsgraph. Bewusst flach: jede Route ist direkt anspringbar,
 * damit der Screen-Katalog sie alle erreichen kann.
 */

internal fun NavGraphBuilder.tabZiele(
    navigation: NavHostController,
    zustand: AppZustand,
) {
    composable(Ziel.HEUTE) {
        HeuteScreen(
            state = zustand.heute,
            beiEinstellungen = { navigation.navigate(Ziel.EINSTELLUNGEN) },
            beiKatalog = { navigation.navigate(Ziel.KATALOG) },
            beiPostenAnhaken = zustand::planpostenAnhaken,
            beiPostenOeffnen = { posten ->
                when (posten.art) {
                    de.kopfgeld.app.data.Planart.Karteikarten ->
                        navigation.navigate(Ziel.KARTEIKARTEN)

                    de.kopfgeld.app.data.Planart.Verbesserung ->
                        navigation.navigate(Ziel.VERBESSERUNG)

                    de.kopfgeld.app.data.Planart.Schlafenszeit -> Unit
                    else -> navigation.navigate(Ziel.SESSION_PLANEN)
                }
            },
            beiVorschlagStarten = { navigation.navigate(Ziel.SESSION_PLANEN) },
            beiEigeneSession = { navigation.navigate(Ziel.SESSION_PLANEN) },
            beiLaufendeSession = { navigation.navigate(Ziel.IN_DER_BOX) },
        )
    }

    composable(Ziel.LERNEN) {
        LernenScreen(
            state = zustand.lernen,
            beiSessionPlanen = { navigation.navigate(Ziel.SESSION_PLANEN) },
            beiVorlage = { vorlage ->
                zustand.vorlageUebernehmen(vorlage.name)
                navigation.navigate(Ziel.SESSION_PLANEN)
            },
            beiProbearbeit = { navigation.navigate(Ziel.PROBEARBEIT_EINRICHTEN) },
            beiKarteikarten = { navigation.navigate(Ziel.KARTEIKARTEN) },
            beiVerbesserungen = { navigation.navigate(Ziel.VERBESSERUNG) },
        )
    }

    composable(Ziel.BRAIN) {
        BrainScreen(
            state = zustand.brain,
            beiFach = { fach ->
                zustand.brainFachWaehlen(fach.id)
                navigation.navigate(Ziel.BRAIN_FACH)
            },
            beiImport = { navigation.navigate(Ziel.IMPORT_SCANNEN) },
            beiScans = { navigation.navigate(Ziel.BRAIN_SCANS) },
            beiKarten = { navigation.navigate(Ziel.BRAIN_KARTEN) },
            beiFehler = { navigation.navigate(Ziel.BRAIN_FEHLER) },
            beiPodcasts = { navigation.navigate(Ziel.BRAIN_PODCASTS) },
        )
    }

    composable(Ziel.FORTSCHRITT) {
        FortschrittScreen(
            state = zustand.fortschritt,
            beiVerlaufWechseln = { index ->
                zustand.fortschritt = zustand.fortschritt.copy(gewaehlterVerlauf = index)
            },
            beiProbearbeit = { navigation.navigate(Ziel.PROBEARBEIT_ERGEBNIS) },
        )
    }
}

internal fun NavGraphBuilder.vollbildZiele(
    navigation: NavHostController,
    zustand: AppZustand,
) {
    // --- Onboarding --------------------------------------------------------

    composable(Ziel.ONBOARDING) {
        OnboardingScreen(
            state = zustand.onboarding,
            beiWeiter = zustand::onboardingWeiter,
            beiZurueck = zustand::onboardingZurueck,
            beiFachUmschalten = zustand::fachUmschalten,
            beiPruefungsfachUmschalten = zustand::pruefungsfachUmschalten,
            beiStundenplanfeld = zustand::stundenplanfeld,
            beiAppUmschalten = zustand::appUmschalten,
            beiServerAdresse = zustand::serverAdresse,
            beiVerbindungTesten = zustand::verbindungTesten,
            beiSpaeterEinrichten = zustand::onboardingWeiter,
            beiBerechtigung = zustand::berechtigungUmschalten,
            beiFertig = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.ONBOARDING) { inclusive = true }
                }
            },
        )
    }

    // --- Einstellungen und Katalog -----------------------------------------

    composable(Ziel.EINSTELLUNGEN) {
        EinstellungenScreen(
            state = zustand.einstellungen,
            beiZurueck = { navigation.popBackStack() },
            beiPause = zustand::pauseBereich,
            beiSpeicherzeit = zustand::speicherzeit,
            beiStandardZyklus = zustand::standardZyklus,
            beiSchlafenszeit = {},
            beiServer = { navigation.navigate(Ziel.ONBOARDING) },
            beiApps = { navigation.navigate(Ziel.ONBOARDING) },
            beiFaecher = { navigation.navigate(Ziel.ONBOARDING) },
            beiBerechtigungen = { navigation.navigate(Ziel.ONBOARDING) },
            beiOnboardingErneut = {
                zustand.onboardingSeite(0)
                navigation.navigate(Ziel.ONBOARDING)
            },
            beiKatalog = { navigation.navigate(Ziel.KATALOG) },
        )
    }

    composable(Ziel.KATALOG) {
        KatalogScreen(
            beiZurueck = { navigation.popBackStack() },
            beiEintrag = { eintrag ->
                zustand.demozustandSetzen(eintrag.zustand)
                navigation.navigate(eintrag.route)
            },
        )
    }

    composable(Ziel.SPERRBILDSCHIRM) {
        SperrbildschirmScreen(
            state = zustand.sperrbildschirm,
            beiZurueck = { navigation.popBackStack() },
            beiGuthabenEinsetzen = { navigation.popBackStack() },
        )
    }

    // --- Sessionplanung ----------------------------------------------------

    composable(Ziel.SESSION_PLANEN) {
        SessionPlanenScreen(
            state = zustand.planen,
            beiZurueck = { navigation.popBackStack() },
            beiZyklusBearbeiten = zustand::zyklusSheetOeffnen,
            beiZyklusHinzufuegen = zustand::zyklusHinzufuegen,
            beiZyklusLoeschen = zustand::zyklusLoeschen,
            beiLoeschenRueckgaengig = zustand::zyklusLoeschenRueckgaengig,
            beiZyklusVerschieben = zustand::zyklusVerschieben,
            beiPauseBearbeiten = { navigation.navigate(Ziel.EINSTELLUNGEN) },
            beiSpeicherzeit = { navigation.navigate(Ziel.EINSTELLUNGEN) },
            beiAlsVorlageSpeichern = { name -> zustand.vorlageUebernehmen(name) },
            beiStarten = { navigation.navigate(Ziel.BOX_AUFSTELLEN) },
        )
    }

    composable(Ziel.AUFGABENAUSWAHL) {
        AufgabenauswahlScreen(
            state = zustand.aufgabenauswahl,
            beiZurueck = { navigation.popBackStack() },
            beiAufgabeUmschalten = zustand::aufgabeUmschalten,
            beiQuellenfilter = zustand::quellenfilter,
            beiStandfilter = zustand::standfilter,
            beiSortierung = zustand::sortierungUmschalten,
            beiUebernehmen = { navigation.popBackStack() },
        )
    }

    // --- Session-Flow ------------------------------------------------------

    composable(Ziel.BOX_AUFSTELLEN) {
        BoxAufstellenScreen(
            state = zustand.boxAufstellen,
            beiAbbrechen = { navigation.popBackStack() },
            beiVerschieben = zustand::boxVerschieben,
            beiGroesse = zustand::boxGroesse,
            beiPlatzieren = { navigation.navigate(Ziel.HINEINLEGEN) },
            beiStandWechseln = zustand::flaechenstandWechseln,
        )
    }

    composable(Ziel.HINEINLEGEN) {
        // Der Countdown laeuft von 5 bis 1 und geht dann in die Box.
        LaunchedEffect(zustand.hineinlegen.countdown) {
            if (zustand.hineinlegen.countdown != null) {
                delay(1000)
                val fertig = zustand.countdownWeiter()
                if (fertig) {
                    zustand.boxstand(Boxstand.Laeuft)
                    navigation.navigate(Ziel.IN_DER_BOX) {
                        popUpTo(Ziel.HINEINLEGEN) { inclusive = true }
                    }
                }
            }
        }

        HineinlegenScreen(
            state = zustand.hineinlegen,
            beiHandyErkannt = zustand::countdownStarten,
            beiAbbrechen = { navigation.popBackStack() },
        )
    }

    composable(Ziel.IN_DER_BOX) {
        InDerBoxScreen(
            state = zustand.inDerBox,
            beiHerausnehmen = { zustand.boxstand(Boxstand.Fertig) },
            beiZuruecklegen = { zustand.boxstand(Boxstand.Laeuft) },
            beiZyklusAbbrechen = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.HEUTE) { inclusive = true }
                }
            },
            beiWeiter = {
                // Nach der Lernphase kommen die Aufgaben, nach der
                // Aufgabenphase wird gescannt (DESIGN.md 2.1).
                if (zustand.boxPhaseIstLernphase) {
                    navigation.navigate(Ziel.AUFGABEN_ERHALTEN)
                } else {
                    zustand.boxPhaseIstLernphase = true
                    navigation.navigate(Ziel.SCANNEN)
                }
            },
        )
    }

    composable(Ziel.AUFGABEN_ERHALTEN) {
        AufgabenErhaltenScreen(
            state = zustand.aufgabenErhalten,
            beiZurueckInDieBox = {
                zustand.boxPhaseIstLernphase = false
                zustand.hineinlegen = zustand.hineinlegen.copy(
                    erkannt = false,
                    countdown = null,
                    phasentitel = "Aufgabenphase, ${zustand.aufgabenErhalten.fachName}",
                )
                zustand.inDerBox = zustand.inDerBox.copy(
                    stand = Boxstand.Laeuft,
                    phasentitel = "Aufgabenphase, ${zustand.aufgabenErhalten.fachName}",
                    restSekunden = zustand.aufgabenErhalten.aufgabenphaseMinuten * 60,
                    phasenMinuten = zustand.aufgabenErhalten.aufgabenphaseMinuten,
                )
                navigation.navigate(Ziel.HINEINLEGEN)
            },
            beiAbbrechen = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.HEUTE) { inclusive = true }
                }
            },
        )
    }

    composable(Ziel.SCANNEN) {
        ScannenScreen(
            state = zustand.scannen,
            beiAusloesen = zustand::scanHinzufuegen,
            beiScanEntfernen = zustand::scanEntfernen,
            beiFertig = { navigation.navigate(Ziel.WEITER_ODER_AUFHOEREN) },
            beiAbbrechen = { navigation.popBackStack() },
        )
    }

    composable(Ziel.WEITER_ODER_AUFHOEREN) {
        WeiterOderAufhoerenScreen(
            state = zustand.weiter,
            beiPausenwahl = zustand::pausenwahl,
            beiWeitermachen = { navigation.navigate(Ziel.PAUSE) },
            beiAufhoeren = { navigation.navigate(Ziel.VERBESSERUNG) },
        )
    }

    composable(Ziel.PAUSE) {
        PauseScreen(
            state = zustand.pause,
            beiVerbesserungOeffnen = { navigation.navigate(Ziel.VERBESSERUNG) },
            beiPauseBeenden = { navigation.navigate(Ziel.HINEINLEGEN) },
            beiSpaeterVerbessern = { navigation.navigate(Ziel.SESSION_ENDE) },
        )
    }

    composable(Ziel.VERBESSERUNG) {
        VerbesserungScreen(
            state = zustand.verbesserung,
            beiZurueck = { navigation.popBackStack() },
            beiUrteil = zustand::urteilSetzen,
            beiKarteUmschalten = zustand::karteUmschalten,
            beiWeiter = zustand::verbesserungWeiter,
            beiSpaeter = { navigation.navigate(Ziel.SESSION_ENDE) },
            beiAbschliessen = {
                zustand.verbesserungZuruecksetzen()
                navigation.navigate(Ziel.SESSION_ENDE)
            },
        )
    }

    composable(Ziel.SESSION_ENDE) {
        SessionEndeScreen(
            state = zustand.sessionEnde,
            beiVerbesserungOeffnen = { navigation.navigate(Ziel.VERBESSERUNG) },
            beiFertig = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.HEUTE) { inclusive = true }
                }
            },
            beiSpeicherzeitAendern = { navigation.navigate(Ziel.EINSTELLUNGEN) },
        )
    }

    // --- Brain -------------------------------------------------------------

    composable(Ziel.BRAIN_FACH) {
        BrainFachScreen(
            state = zustand.brainFach,
            beiZurueck = { navigation.popBackStack() },
            beiThema = { thema ->
                zustand.brainThemaWaehlen(thema.id)
                navigation.navigate(Ziel.BRAIN_THEMA)
            },
        )
    }

    composable(Ziel.BRAIN_THEMA) {
        BrainThemaScreen(
            state = zustand.brainThema,
            beiZurueck = { navigation.popBackStack() },
            beiAufgabe = {},
            beiKartenAlle = { navigation.navigate(Ziel.BRAIN_KARTEN) },
            beiFehlerAlle = { navigation.navigate(Ziel.BRAIN_FEHLER) },
        )
    }

    composable(Ziel.BRAIN_SCANS) {
        BrainScansScreen(
            state = zustand.brainScans,
            beiZurueck = { navigation.popBackStack() },
            beiScan = {},
        )
    }

    composable(Ziel.BRAIN_KARTEN) {
        BrainKartenScreen(
            state = zustand.brainKarten,
            beiZurueck = { navigation.popBackStack() },
            beiFilterUmschalten = zustand::kartenfilterUmschalten,
            beiWiederholen = { navigation.navigate(Ziel.KARTEIKARTEN) },
        )
    }

    composable(Ziel.BRAIN_FEHLER) {
        BrainFehlerScreen(
            state = zustand.brainFehler,
            beiZurueck = { navigation.popBackStack() },
            beiKarteErzeugen = {},
        )
    }

    composable(Ziel.BRAIN_PODCASTS) {
        BrainPodcastsScreen(
            state = zustand.brainPodcasts,
            beiZurueck = { navigation.popBackStack() },
            beiPaketKopieren = {},
            beiAudioImportieren = {},
            beiAbspielen = { id ->
                zustand.brainPodcasts = zustand.brainPodcasts.copy(
                    laeuft = if (zustand.brainPodcasts.laeuft == id) null else id,
                )
            },
            beiNeuesPaket = {},
        )
    }

    // --- Import ------------------------------------------------------------

    composable(Ziel.IMPORT_SCANNEN) {
        ImportScannenScreen(
            state = zustand.importScannen,
            beiQuelle = { quelle ->
                zustand.importScannen = zustand.importScannen.copy(quelle = quelle)
            },
            beiBezeichnung = { text ->
                zustand.importScannen = zustand.importScannen.copy(bezeichnung = text)
            },
            beiAusloesen = {},
            beiFertig = { navigation.navigate(Ziel.IMPORT_WARTESCHLANGE) },
            beiAbbrechen = { navigation.popBackStack() },
        )
    }

    composable(Ziel.IMPORT_WARTESCHLANGE) {
        ImportWarteschlangeScreen(
            state = zustand.importWarteschlange,
            beiZurueck = { navigation.popBackStack() },
            beiPruefen = { navigation.navigate(Ziel.IMPORT_PRUEFEN) },
            beiSpaeter = {
                navigation.navigate(Ziel.BRAIN) {
                    popUpTo(Ziel.BRAIN) { inclusive = true }
                }
            },
        )
    }

    composable(Ziel.IMPORT_PRUEFEN) {
        ImportPruefenScreen(
            state = zustand.importPruefen,
            beiZurueck = { navigation.popBackStack() },
            beiText = zustand::importText,
            beiFach = zustand::importFach,
            beiThemaUmschalten = zustand::importThemaUmschalten,
            beiTyp = zustand::importTyp,
            beiLoesungUmschalten = zustand::importLoesungUmschalten,
            beiVerwerfen = zustand::importVerwerfen,
            beiUebernehmen = {
                if (zustand.importPruefen.istLetzte) {
                    navigation.navigate(Ziel.BRAIN) {
                        popUpTo(Ziel.BRAIN) { inclusive = true }
                    }
                } else {
                    zustand.importWeiter()
                }
            },
        )
    }

    // --- Probearbeit -------------------------------------------------------

    composable(Ziel.PROBEARBEIT_EINRICHTEN) {
        ProbearbeitEinrichtenScreen(
            state = zustand.probearbeitEinrichten,
            beiZurueck = { navigation.popBackStack() },
            beiTitel = { text ->
                zustand.probearbeitEinrichten =
                    zustand.probearbeitEinrichten.copy(titel = text)
            },
            beiFach = { fachId ->
                zustand.probearbeitEinrichten =
                    zustand.probearbeitEinrichten.copy(fachId = fachId)
            },
            beiDauer = { minuten ->
                zustand.probearbeitEinrichten =
                    zustand.probearbeitEinrichten.copy(dauerMinuten = minuten)
            },
            beiVorlage = zustand::probearbeitVorlage,
            beiStarten = { navigation.navigate(Ziel.PROBEARBEIT_LAEUFT) },
            beiVergangene = { navigation.navigate(Ziel.PROBEARBEIT_ERGEBNIS) },
        )
    }

    composable(Ziel.PROBEARBEIT_LAEUFT) {
        ProbearbeitLaeuftScreen(
            state = zustand.probearbeitLaeuft,
            beiHerausnehmen = { navigation.navigate(Ziel.PROBEARBEIT_SCANNEN) },
            beiAbbrechen = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.HEUTE) { inclusive = true }
                }
            },
        )
    }

    composable(Ziel.PROBEARBEIT_SCANNEN) {
        ProbearbeitWartetScreen(
            state = zustand.probearbeitWartet,
            beiFertig = {
                navigation.navigate(Ziel.HEUTE) {
                    popUpTo(Ziel.HEUTE) { inclusive = true }
                }
            },
            beiErgebnisAnsehen = { navigation.navigate(Ziel.PROBEARBEIT_ERGEBNIS) },
        )
    }

    composable(Ziel.PROBEARBEIT_ERGEBNIS) {
        ProbearbeitErgebnisScreen(
            state = zustand.probearbeitErgebnis,
            beiZurueck = { navigation.popBackStack() },
            beiEchteNote = zustand::echteNote,
            beiFehlerInsBrain = { navigation.navigate(Ziel.BRAIN_FEHLER) },
        )
    }

    // --- Karteikarten ------------------------------------------------------

    composable(Ziel.KARTEIKARTEN) {
        KarteikartenScreen(
            state = zustand.karteikarten,
            beiZurueck = { navigation.popBackStack() },
            beiUmdrehen = zustand::karteUmdrehen,
            beiBewertung = { zustand.karteBewerten() },
            beiFertig = {
                zustand.karteikartenZuruecksetzen()
                navigation.popBackStack()
            },
        )
    }
}
