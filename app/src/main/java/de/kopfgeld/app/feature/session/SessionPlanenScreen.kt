package de.kopfgeld.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Sessionplan
import de.kopfgeld.app.data.Testphase
import de.kopfgeld.app.data.Zyklus
import de.kopfgeld.app.data.Zyklustyp
import de.kopfgeld.app.data.formatiereDauer
import de.kopfgeld.app.ui.components.CycleBlock
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.PauseBlock
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.WischZumLoeschen
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius
import kotlinx.coroutines.launch

/**
 * Session planen, DESIGN.md 7.4. Der Zyklen-Editor.
 */
data class SessionPlanenUiState(
    val plan: Sessionplan = FakeData.beispielSession,
    val testphase: Testphase? = FakeData.testphase,
) {
    val gesamtText: String get() = "Gesamt: ${formatiereDauer(plan.gesamtMinuten)}"

    /**
     * In der Testphase werden Lern-Zyklen des betroffenen Fachs ausgegraut.
     * Siehe Lernstrategie: ab zwei Wochen vor der Arbeit nur noch testen.
     */
    fun istAusgegraut(zyklus: Zyklus): Boolean {
        val phase = testphase ?: return false
        if (zyklus.fachId != phase.fachId) return false
        return zyklus.typ == Zyklustyp.Lernen || zyklus.typ == Zyklustyp.Blurting
    }
}

@Composable
fun SessionPlanenScreen(
    state: SessionPlanenUiState,
    beiZurueck: () -> Unit,
    beiZyklusBearbeiten: (Zyklus) -> Unit,
    beiZyklusHinzufuegen: () -> Unit,
    beiZyklusLoeschen: (String) -> Unit,
    beiLoeschenRueckgaengig: () -> Unit,
    beiZyklusVerschieben: (Int, Int) -> Unit,
    beiPauseBearbeiten: () -> Unit,
    beiSpeicherzeit: () -> Unit,
    beiAlsVorlageSpeichern: (String) -> Unit,
    beiStarten: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val dichte = LocalDensity.current
    val bereich = rememberCoroutineScope()
    val meldungen = remember { SnackbarHostState() }
    var speichernOffen by remember { mutableStateOf(false) }
    var vorlagenname by remember { mutableStateOf("") }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Kopf
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Mass.Seitenrand - 12.dp,
                        end = Mass.Seitenrand,
                        top = Mass.Mittel,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(Mass.Tippziel)
                        .clickable(onClick = beiZurueck),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = state.plan.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = state.gesamtText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = stifte.blei,
                    )
                }
                TextAktion(
                    text = "Speichern",
                    beiKlick = {
                        vorlagenname = state.plan.name
                        speichernOffen = true
                    },
                )
            }

            Luft(Mass.Klein)

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (state.testphase != null) {
                    Box(
                        Modifier
                            .padding(horizontal = Mass.Seitenrand)
                            .fillMaxWidth()
                            .background(stifte.korrekturHauch, Radius.Flaeche)
                            .padding(12.dp),
                    ) {
                        Text(
                            text = "Testphase ${state.testphase.fachName}: Plane Aufgaben " +
                                "statt Lernphasen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.korrektur,
                        )
                    }
                    Luft(Mass.Mittel)
                }

                state.plan.zyklen.forEachIndexed { index, zyklus ->
                    if (index > 0) {
                        PauseBlock(
                            vonMinuten = state.plan.pauseVonMinuten,
                            bisMinuten = state.plan.pauseBisMinuten,
                            beiKlick = beiPauseBearbeiten,
                        )
                    }

                    WischZumLoeschen(
                        beiLoeschen = {
                            beiZyklusLoeschen(zyklus.id)
                            bereich.launch {
                                val ergebnis = meldungen.showSnackbar(
                                    message = "Zyklus gelöscht",
                                    actionLabel = "Rückgängig",
                                )
                                if (ergebnis == SnackbarResult.ActionPerformed) {
                                    beiLoeschenRueckgaengig()
                                }
                            }
                        },
                    ) {
                        CycleBlock(
                            zyklus = zyklus,
                            ausgegraut = state.istAusgegraut(zyklus),
                            ausgegrautHinweis = "In der Testphase ausgegraut: " +
                                "jetzt nur noch testen.",
                            beiKlick = { beiZyklusBearbeiten(zyklus) },
                            ziehgriffModifier = Modifier.ziehgriffVerschieben(
                                index = index,
                                anzahl = state.plan.zyklen.size,
                                dichte = dichte,
                                beiVerschieben = beiZyklusVerschieben,
                            ),
                        )
                    }
                    Luft(Mass.Klein)
                }

                Luft(Mass.Klein)
                Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(onClick = beiZyklusHinzufuegen)
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "+ Zyklus hinzufügen",
                            style = MaterialTheme.typography.labelLarge,
                            color = stifte.tinte,
                        )
                    }
                }
                Luft(Mass.Gross)
            }

            Trennlinie()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(Mass.Seitenrand),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = beiSpeicherzeit)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Hinweistext(
                        text = "Nach dem Aufhören: Speicherzeit " +
                            "${state.plan.speicherzeitMinuten} Min",
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "Ändern",
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.tinte,
                    )
                }
                PrimaerButton(
                    text = "Session starten",
                    beiKlick = beiStarten,
                    aktiv = state.plan.zyklen.isNotEmpty(),
                )
            }
        }

        SnackbarHost(
            hostState = meldungen,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { daten ->
            Snackbar(
                snackbarData = daten,
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.background,
                actionColor = stifte.marker,
            )
        }
    }

    if (speichernOffen) {
        AlertDialog(
            onDismissRequest = { speichernOffen = false },
            title = { Text("Als Vorlage speichern") },
            text = {
                OutlinedTextField(
                    value = vorlagenname,
                    onValueChange = { vorlagenname = it },
                    label = { Text("Name") },
                    singleLine = true,
                    shape = Radius.Eingabe,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    beiAlsVorlageSpeichern(vorlagenname)
                    speichernOffen = false
                }) {
                    Text("Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { speichernOffen = false }) { Text("Abbrechen") }
            },
            shape = Radius.Flaeche,
        )
    }
}

/**
 * Ziehen zum Umsortieren. Vereinfacht: sobald die Ziehstrecke die Hoehe eines
 * Blocks ueberschreitet, rutscht der Zyklus eine Position weiter. Das reicht
 * fuer Phase 1 und braucht keine eigene Reorder-Bibliothek.
 */
private fun Modifier.ziehgriffVerschieben(
    index: Int,
    anzahl: Int,
    dichte: Density,
    beiVerschieben: (Int, Int) -> Unit,
): Modifier = this.pointerInput(index, anzahl) {
    var gesammelt = 0f
    val blockhoehe = with(dichte) { 118.dp.toPx() }
    // Nur senkrecht, damit das waagerechte Wischen zum Loeschen frei bleibt.
    detectVerticalDragGestures(
        onDragEnd = { gesammelt = 0f },
        onDragCancel = { gesammelt = 0f },
    ) { _, verschiebung ->
        gesammelt += verschiebung
        if (gesammelt > blockhoehe && index < anzahl - 1) {
            beiVerschieben(index, index + 1)
            gesammelt = 0f
        } else if (gesammelt < -blockhoehe && index > 0) {
            beiVerschieben(index, index - 1)
            gesammelt = 0f
        }
    }
}
