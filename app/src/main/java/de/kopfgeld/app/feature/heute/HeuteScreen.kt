package de.kopfgeld.app.feature.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Planart
import de.kopfgeld.app.data.Planposten
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.data.Sperrstand
import de.kopfgeld.app.data.Testphase
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.LockStatusLine
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MarkerAmount
import de.kopfgeld.app.ui.components.PlanRow
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.ServerStatusChip
import de.kopfgeld.app.ui.components.TestphaseBanner
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.karoRaster
import de.kopfgeld.app.ui.components.langerDruck
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/** Eine gerade laufende Session, DESIGN.md 7.2, Zustand "Session laeuft". */
data class LaufendeSession(
    val zyklusNummer: Int,
    val zyklenGesamt: Int,
    val boxZeit: String,
)

/**
 * Heute, DESIGN.md 7.2.
 *
 * Zustaende: normal, Session laeuft gerade, alles erledigt,
 * Server nicht erreichbar.
 */
data class HeuteUiState(
    val datum: String = FakeData.heutigesDatum,
    val guthabenMinuten: Int = FakeData.guthabenMinuten,
    val sperre: Sperrstand = FakeData.sperrstandHeute,
    val server: Serverstand = FakeData.serverstandHeute,
    val testphase: Testphase? = FakeData.testphase,
    val plan: List<Planposten> = FakeData.tagesplan,
    val laufendeSession: LaufendeSession? = null,
) {
    private val aufgabenImPlan: List<Planposten>
        get() = plan.filter { it.art != Planart.Schlafenszeit }

    val allesErledigt: Boolean
        get() = aufgabenImPlan.isNotEmpty() && aufgabenImPlan.all { it.erledigt }
}

@Composable
fun HeuteScreen(
    state: HeuteUiState,
    beiEinstellungen: () -> Unit,
    beiKatalog: () -> Unit,
    beiPostenAnhaken: (String) -> Unit,
    beiPostenOeffnen: (Planposten) -> Unit,
    beiVorschlagStarten: () -> Unit,
    beiEigeneSession: () -> Unit,
    beiLaufendeSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            // Kopf mit Karo. Laut DESIGN.md 4.3 traegt nur der obere Teil das Raster.
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .karoRaster(stifte.karo)
                    .padding(
                        start = Mass.Seitenrand,
                        end = Mass.Seitenrand,
                        top = Mass.Mittel,
                        bottom = Mass.Gross,
                    ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.datum,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            // Langer Druck oeffnet den Screen-Katalog, DESIGN.md 6.
                            .langerDruck(beiKatalog),
                    )
                    Box(
                        Modifier
                            .size(Mass.Tippziel)
                            .clickable(onClick = beiEinstellungen),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Einstellungen",
                            tint = stifte.blei,
                        )
                    }
                }

                Luft(Mass.Mittel)
                Text(
                    text = "Guthaben",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
                MarkerAmount(
                    wert = state.guthabenMinuten.toString(),
                    einheit = "Min",
                    animieren = true,
                )

                Luft(Mass.Mittel)
                LockStatusLine(state.sperre)
                Luft(Mass.Klein)
                ServerStatusChip(state.server)
            }

            Trennlinie()

            if (state.laufendeSession != null) {
                LaufendeSessionHinweis(
                    session = state.laufendeSession,
                    beiKlick = beiLaufendeSession,
                )
                Trennlinie()
            }

            if (state.testphase != null) {
                TestphaseBanner(text = state.testphase.bannerText)
                Trennlinie()
            }

            Luft(Mass.Gross)
            Text(
                text = if (state.allesErledigt) "Heute geschafft" else "Vorschlag für heute",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = Mass.Seitenrand),
            )
            Luft(Mass.Klein)

            if (state.allesErledigt) {
                Hinweistext(
                    text = "Alles abgehakt. Die Speicherzeit läuft noch, danach " +
                        "ist dein Guthaben frei.",
                    modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                )
                Luft(Mass.Mittel)
            }

            state.plan.forEach { posten ->
                PlanRow(
                    posten = posten,
                    beiAnhaken = { beiPostenAnhaken(posten.id) },
                    beiKlick = { beiPostenOeffnen(posten) },
                )
            }

            if (state.server is Serverstand.NichtErreichbar) {
                Luft(Mass.Mittel)
                Hinweistext(
                    text = "Der Server ist gerade nicht erreichbar. Lernen, Aufgaben " +
                        "und Karten gehen trotzdem. Deine Scans warten, bis er wieder da ist.",
                    modifier = Modifier.padding(horizontal = Mass.Seitenrand),
                )
            }

            Luft(Mass.Gross)
        }

        Trennlinie()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = Mass.Seitenrand,
                    end = Mass.Seitenrand,
                    top = Mass.Mittel,
                    bottom = Mass.Klein,
                ),
        ) {
            PrimaerButton(
                text = if (state.laufendeSession != null) {
                    "Zurück in die laufende Session"
                } else {
                    "Vorschlag als Session starten"
                },
                beiKlick = if (state.laufendeSession != null) {
                    beiLaufendeSession
                } else {
                    beiVorschlagStarten
                },
            )
            TextAktion(text = "Eigene Session planen", beiKlick = beiEigeneSession)
        }
    }
}

@Composable
private fun LaufendeSessionHinweis(
    session: LaufendeSession,
    beiKlick: () -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        Modifier
            .fillMaxWidth()
            .background(stifte.tinteHauch)
            .clickable(onClick = beiKlick)
            .padding(horizontal = Mass.Seitenrand, vertical = Mass.Mittel),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Zyklus ${session.zyklusNummer} von ${session.zyklenGesamt} läuft",
                style = MaterialTheme.typography.titleMedium,
                color = stifte.tinte,
            )
            Text(
                text = "Box: ${session.boxZeit}",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.tinte,
            )
        }
        LuftBreit(12.dp)
        Box(
            Modifier
                .border(1.dp, stifte.tinte, Radius.Chip)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text(
                text = "Weiter",
                style = MaterialTheme.typography.labelSmall,
                color = stifte.tinte,
            )
        }
    }
}
