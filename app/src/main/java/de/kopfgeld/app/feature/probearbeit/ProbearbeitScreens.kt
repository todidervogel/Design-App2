package de.kopfgeld.app.feature.probearbeit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Probearbeit
import de.kopfgeld.app.data.Probearbeitstand
import de.kopfgeld.app.data.Rasterzeile
import de.kopfgeld.app.data.formatiereDauer
import de.kopfgeld.app.data.formatiereUhr
import de.kopfgeld.app.ui.components.DurationSlider
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MarkerAmount
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.RubricBar
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.TafelScaffold
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.TypChip
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.components.notenText
import de.kopfgeld.app.feature.session.MockHinweis
import de.kopfgeld.app.ui.theme.Kreide
import de.kopfgeld.app.ui.theme.KreideMatt
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/*
 * Probearbeit, DESIGN.md 2.4.
 *
 * Lange Arbeiten (45 bis 120 Min oder frei), ganze Zeit in der Box, danach
 * alle Seiten scannen. Die Bewertung nach Raster kann Stunden dauern, auch
 * ueber Nacht. Spaeter traegt man die echte Lehrer-Note ein, um die
 * Schaetzung zu vergleichen.
 */

// --- Einrichten ------------------------------------------------------------

data class ProbearbeitEinrichtenUiState(
    val titel: String = "Alte Klassenarbeit",
    val fachId: String = "m",
    val dauerMinuten: Int = 60,
    val vergangene: List<Probearbeit> = FakeData.probearbeiten,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProbearbeitEinrichtenScreen(
    state: ProbearbeitEinrichtenUiState,
    beiZurueck: () -> Unit,
    beiTitel: (String) -> Unit,
    beiFach: (String) -> Unit,
    beiDauer: (Int) -> Unit,
    beiVorlage: (String, Int) -> Unit,
    beiStarten: () -> Unit,
    beiVergangene: (Probearbeit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Probearbeit",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            PrimaerButton(text = "Probearbeit starten", beiKlick = beiStarten)
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Mass.Seitenrand),
        ) {
            Hinweistext(
                "Eine lange Arbeit am Stück. Das Handy liegt die ganze Zeit in der Box.",
            )

            Luft(Mass.Gross)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FakeData.probearbeitVorlagen.forEach { (name, minuten) ->
                    TypChip(
                        text = "$name · ${formatiereDauer(minuten)}",
                        gewaehlt = state.titel == name && state.dauerMinuten == minuten,
                        beiKlick = { beiVorlage(name, minuten) },
                    )
                }
            }

            Luft(Mass.Gross)
            OutlinedTextField(
                value = state.titel,
                onValueChange = beiTitel,
                label = { Text("Titel") },
                singleLine = true,
                shape = Radius.Eingabe,
                modifier = Modifier.fillMaxWidth(),
            )

            Luft(Mass.Gross)
            Text(
                text = "Fach",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FakeData.faecher.filter { it.aktiv }.forEach { fach ->
                    TypChip(
                        text = fach.name,
                        gewaehlt = fach.id == state.fachId,
                        beiKlick = { beiFach(fach.id) },
                    )
                }
            }

            Luft(Mass.Gross)
            DurationSlider(
                beschriftung = "Dauer",
                minuten = state.dauerMinuten,
                beiAenderung = beiDauer,
                vonMinuten = 15,
                bisMinuten = 180,
            )

            Luft(Mass.Gross)
            Trennlinie()
            Luft(Mass.Mittel)
            Text(
                text = "Frühere Probearbeiten",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            state.vergangene.forEach { arbeit ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SubjectBadge(FakeData.fach(arbeit.fachId), groesse = 26.dp)
                    LuftBreit(12.dp)
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = arbeit.titel,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${arbeit.datum} · ${formatiereDauer(arbeit.dauerMinuten)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.blei,
                        )
                    }
                    TextAktion(
                        text = when (arbeit.stand) {
                            Probearbeitstand.Bewertet ->
                                "Note ${notenText(arbeit.geschaetzteNote ?: 0.0)}"

                            Probearbeitstand.WartetAufBewertung -> "wartet"
                            else -> arbeit.stand.anzeige
                        },
                        beiKlick = { beiVergangene(arbeit) },
                    )
                }
                Trennlinie()
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Laeuft ----------------------------------------------------------------

data class ProbearbeitLaeuftUiState(
    val titel: String = "Alte Klassenarbeit",
    val fachName: String = "Mathe",
    val restSekunden: Int = 3420,
    val gesamtMinuten: Int = 60,
)

@Composable
fun ProbearbeitLaeuftScreen(
    state: ProbearbeitLaeuftUiState,
    beiHerausnehmen: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TafelScaffold(modifier = modifier, abdunkeln = 0.8f) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = state.titel,
                style = MaterialTheme.typography.bodyMedium,
                color = KreideMatt.copy(alpha = 0.7f),
            )
            Text(
                text = state.fachName,
                style = MaterialTheme.typography.labelSmall,
                color = KreideMatt.copy(alpha = 0.5f),
            )
            Luft(Mass.Mittel)
            Text(
                text = formatiereUhr(state.restSekunden),
                style = MaterialTheme.typography.headlineMedium,
                color = Kreide.copy(alpha = 0.85f),
            )
            Luft(Mass.Sehr)
            MockHinweis(
                text = "Tippen: Arbeit beenden und scannen",
                beiKlick = beiHerausnehmen,
                zentriert = true,
            )
            MockHinweis(
                text = "Tippen: Probearbeit abbrechen",
                beiKlick = beiAbbrechen,
                zentriert = true,
            )
        }
    }
}

// --- Wartet auf Bewertung --------------------------------------------------

data class ProbearbeitWartetUiState(
    val titel: String = "Deutsch-Aufsatz: Erörterung",
    val seiten: Int = 5,
    val eingereichtUm: String = "17:42",
)

@Composable
fun ProbearbeitWartetScreen(
    state: ProbearbeitWartetUiState,
    beiFertig: () -> Unit,
    beiErgebnisAnsehen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeftScaffold(
        titel = "Wartet auf Bewertung",
        unterzeile = state.titel,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(text = "Fertig", beiKlick = beiFertig)
                TextAktion(text = "Ergebnis ansehen (Demo)", beiKlick = beiErgebnisAnsehen)
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Mass.Seitenrand),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${state.seiten} Seiten liegen beim Server.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Luft(Mass.Mittel)
            Hinweistext(
                text = "Eine ganze Arbeit zu bewerten dauert lange, oft Stunden, " +
                    "manchmal über Nacht. Du bekommst Bescheid, sobald sie fertig ist.",
                zentriert = true,
            )
            Luft(Mass.Klein)
            Hinweistext(text = "Abgegeben um ${state.eingereichtUm}", zentriert = true)
        }
    }
}

// --- Ergebnis --------------------------------------------------------------

data class ProbearbeitErgebnisUiState(
    val arbeit: Probearbeit = FakeData.probearbeiten.first {
        it.stand == Probearbeitstand.Bewertet
    },
) {
    val raster: List<Rasterzeile> get() = arbeit.raster
    val punkte: Int get() = raster.sumOf { it.punkte }
    val maximum: Int get() = raster.sumOf { it.maximum }
}

@Composable
fun ProbearbeitErgebnisScreen(
    state: ProbearbeitErgebnisUiState,
    beiZurueck: () -> Unit,
    beiEchteNote: (Double) -> Unit,
    beiFehlerInsBrain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val arbeit = state.arbeit
    var noteDialogOffen by remember { mutableStateOf(false) }

    HeftScaffold(
        titel = arbeit.titel,
        unterzeile = "${FakeData.fach(arbeit.fachId).name} · ${arbeit.datum} · " +
            formatiereDauer(arbeit.dauerMinuten),
        zurueck = beiZurueck,
        karo = true,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(
                    text = "Fehler ins Brain übernehmen",
                    beiKlick = beiFehlerInsBrain,
                )
                TextAktion(
                    text = if (arbeit.echteNote == null) {
                        "Echte Note eintragen"
                    } else {
                        "Echte Note ändern"
                    },
                    beiKlick = { noteDialogOffen = true },
                )
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Mass.Seitenrand),
        ) {
            Luft(Mass.Klein)
            Text(
                text = "Geschätzte Note",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
            )
            MarkerAmount(
                wert = notenText(arbeit.geschaetzteNote ?: 0.0),
                animieren = true,
            )
            Luft(Mass.Klein)
            Text(
                text = "${state.punkte} von ${state.maximum} Punkten",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
            )

            if (arbeit.echteNote != null) {
                Luft(Mass.Mittel)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(stifte.tinteHauch, Radius.Flaeche)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Echte Note vom Lehrer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.tinte,
                        )
                        Text(
                            text = vergleichstext(arbeit.geschaetzteNote, arbeit.echteNote),
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.tinte,
                        )
                    }
                    Text(
                        text = notenText(arbeit.echteNote),
                        style = MaterialTheme.typography.headlineMedium,
                        color = stifte.tinte,
                    )
                }
            }

            Luft(Mass.Gross)
            Text(
                text = "Bewertungsraster",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            state.raster.forEach { zeile -> RubricBar(zeile) }

            if (arbeit.kommentar.isNotBlank()) {
                Luft(Mass.Gross)
                Text(
                    text = "Kommentar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Luft(Mass.Klein)
                Text(
                    text = arbeit.kommentar,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Luft(Mass.Gross)
        }
    }

    if (noteDialogOffen) {
        NotenDialog(
            vorbelegt = arbeit.echteNote,
            beiAbbrechen = { noteDialogOffen = false },
            beiWahl = { note ->
                beiEchteNote(note)
                noteDialogOffen = false
            },
        )
    }
}

private fun vergleichstext(geschaetzt: Double?, echt: Double?): String {
    if (geschaetzt == null || echt == null) return ""
    val unterschied = echt - geschaetzt
    return when {
        unterschied == 0.0 -> "Die Schätzung hat gestimmt."
        unterschied > 0 -> "Die Schätzung war um ${notenText(unterschied)} zu gut."
        else -> "Die Schätzung war um ${notenText(-unterschied)} zu streng."
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotenDialog(
    vorbelegt: Double?,
    beiAbbrechen: () -> Unit,
    beiWahl: (Double) -> Unit,
) {
    var gewaehlt by remember { mutableStateOf(vorbelegt ?: 3.0) }

    AlertDialog(
        onDismissRequest = beiAbbrechen,
        title = { Text("Echte Note eintragen") },
        text = {
            Column {
                Text(
                    text = "Was stand am Ende unter der Arbeit?",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Luft(Mass.Mittel)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..6).forEach { note ->
                        TypChip(
                            text = note.toString(),
                            gewaehlt = gewaehlt == note.toDouble(),
                            beiKlick = { gewaehlt = note.toDouble() },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { beiWahl(gewaehlt) }) { Text("Speichern") }
        },
        dismissButton = {
            TextButton(onClick = beiAbbrechen) { Text("Abbrechen") }
        },
        shape = Radius.Flaeche,
    )
}

/** Kleiner Einstieg fuer den Scan-Schritt der Probearbeit. */
@Composable
fun ProbearbeitScannenHinweis(
    seiten: Int,
    beiWeiter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        ZweitButton(text = "Alle $seiten Seiten gescannt", beiKlick = beiWeiter)
    }
}
