package de.relock.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import de.relock.app.data.Sessionplan
import de.relock.app.data.Zyklus
import de.relock.app.data.Zyklustyp
import de.relock.app.data.dauer
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Chip
import de.relock.app.ui.components.CycleRow
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PauseRow
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.SymbolPlus
import de.relock.app.ui.components.SymbolZyklustyp
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.Zeile
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme
import kotlin.math.roundToInt

/**
 * Session planen, DESIGN.md 6.6.
 *
 * "Hausaufgaben starten" auf Heute oeffnet denselben Screen, nur mit allen
 * offenen Hausaufgaben schon als Zyklen drin.
 */
data class SessionPlanenUiState(
    val titel: String = "Neue Session",
    val plan: Sessionplan = FakeData.sessionplan,
) {
    val gesamt: String get() = dauer(plan.gesamtMinuten)
}

@Composable
fun SessionPlanenScreen(
    state: SessionPlanenUiState,
    beiZurueck: () -> Unit,
    beiZyklus: (Zyklus) -> Unit,
    beiZyklusVerschieben: (Int, Int) -> Unit,
    beiZyklusHinzufuegen: () -> Unit,
    beiNachsperre: () -> Unit,
    beiStarten: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val dichte = LocalDensity.current

    Geruest(
        titel = state.titel,
        unterzeile = state.gesamt,
        zurueck = beiZurueck,
        modifier = modifier,
        fussleiste = {
            PrimaryButton(
                text = "Session starten",
                beiKlick = beiStarten,
                aktiv = state.plan.zyklen.isNotEmpty(),
            )
        },
    ) {
        Luft(Mass.Klein)

        state.plan.zyklen.forEachIndexed { index, zyklus ->
            if (index > 0) {
                PauseRow(
                    vonMinuten = state.plan.pauseVonMinuten,
                    bisMinuten = state.plan.pauseBisMinuten,
                )
            }
            CycleRow(
                zyklus = zyklus,
                beiKlick = { beiZyklus(zyklus) },
                ziehgriffModifier = Modifier.sortiergriff(
                    index = index,
                    anzahl = state.plan.zyklen.size,
                    dichte = dichte,
                    beiVerschieben = beiZyklusVerschieben,
                ),
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = beiZyklusHinzufuegen)
                .padding(horizontal = Mass.Rand, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SymbolPlus()
            LuftBreit(10.dp)
            Text(
                text = "Zyklus",
                style = MaterialTheme.typography.labelLarge,
                color = Messing,
            )
        }
        Trennlinie()

        Zeile(beiKlick = beiNachsperre, mitLinie = false) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nachsperre nach dem Aufhören",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${state.plan.nachsperreMinuten} Min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = farben.matt,
                )
            }
        }

        Luft(Mass.Gross)
    }
}

/**
 * Ziehen zum Umsortieren. Nur senkrecht und nur auf dem Griff - sonst
 * wuerde die Geste das Wischen in Listen schlucken.
 */
private fun Modifier.sortiergriff(
    index: Int,
    anzahl: Int,
    dichte: Density,
    beiVerschieben: (Int, Int) -> Unit,
): Modifier = this.pointerInput(index, anzahl) {
    var gesammelt = 0f
    val zeilenhoehe = with(dichte) { 68.dp.toPx() }
    detectVerticalDragGestures(
        onDragEnd = { gesammelt = 0f },
        onDragCancel = { gesammelt = 0f },
    ) { _, verschiebung ->
        gesammelt += verschiebung
        if (gesammelt > zeilenhoehe && index < anzahl - 1) {
            beiVerschieben(index, index + 1)
            gesammelt = 0f
        } else if (gesammelt < -zeilenhoehe && index > 0) {
            beiVerschieben(index, index - 1)
            gesammelt = 0f
        }
    }
}

// --- Typauswahl ------------------------------------------------------------

/**
 * Die Typauswahl hinter "+ Zyklus", DESIGN.md 6.6.
 * Je Typ ein Symbol und ein Satz, der erklaert, was passiert.
 */
@Composable
fun TypauswahlInhalt(
    beiTyp: (Zyklustyp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Column(modifier.fillMaxWidth()) {
        Zyklustyp.entries.forEach { typ ->
            Zeile(beiKlick = { beiTyp(typ) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SymbolZyklustyp(typ)
                    LuftBreit(16.dp)
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = typ.anzeige,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Elfenbein,
                        )
                        Text(
                            text = typ.satz,
                            style = MaterialTheme.typography.labelSmall,
                            color = farben.matt,
                        )
                    }
                }
            }
        }
    }
}

// --- Zyklus bearbeiten -----------------------------------------------------

data class ZyklusBearbeitenUiState(
    val zyklus: Zyklus,
    val themen: List<String> = emptyList(),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZyklusBearbeitenInhalt(
    state: ZyklusBearbeitenUiState,
    beiFach: (String) -> Unit,
    beiThemaUmschalten: (String) -> Unit,
    beiLernMinuten: (Int) -> Unit,
    beiAufgabenMinuten: (Int) -> Unit,
    beiAufgabenwahl: () -> Unit,
    beiUebernehmen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val zyklus = state.zyklus

    Column(modifier.fillMaxWidth().padding(horizontal = Mass.Rand)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SymbolZyklustyp(zyklus.typ)
            LuftBreit(12.dp)
            Text(
                text = zyklus.typ.anzeige,
                style = MaterialTheme.typography.titleMedium,
                color = Elfenbein,
            )
        }
        Luft(6.dp)
        Hinweis(zyklus.typ.satz)

        Luft(Mass.Gross)
        Text("Fach", style = MaterialTheme.typography.titleMedium, color = Elfenbein)
        Luft(Mass.Klein)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FakeData.faecher.forEach { fach ->
                Chip(
                    text = fach.name,
                    gewaehlt = fach.id == zyklus.fachId,
                    beiKlick = { beiFach(fach.id) },
                )
            }
        }

        Luft(Mass.Gross)
        Text("Thema", style = MaterialTheme.typography.titleMedium, color = Elfenbein)
        Luft(Mass.Klein)
        val themen = FakeData.themenVon(zyklus.fachId)
        if (themen.isEmpty()) {
            Hinweis("Für dieses Fach sind noch keine Themen im Brain.")
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                themen.forEach { thema ->
                    Chip(
                        text = thema.name,
                        gewaehlt = thema.name in state.themen,
                        beiKlick = { beiThemaUmschalten(thema.name) },
                    )
                }
            }
        }

        Luft(Mass.Gross)
        Text("Dauer", style = MaterialTheme.typography.titleMedium, color = Elfenbein)
        Luft(Mass.Klein)
        Minutenregler(
            beschriftung = "Lernphase",
            minuten = zyklus.lernMinuten,
            beiAenderung = beiLernMinuten,
        )
        Luft(Mass.Klein)
        Minutenregler(
            beschriftung = "Aufgabenphase",
            minuten = zyklus.aufgabenMinuten,
            beiAenderung = beiAufgabenMinuten,
        )
        Luft(Mass.Klein)
        Hinweis("Zusammen ${dauer(zyklus.minuten)}")

        if (zyklus.typ == Zyklustyp.Aufgaben) {
            Luft(Mass.Gross)
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(farben.flaecheHoch, de.relock.app.ui.theme.Radius.Eingabe)
                    .clickable(onClick = beiAufgabenwahl)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${zyklus.aufgabenAnzahl} Aufgaben ausgewählt",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Elfenbein,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Ändern",
                    style = MaterialTheme.typography.labelLarge,
                    color = Messing,
                )
            }
        }

        Luft(Mass.Gross)
        PrimaryButton(text = "Übernehmen", beiKlick = beiUebernehmen)
    }
}

/** Slider in 5-Min-Schritten mit der Zahl daneben. */
@Composable
fun Minutenregler(
    beschriftung: String,
    minuten: Int,
    beiAenderung: (Int) -> Unit,
    modifier: Modifier = Modifier,
    von: Int = 0,
    bis: Int = 60,
    schritt: Int = 5,
) {
    val farben = RelockTheme.farben
    val schritte = ((bis - von) / schritt - 1).coerceAtLeast(0)

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = beschriftung,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$minuten Min",
                style = MaterialTheme.typography.bodyLarge,
                color = Elfenbein,
            )
        }
        Slider(
            value = minuten.toFloat(),
            onValueChange = { neu ->
                beiAenderung(((neu / schritt).roundToInt() * schritt).coerceIn(von, bis))
            },
            valueRange = von.toFloat()..bis.toFloat(),
            steps = schritte,
            colors = SliderDefaults.colors(
                thumbColor = Messing,
                activeTrackColor = Messing,
                inactiveTrackColor = farben.linie,
                activeTickColor = Messing.copy(alpha = 0.4f),
                inactiveTickColor = farben.linie,
            ),
        )
    }
}
