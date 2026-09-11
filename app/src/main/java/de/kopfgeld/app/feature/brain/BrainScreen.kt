package de.kopfgeld.app.feature.brain

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Fach
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.data.Thema
import de.kopfgeld.app.data.Themastand
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.ServerStatusChip
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.TaskRow
import de.kopfgeld.app.ui.components.TopicStatus
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Brain-Tab, DESIGN.md 6: "Faecher, Themen, Aufgaben, Scans, Karten, Fehler,
 * Import, Podcasts". Ein eigener Wireframe fehlt im uebermittelten Auftrag,
 * der Inhalt ist damit vorgegeben.
 */
data class BrainUiState(
    val faecher: List<Fach> = FakeData.faecher,
    val server: Serverstand = FakeData.serverstandHeute,
    val anzahlScans: Int = FakeData.scansBrain.size,
    val anzahlKarten: Int = FakeData.karteikarten.size,
    val anzahlFehler: Int = FakeData.fehler.size,
    val anzahlPodcasts: Int = FakeData.podcasts.size,
    val wartendeScans: Int = FakeData.scansBrain.count {
        it.stand != de.kopfgeld.app.data.Scanstand.Fertig
    },
)

@Composable
fun BrainScreen(
    state: BrainUiState,
    beiFach: (Fach) -> Unit,
    beiImport: () -> Unit,
    beiScans: () -> Unit,
    beiKarten: () -> Unit,
    beiFehler: () -> Unit,
    beiPodcasts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(titel = "Brain", scrollbar = false, modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                ServerStatusChip(state.server)
                Luft(Mass.Mittel)
                PrimaerButton(text = "Seiten importieren", beiKlick = beiImport)
                Luft(Mass.Klein)
                Hinweistext(
                    "Buchseiten, Arbeitsblätter und alte Arbeiten scannen. " +
                        "Danach ordnest du sie Fach, Thema und Typ zu.",
                )
            }

            Abschnittstitel("Fächer")
            state.faecher.forEach { fach ->
                val themen = FakeData.themenVon(fach.id)
                RasterZeile(beiKlick = { beiFach(fach) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SubjectBadge(fach)
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = fach.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${themen.size} Themen, " +
                                    "${themen.sumOf { it.anzahlAufgaben }} Aufgaben, " +
                                    "${themen.sumOf { it.anzahlKarten }} Karten",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            themen.take(5).forEach { thema ->
                                TopicStatus(thema.stand, groesse = 10.dp)
                                LuftBreit(3.dp)
                            }
                        }
                    }
                }
            }

            Abschnittstitel("Alles im Brain")
            BrainZeile(
                titel = "Scans",
                meta = if (state.wartendeScans > 0) {
                    "${state.anzahlScans} Seiten, ${state.wartendeScans} warten auf Analyse"
                } else {
                    "${state.anzahlScans} Seiten"
                },
                beiKlick = beiScans,
            )
            BrainZeile(
                titel = "Karteikarten",
                meta = "${state.anzahlKarten} Karten, ${FakeData.faelligeKarten} fällig",
                beiKlick = beiKarten,
            )
            BrainZeile(
                titel = "Fehler",
                meta = "${state.anzahlFehler} gesammelt, werden zu Karten",
                beiKlick = beiFehler,
            )
            BrainZeile(
                titel = "Podcasts",
                meta = "${state.anzahlPodcasts} Pakete und Aufnahmen",
                beiKlick = beiPodcasts,
            )

            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun BrainZeile(titel: String, meta: String, beiKlick: () -> Unit) {
    val stifte = KopfgeldTheme.stifte
    RasterZeile(beiKlick = beiKlick) {
        Text(
            text = titel,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = meta,
            style = MaterialTheme.typography.bodyMedium,
            color = stifte.blei,
        )
    }
}

// --- Fach ------------------------------------------------------------------

data class BrainFachUiState(
    val fach: Fach = FakeData.fach("m"),
) {
    val themen: List<Thema> get() = FakeData.themenVon(fach.id)
}

@Composable
fun BrainFachScreen(
    state: BrainFachUiState,
    beiZurueck: () -> Unit,
    beiThema: (Thema) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = state.fach.name,
        unterzeile = if (state.fach.istPruefungsfach) "Prüfungsfach" else null,
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Abschnittstitel("Themen")
            state.themen.forEach { thema ->
                RasterZeile(beiKlick = { beiThema(thema) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TopicStatus(thema.stand)
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = thema.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${thema.anzahlAufgaben} Aufgaben, " +
                                    "${thema.anzahlKarten} Karten",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        Text(
                            text = when (thema.stand) {
                                Themastand.Neu -> "neu"
                                Themastand.Gelernt -> "gelernt"
                                Themastand.Sitzt -> "sitzt"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Thema -----------------------------------------------------------------

data class BrainThemaUiState(
    val thema: Thema = FakeData.thema("m2"),
) {
    val aufgaben get() = FakeData.aufgabenZuThemen(listOf(thema.id))
    val karten get() = FakeData.karteikarten.filter { it.themaId == thema.id }
    val fehler get() = FakeData.fehler.filter { it.themaId == thema.id }
}

@Composable
fun BrainThemaScreen(
    state: BrainThemaUiState,
    beiZurueck: () -> Unit,
    beiAufgabe: (String) -> Unit,
    beiKartenAlle: () -> Unit,
    beiFehlerAlle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val fach = FakeData.fach(state.thema.fachId)

    HeftScaffold(
        titel = state.thema.name,
        unterzeile = fach.name,
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Seitenrand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TopicStatus(state.thema.stand)
                LuftBreit(8.dp)
                Text(
                    text = when (state.thema.stand) {
                        Themastand.Neu -> "Noch nicht gelernt"
                        Themastand.Gelernt -> "Gelernt, noch nicht sicher"
                        Themastand.Sitzt -> "Sitzt"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }

            Abschnittstitel("Aufgaben")
            if (state.aufgaben.isEmpty()) {
                Hinweistext(
                    "Noch keine Aufgaben. Scanne Buchseiten oder Arbeitsblätter.",
                    Modifier.padding(horizontal = Mass.Seitenrand),
                )
            } else {
                state.aufgaben.forEach { aufgabe ->
                    TaskRow(aufgabe = aufgabe, beiKlick = { beiAufgabe(aufgabe.id) })
                }
            }

            Abschnittstitel("Karteikarten")
            state.karten.forEach { karte ->
                RasterZeile {
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
                }
            }
            if (state.karten.isEmpty()) {
                Hinweistext(
                    "Noch keine Karten zu diesem Thema.",
                    Modifier.padding(horizontal = Mass.Seitenrand),
                )
            } else {
                RasterZeile(beiKlick = beiKartenAlle) {
                    Text(
                        text = "Alle Karten ansehen",
                        style = MaterialTheme.typography.labelLarge,
                        color = stifte.tinte,
                    )
                }
            }

            Abschnittstitel("Fehler")
            state.fehler.forEach { posten ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = posten.beschreibung,
                                style = MaterialTheme.typography.bodyLarge,
                                color = stifte.korrektur,
                            )
                            Text(
                                text = "${posten.anzahl}× seit ${posten.datum}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        if (posten.hatKarte) {
                            Text(
                                text = "Karte da",
                                style = MaterialTheme.typography.labelSmall,
                                color = stifte.tinte,
                            )
                        }
                    }
                }
            }
            if (state.fehler.isEmpty()) {
                Hinweistext(
                    "Keine Fehler gespeichert. Gut.",
                    Modifier.padding(horizontal = Mass.Seitenrand),
                )
            } else {
                RasterZeile(beiKlick = beiFehlerAlle) {
                    Text(
                        text = "Alle Fehler ansehen",
                        style = MaterialTheme.typography.labelLarge,
                        color = stifte.tinte,
                    )
                }
            }

            Luft(Mass.Gross)
        }
    }
}
