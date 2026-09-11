package de.kopfgeld.app.feature.fortschritt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Notenverlauf
import de.kopfgeld.app.data.Probearbeit
import de.kopfgeld.app.data.Probearbeitstand
import de.kopfgeld.app.data.Tagesbilanz
import de.kopfgeld.app.data.Ziel
import de.kopfgeld.app.data.formatiereDauer
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.GradeTrail
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MarkerAmount
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.WeekBars
import de.kopfgeld.app.ui.components.notenText
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Fortschritt-Tab, DESIGN.md 6: "Ziele, Noten, Wochenbilanz, Probearbeiten".
 */
data class FortschrittUiState(
    val ziele: List<Ziel> = FakeData.ziele,
    val notenverlaeufe: List<Notenverlauf> = FakeData.notenverlaeufe,
    val wochenbilanz: List<Tagesbilanz> = FakeData.wochenbilanz,
    val probearbeiten: List<Probearbeit> = FakeData.probearbeiten,
    val gewaehlterVerlauf: Int = 0,
) {
    val lernminutenWoche: Int get() = wochenbilanz.sumOf { it.lernMinuten }
    val scrollminutenWoche: Int get() = wochenbilanz.sumOf { it.scrollMinuten }
    val verlauf: Notenverlauf? get() = notenverlaeufe.getOrNull(gewaehlterVerlauf)
}

@Composable
fun FortschrittScreen(
    state: FortschrittUiState,
    beiVerlaufWechseln: (Int) -> Unit,
    beiProbearbeit: (Probearbeit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(titel = "Fortschritt", scrollbar = false, modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // --- Wochenbilanz
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                Luft(Mass.Klein)
                Text(
                    text = "Diese Woche gelernt",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
                MarkerAmount(
                    wert = state.lernminutenWoche.toString(),
                    einheit = "Min",
                    animieren = true,
                )
                Luft(Mass.Klein)
                Hinweistext(
                    "Gescrollt: ${formatiereDauer(state.scrollminutenWoche)}",
                )
            }

            Abschnittstitel("Wochenbilanz")
            Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                WeekBars(state.wochenbilanz)
            }

            // --- Ziele
            Abschnittstitel("Ziele")
            state.ziele.forEach { ziel ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = ziel.titel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = ziel.meta,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                            Luft(6.dp)
                            Zielbalken(erreicht = ziel.erreicht, gesamt = ziel.gesamt)
                        }
                        LuftBreit(12.dp)
                        Text(
                            text = "${ziel.erreicht}/${ziel.gesamt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                }
            }

            // --- Noten
            Abschnittstitel("Noten")
            Row(
                Modifier.padding(horizontal = Mass.Seitenrand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                state.notenverlaeufe.forEachIndexed { index, verlauf ->
                    val fach = FakeData.fach(verlauf.fachId)
                    val gewaehlt = index == state.gewaehlterVerlauf
                    Box(
                        Modifier
                            .padding(end = 8.dp)
                            .alpha(if (gewaehlt) 1f else 0.4f)
                            .clickable { beiVerlaufWechseln(index) },
                    ) {
                        SubjectBadge(fach)
                    }
                }
                LuftBreit(8.dp)
                val verlauf = state.verlauf
                if (verlauf != null) {
                    Text(
                        text = "Ziel: ${notenText(verlauf.zielnote)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.tinte,
                    )
                }
            }
            Luft(Mass.Mittel)
            val verlauf = state.verlauf
            if (verlauf == null) {
                Hinweistext(
                    "Noch keine Noten eingetragen.",
                    Modifier.padding(horizontal = Mass.Seitenrand),
                )
            } else {
                Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                    GradeTrail(verlauf)
                }
            }

            // --- Probearbeiten
            Abschnittstitel("Probearbeiten")
            state.probearbeiten.forEach { arbeit ->
                RasterZeile(beiKlick = { beiProbearbeit(arbeit) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SubjectBadge(FakeData.fach(arbeit.fachId), groesse = 26.dp)
                        LuftBreit(12.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = arbeit.titel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${arbeit.datum} · ${arbeit.seiten} Seiten",
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        when (arbeit.stand) {
                            Probearbeitstand.Bewertet -> Text(
                                text = notenText(arbeit.geschaetzteNote ?: 0.0),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            else -> Text(
                                text = arbeit.stand.anzeige,
                                style = MaterialTheme.typography.labelSmall,
                                color = stifte.blei,
                            )
                        }
                    }
                }
            }

            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun Zielbalken(erreicht: Int, gesamt: Int) {
    val stifte = KopfgeldTheme.stifte
    val anteil = if (gesamt <= 0) 0f else erreicht.toFloat() / gesamt.toFloat()
    Box(
        Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(stifte.karo),
    ) {
        Box(
            Modifier
                .fillMaxWidth(anteil.coerceIn(0f, 1f))
                .height(6.dp)
                .background(stifte.tinte),
        )
    }
}
