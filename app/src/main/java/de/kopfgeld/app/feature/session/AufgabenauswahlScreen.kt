package de.kopfgeld.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import de.kopfgeld.app.data.Aufgabe
import de.kopfgeld.app.data.Aufgabenquelle
import de.kopfgeld.app.data.Aufgabenstand
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.TaskRow
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.TypChip
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Aufgabenauswahl, DESIGN.md 7.6.
 *
 * Sortierung "Fehler zuerst" ist Standard - Fehler sind das Wertvollste im
 * Brain, sie sollen nicht unten liegen.
 */
data class AufgabenauswahlUiState(
    val themaIds: List<String> = listOf("m1", "m2"),
    val ausgewaehlt: Set<String> = setOf("a1", "a2", "a3", "a4", "a5", "a6"),
    val quellenfilter: Set<Aufgabenquelle> = emptySet(),
    val standfilter: Set<Aufgabenstand> = emptySet(),
    val fehlerZuerst: Boolean = true,
) {
    val sichtbareAufgaben: List<Aufgabe>
        get() {
            val grund = FakeData.aufgabenZuThemen(themaIds)
            val gefiltert = grund.filter { aufgabe ->
                val quellePasst = quellenfilter.isEmpty() || aufgabe.quelle in quellenfilter
                val standPasst = standfilter.isEmpty() || aufgabe.stand in standfilter
                quellePasst && standPasst
            }
            return if (fehlerZuerst) FakeData.fehlerZuerst(gefiltert) else gefiltert
        }

    val geschaetzteMinuten: Int
        get() = FakeData.aufgaben
            .filter { it.id in ausgewaehlt }
            .sumOf { it.geschaetzteMinuten }

    val zaehlerText: String
        get() = "${ausgewaehlt.size} ausgewählt, ca. $geschaetzteMinuten Min"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AufgabenauswahlScreen(
    state: AufgabenauswahlUiState,
    beiZurueck: () -> Unit,
    beiAufgabeUmschalten: (String) -> Unit,
    beiQuellenfilter: (Aufgabenquelle) -> Unit,
    beiStandfilter: (Aufgabenstand) -> Unit,
    beiSortierung: () -> Unit,
    beiUebernehmen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Aufgaben auswählen",
        unterzeile = FakeData.themenNamen(state.themaIds),
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            PrimaerButton(text = "Übernehmen", beiKlick = beiUebernehmen)
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(stifte.tief)
                    .padding(horizontal = Mass.Seitenrand, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.zaehlerText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                TypChip(
                    text = if (state.fehlerZuerst) "Fehler zuerst" else "Wie im Brain",
                    gewaehlt = state.fehlerZuerst,
                    beiKlick = beiSortierung,
                )
            }
            Trennlinie()

            Luft(Mass.Mittel)
            Text(
                text = "Quelle",
                style = MaterialTheme.typography.labelSmall,
                color = stifte.blei,
                modifier = Modifier.padding(horizontal = Mass.Seitenrand),
            )
            Luft(6.dp)
            FlowRow(
                modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Aufgabenquelle.entries.forEach { quelle ->
                    TypChip(
                        text = quelle.anzeige,
                        gewaehlt = quelle in state.quellenfilter,
                        beiKlick = { beiQuellenfilter(quelle) },
                    )
                }
            }

            Luft(Mass.Mittel)
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelSmall,
                color = stifte.blei,
                modifier = Modifier.padding(horizontal = Mass.Seitenrand),
            )
            Luft(6.dp)
            FlowRow(
                modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    Aufgabenstand.Neu,
                    Aufgabenstand.Fehler,
                    Aufgabenstand.Geuebt,
                ).forEach { stand ->
                    TypChip(
                        text = stand.anzeige,
                        gewaehlt = stand in state.standfilter,
                        beiKlick = { beiStandfilter(stand) },
                    )
                }
            }

            Luft(Mass.Gross)
            Trennlinie()

            val aufgaben = state.sichtbareAufgaben
            if (aufgaben.isEmpty()) {
                Luft(Mass.Gross)
                Hinweistext(
                    text = "Keine Aufgabe passt zu diesen Filtern.",
                    modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                )
            } else {
                aufgaben.forEach { aufgabe ->
                    TaskRow(
                        aufgabe = aufgabe,
                        auswahlmodus = true,
                        ausgewaehlt = aufgabe.id in state.ausgewaehlt,
                        beiKlick = { beiAufgabeUmschalten(aufgabe.id) },
                    )
                }
            }

            Luft(Mass.Gross)
        }
    }
}
