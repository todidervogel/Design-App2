package de.kopfgeld.app.feature.lernen

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
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Testphase
import de.kopfgeld.app.data.Vorlage
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.TestphaseBanner
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Lernen-Tab, DESIGN.md 7.3. Abschnitte als Zeilen, keine Kartenstapel.
 */
data class LernenUiState(
    val vorlagen: List<Vorlage> = FakeData.vorlagen,
    val faelligeKarten: Int = FakeData.faelligeKarten,
    val offeneVerbesserungen: Int = FakeData.offeneVerbesserungen.size,
    val testphase: Testphase? = FakeData.testphase,
)

@Composable
fun LernenScreen(
    state: LernenUiState,
    beiSessionPlanen: () -> Unit,
    beiVorlage: (Vorlage) -> Unit,
    beiProbearbeit: () -> Unit,
    beiKarteikarten: () -> Unit,
    beiVerbesserungen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(titel = "Lernen", scrollbar = false, modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.testphase != null) {
                TestphaseBanner(text = state.testphase.bannerText)
            }

            Luft(Mass.Mittel)
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                PrimaerButton(text = "Session planen", beiKlick = beiSessionPlanen)
            }
            Luft(Mass.Klein)

            Abschnittstitel("Vorlagen")
            state.vorlagen.forEach { vorlage ->
                RasterZeile(beiKlick = { beiVorlage(vorlage) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row {
                            vorlage.plan.zyklen
                                .map { it.fachId }
                                .distinct()
                                .take(3)
                                .forEachIndexed { index, fachId ->
                                    if (index > 0) LuftBreit(4.dp)
                                    SubjectBadge(
                                        fach = FakeData.fach(fachId),
                                        groesse = 26.dp,
                                    )
                                }
                        }
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = vorlage.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = vorlage.beschreibung,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                    }
                }
            }

            Luft(Mass.Gross)
            RasterZeile(beiKlick = beiProbearbeit) {
                Text(
                    text = "Probearbeit schreiben",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Lange Arbeit am Stück, danach Bewertung nach Raster",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }

            RasterZeile(beiKlick = beiKarteikarten) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Karteikarten wiederholen",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${state.faelligeKarten} fällig",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.blei,
                        )
                    }
                    Text(
                        text = state.faelligeKarten.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = stifte.tinte,
                    )
                }
            }

            RasterZeile(beiKlick = beiVerbesserungen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Offene Verbesserungen",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (state.offeneVerbesserungen == 0) {
                                "nichts offen"
                            } else {
                                "von gestern, bereit zum Verbessern"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.blei,
                        )
                    }
                    if (state.offeneVerbesserungen > 0) {
                        Text(
                            text = state.offeneVerbesserungen.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = stifte.korrektur,
                        )
                    }
                }
            }

            Luft(Mass.Gross)
        }
    }
}
