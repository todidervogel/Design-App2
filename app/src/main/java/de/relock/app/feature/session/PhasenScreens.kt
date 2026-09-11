package de.relock.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.relock.app.data.Aufgabe
import de.relock.app.data.Scan
import de.relock.app.data.Serverzustand
import de.relock.app.data.fake.FakeData
import de.relock.app.data.uhr
import de.relock.app.ui.components.Ausloeser
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.components.KameraPlatzhalter
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.Nachtflaeche
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.ScanThumb
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.Segmente
import de.relock.app.ui.components.ServerStatus
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.seitenPlatzhalter
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal

// ========================================================= 6.10 Aufgaben erhalten

/**
 * Aufgaben erhalten, DESIGN.md 6.10.
 * Danach geht das Handy zurueck in die Box.
 */
data class AufgabenErhaltenUiState(
    val fachName: String = "Mathe",
    val minuten: Int = 20,
    val aufgaben: List<Aufgabe> = FakeData.aufgaben.take(4),
)

@Composable
fun AufgabenErhaltenScreen(
    state: AufgabenErhaltenUiState,
    beiInDieBox: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Deine Aufgaben",
        unterzeile = "${state.fachName}, ${state.minuten} Min",
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaryButton(text = "In die Box", beiKlick = beiInDieBox)
                QuietButton(text = "Session abbrechen", beiKlick = beiAbbrechen)
            }
        },
    ) {
        Luft(Mass.Klein)
        Trennlinie()

        state.aufgaben.forEach { aufgabe ->
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Mass.Rand, vertical = 14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = aufgabe.quelle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Elfenbein,
                        )
                        Luft(2.dp)
                        Text(
                            text = aufgabe.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = farben.matt,
                        )
                    }
                    if (aufgabe.hatBild) {
                        LuftBreit(14.dp)
                        // Kleines Bild bei Diagrammaufgaben, DESIGN.md 6.10.
                        Box(
                            Modifier
                                .width(56.dp)
                                .height(56.dp)
                                .background(farben.flaecheHoch, Radius.Chip)
                                .border(1.dp, farben.linie, Radius.Chip)
                                .seitenPlatzhalter(farben.leise),
                        )
                    }
                }
                Trennlinie()
            }
        }

        Luft(Mass.Gross)
        Box(
            Modifier
                .padding(horizontal = Mass.Rand)
                .fillMaxWidth()
                .background(farben.flaeche, Radius.Flaeche)
                .padding(Mass.Mittel),
        ) {
            Text(
                text = "Löse alles auf Papier, ohne Lösungen. Schreib die " +
                    "Aufgabennummern dazu.",
                style = MaterialTheme.typography.bodyMedium,
                color = Elfenbein,
            )
        }
        Luft(Mass.Gross)
    }
}

// ================================================================ 6.11 Scannen

/**
 * Scannen, DESIGN.md 6.11.
 * Der Seitenrahmen steht in Messing, weil er sagt, wo das Blatt hin soll.
 */
data class ScannenUiState(
    val titel: String = "Scanne alle Seiten mit deinen Lösungen.",
    val scans: List<Scan> = FakeData.scans,
    val fehler: String? = null,
)

@Composable
fun ScannenScreen(
    state: ScannenUiState,
    beiAusloesen: () -> Unit,
    beiScan: (Scan) -> Unit,
    beiFertig: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Text(
            text = state.titel,
            style = MaterialTheme.typography.bodyLarge,
            color = Elfenbein,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Mass.Rand),
        )

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            KameraPlatzhalter()
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.72f)
                    .fillMaxHeight(0.78f)
                    .border(2.dp, Messing, Radius.Chip),
            )
        }

        if (state.fehler != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(de.relock.app.ui.theme.SignalTief)
                    .padding(horizontal = Mass.Rand, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.fehler,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Signal,
                )
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = Mass.Mittel),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Mass.Rand),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.scans.forEach { scan ->
                    ScanThumb(scan = scan, beiKlick = { beiScan(scan) })
                }
            }

            Luft(Mass.Mittel)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Abbrechen",
                    style = MaterialTheme.typography.labelLarge,
                    color = farben.matt,
                    modifier = Modifier
                        .width(90.dp)
                        .leiseKlickbar(beiAbbrechen)
                        .padding(vertical = 14.dp),
                )
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Ausloeser(beiKlick = beiAusloesen)
                }
                Text(
                    text = "Fertig (${state.scans.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = Messing,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .width(90.dp)
                        .leiseKlickbar(beiFertig)
                        .padding(vertical = 14.dp),
                )
            }
        }
    }
}

// =============================================== 6.12 Weitermachen oder aufhoeren

/**
 * Weitermachen oder aufhoeren, DESIGN.md 6.12.
 */
data class WeiterUiState(
    val zyklus: Int = 1,
    val zyklenGesamt: Int = 3,
    val server: Serverzustand = Serverzustand.Korrigiert(1, 3, "Seiten"),
    val pausenmoeglichkeiten: List<Int> = listOf(5, 10, 15),
    val gewaehlt: Int = 1,
)

@Composable
fun WeiterOderAufhoerenScreen(
    state: WeiterUiState,
    beiPausenwahl: (Int) -> Unit,
    beiWeitermachen: () -> Unit,
    beiAufhoeren: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Zyklus ${state.zyklus} von ${state.zyklenGesamt} geschafft.",
                style = MaterialTheme.typography.headlineMedium,
                color = Elfenbein,
                textAlign = TextAlign.Center,
            )
            Luft(Mass.Mittel)
            ServerStatus(state.server)

            Luft(Mass.Weit)
            Segmente(
                beschriftungen = state.pausenmoeglichkeiten.map { "$it Min" },
                gewaehlt = state.gewaehlt,
                beiWahl = beiPausenwahl,
            )
            Luft(Mass.Klein)
            PrimaryButton(text = "Weitermachen", beiKlick = beiWeitermachen)

            Luft(Mass.Gross)
            SecondaryButton(text = "Aufhören", beiKlick = beiAufhoeren)
        }
    }
}

// ================================================================== 6.13 Pause

/**
 * Pause, DESIGN.md 6.13.
 */
data class PauseUiState(
    val restSekunden: Int = 552,
    val server: Serverzustand = Serverzustand.Korrigiert(2, 3, "Seiten"),
    val vorbei: Boolean = false,
)

@Composable
fun PauseScreen(
    state: PauseUiState,
    beiWeiter: () -> Unit,
    beiUeberspringen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Luft(Mass.Weit)
            Text(
                text = "Pause",
                style = MaterialTheme.typography.headlineMedium,
                color = Elfenbein,
            )
            Luft(Mass.Mittel)
            Text(
                text = uhr(state.restSekunden),
                style = MaterialTheme.typography.displayLarge,
                color = Elfenbein,
            )
            Luft(Mass.Gross)
            Text(
                text = "Aufstehen, trinken, Fenster auf.",
                style = MaterialTheme.typography.bodyLarge,
                color = farben.matt,
                textAlign = TextAlign.Center,
            )

            Box(Modifier.weight(1f))

            if (state.vorbei) {
                Text(
                    text = "Pause vorbei. Weiter mit der Verbesserung.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                    textAlign = TextAlign.Center,
                )
                Luft(Mass.Mittel)
                PrimaryButton(text = "Zur Verbesserung", beiKlick = beiWeiter)
            } else {
                ServerStatus(state.server)
                Luft(Mass.Mittel)
                QuietButton(text = "Pause überspringen", beiKlick = beiUeberspringen)
            }
            Luft(Mass.Gross)
        }
    }
}
