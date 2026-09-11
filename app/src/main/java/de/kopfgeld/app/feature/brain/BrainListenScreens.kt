package de.kopfgeld.app.feature.brain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Fehlerposten
import de.kopfgeld.app.data.Karteikarte
import de.kopfgeld.app.data.Podcast
import de.kopfgeld.app.data.Podcaststand
import de.kopfgeld.app.data.Scan
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MerkChip
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.ScanThumb
import de.kopfgeld.app.ui.components.ServerStatusChip
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.SymbolPodcast
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

// --- Scans -----------------------------------------------------------------

data class BrainScansUiState(
    val scans: List<Scan> = FakeData.scansBrain,
    val server: Serverstand = FakeData.serverstandAnalyse,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrainScansScreen(
    state: BrainScansUiState,
    beiZurueck: () -> Unit,
    beiScan: (Scan) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val gruppen = state.scans.groupBy { it.herkunft }

    HeftScaffold(
        titel = "Scans",
        zurueck = beiZurueck,
        karo = true,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                ServerStatusChip(state.server)
            }
            Luft(Mass.Klein)
            Hinweistext(
                "Der Server analysiert eine Seite in Minuten. Was noch wartet, " +
                    "bleibt hier stehen, bis er wieder Zeit hat.",
                Modifier.padding(horizontal = Mass.Seitenrand),
            )

            gruppen.forEach { (herkunft, scans) ->
                Abschnittstitel(herkunft.ifBlank { "Ohne Herkunft" })
                FlowRow(
                    modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    scans.forEach { scan ->
                        ScanThumb(scan = scan, beiKlick = { beiScan(scan) })
                    }
                }
            }

            Luft(Mass.Gross)
            Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                Text(
                    text = "${state.scans.size} Seiten im Brain",
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.blei,
                )
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Karteikarten ----------------------------------------------------------

data class BrainKartenUiState(
    val karten: List<Karteikarte> = FakeData.karteikarten,
    val nurFaellige: Boolean = false,
) {
    val sichtbar: List<Karteikarte>
        get() = if (nurFaellige) karten.filter { it.faellig } else karten
}

@Composable
fun BrainKartenScreen(
    state: BrainKartenUiState,
    beiZurueck: () -> Unit,
    beiFilterUmschalten: () -> Unit,
    beiWiederholen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Karteikarten",
        unterzeile = "${state.karten.count { it.faellig }} von ${state.karten.size} fällig",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            ZweitButton(text = "Fällige wiederholen", beiKlick = beiWiederholen)
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(Modifier.padding(horizontal = Mass.Seitenrand, vertical = Mass.Klein)) {
                TextAktion(
                    text = if (state.nurFaellige) "Alle zeigen" else "Nur fällige zeigen",
                    beiKlick = beiFilterUmschalten,
                )
            }

            state.sichtbar.forEach { karte ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.Top) {
                        SubjectBadge(FakeData.fach(karte.fachId), groesse = 26.dp)
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = karte.vorderseite,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = karte.rueckseite,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                            if (karte.herkunft.isNotBlank()) {
                                Luft(4.dp)
                                Text(
                                    text = karte.herkunft,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = stifte.korrektur,
                                )
                            }
                        }
                        if (karte.faellig) {
                            MerkChip("fällig", farbe = stifte.tinte)
                        }
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Fehler ----------------------------------------------------------------

data class BrainFehlerUiState(
    val fehler: List<Fehlerposten> = FakeData.fehler,
)

@Composable
fun BrainFehlerScreen(
    state: BrainFehlerUiState,
    beiZurueck: () -> Unit,
    beiKarteErzeugen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Fehler",
        unterzeile = "${state.fehler.size} gesammelt",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Hinweistext(
                "Fehler sind das Wertvollste im Brain. Aus ihnen werden Karteikarten, " +
                    "und in der Aufgabenauswahl stehen sie oben.",
                Modifier.padding(horizontal = Mass.Seitenrand, vertical = Mass.Klein),
            )

            state.fehler.forEach { posten ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.Top) {
                        SubjectBadge(FakeData.fach(posten.fachId), groesse = 26.dp)
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = posten.beschreibung,
                                style = MaterialTheme.typography.bodyLarge,
                                color = stifte.korrektur,
                            )
                            Text(
                                text = "${FakeData.thema(posten.themaId).name} · " +
                                    "${posten.anzahl}× seit ${posten.datum}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        LuftBreit(8.dp)
                        if (posten.hatKarte) {
                            MerkChip("Karte da", farbe = stifte.tinte)
                        } else {
                            Box(
                                Modifier
                                    .background(stifte.tinteHauch, Radius.Chip)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                TextAktion(
                                    text = "Karte draus",
                                    beiKlick = { beiKarteErzeugen(posten.id) },
                                )
                            }
                        }
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Podcasts --------------------------------------------------------------

data class BrainPodcastsUiState(
    val podcasts: List<Podcast> = FakeData.podcasts,
    val laeuft: String? = null,
)

@Composable
fun BrainPodcastsScreen(
    state: BrainPodcastsUiState,
    beiZurueck: () -> Unit,
    beiPaketKopieren: (String) -> Unit,
    beiAudioImportieren: (String) -> Unit,
    beiAbspielen: (String) -> Unit,
    beiNeuesPaket: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Podcasts",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            ZweitButton(text = "Neues Paket erstellen", beiKlick = beiNeuesPaket)
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Hinweistext(
                "Die App baut aus deinem Stoff ein Paket mit Prompt zum Kopieren. " +
                    "Die fertige Audiodatei importierst du hier wieder herein.",
                Modifier.padding(horizontal = Mass.Seitenrand, vertical = Mass.Klein),
            )

            state.podcasts.forEach { podcast ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(Modifier.padding(top = 4.dp)) { SymbolPodcast() }
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = podcast.titel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${FakeData.fach(podcast.fachId).name} · " +
                                    podcast.themen,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                            Luft(6.dp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MerkChip(podcast.stand.anzeige)
                                if (podcast.stand == Podcaststand.Importiert) {
                                    LuftBreit(8.dp)
                                    Text(
                                        text = podcast.dauer,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = stifte.blei,
                                    )
                                }
                            }
                            Luft(4.dp)
                            when (podcast.stand) {
                                Podcaststand.PaketBereit -> TextAktion(
                                    text = "Paket kopieren",
                                    beiKlick = { beiPaketKopieren(podcast.id) },
                                )

                                Podcaststand.WartetAufImport -> TextAktion(
                                    text = "Audio importieren",
                                    beiKlick = { beiAudioImportieren(podcast.id) },
                                )

                                Podcaststand.Importiert -> TextAktion(
                                    text = if (state.laeuft == podcast.id) {
                                        "Pause"
                                    } else {
                                        "Abspielen"
                                    },
                                    beiKlick = { beiAbspielen(podcast.id) },
                                )
                            }
                        }
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}
