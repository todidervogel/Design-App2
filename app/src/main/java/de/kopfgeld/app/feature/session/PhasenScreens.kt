package de.kopfgeld.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Aufgabe
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Scan
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.data.formatiereUhr
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.KameraPlatzhalter
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MerkChip
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.ScanThumb
import de.kopfgeld.app.ui.components.SegmentWahl
import de.kopfgeld.app.ui.components.ServerStatusChip
import de.kopfgeld.app.ui.components.TafelScaffold
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.components.karoRaster
import de.kopfgeld.app.ui.theme.Kreide
import de.kopfgeld.app.ui.theme.KreideMatt
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

// ======================================================= 7.10 Aufgaben erhalten

/**
 * Aufgaben erhalten, DESIGN.md 7.10. Heft-Look.
 * Danach geht das Handy zurueck in die Box.
 */
data class AufgabenErhaltenUiState(
    val fachName: String = "Mathe",
    val aufgabenphaseMinuten: Int = 20,
    val aufgaben: List<Aufgabe> = FakeData.aufgaben.filter {
        it.id in listOf("a1", "a3", "a4")
    },
)

@Composable
fun AufgabenErhaltenScreen(
    state: AufgabenErhaltenUiState,
    beiZurueckInDieBox: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Deine Aufgaben",
        unterzeile = "${state.fachName}, Aufgabenphase ${state.aufgabenphaseMinuten} Min",
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(
                    text = "Handy in die Box, Aufgabenphase starten",
                    beiKlick = beiZurueckInDieBox,
                )
                TextAktion(text = "Session abbrechen", beiKlick = beiAbbrechen)
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Trennlinie()
            state.aufgaben.forEach { aufgabe ->
                AufgabenPosten(aufgabe)
            }

            Luft(Mass.Gross)
            Box(
                Modifier
                    .padding(horizontal = Mass.Seitenrand)
                    .fillMaxWidth()
                    .background(stifte.tief, Radius.Flaeche)
                    .padding(14.dp),
            ) {
                Text(
                    text = "Löse alles auf Papier, ohne Lösungen. Nummeriere deine " +
                        "Antworten wie die Aufgaben.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun AufgabenPosten(aufgabe: Aufgabe) {
    val stifte = KopfgeldTheme.stifte
    var offen by remember { mutableStateOf(false) }

    Column {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { offen = !offen }
                .padding(horizontal = Mass.Seitenrand, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = aufgabe.quellenangabe,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (offen) "zuklappen" else "Text zeigen",
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.tinte,
                )
            }
            if (offen) {
                Luft(Mass.Klein)
                Text(
                    text = aufgabe.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
                Luft(Mass.Klein)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MerkChip(aufgabe.typ)
                    if (aufgabe.hatLoesung) {
                        LuftBreit(8.dp)
                        Text(
                            text = "Lösung liegt im Brain, bleibt hier verborgen",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                }
            }
        }
        Trennlinie()
    }
}

// ================================================================ 7.11 Scannen

/**
 * Scannen, DESIGN.md 7.11. Kamera-Platzhalter mit Seitenrahmen.
 */
data class ScannenUiState(
    val titel: String = "Scanne alle Seiten mit deinen Lösungen.",
    val scans: List<Scan> = FakeData.scansSession,
)

@Composable
fun ScannenScreen(
    state: ScannenUiState,
    beiAusloesen: () -> Unit,
    beiScanEntfernen: (String) -> Unit,
    beiFertig: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = state.titel,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .karoRaster(KopfgeldTheme.stifte.karo)
                    .padding(Mass.Seitenrand),
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                KameraPlatzhalter()
                // Seitenrahmen: zeigt, wie das Blatt liegen soll.
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.7f)
                        .height(300.dp)
                        .border(2.dp, Kreide.copy(alpha = 0.8f), Radius.Chip),
                )
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
                        .padding(horizontal = Mass.Seitenrand),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.scans.forEach { scan ->
                        ScanThumb(scan = scan, beiKlick = { beiScanEntfernen(scan.id) })
                    }
                }

                Luft(Mass.Mittel)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Mass.Seitenrand),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextAktion(text = "Abbrechen", beiKlick = beiAbbrechen)
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Ausloeser(beiKlick = beiAusloesen)
                    }
                    TextAktion(
                        text = "Fertig (${state.scans.size})",
                        beiKlick = beiFertig,
                    )
                }
            }
        }
    }
}

/** Weisser Kreis mit Tinten-Ring, DESIGN.md 7.11. */
@Composable
private fun Ausloeser(beiKlick: () -> Unit) {
    val stifte = KopfgeldTheme.stifte
    Box(
        Modifier
            .size(68.dp)
            .border(3.dp, stifte.tinte, CircleShape)
            .clickable(onClick = beiKlick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape)
                .border(1.dp, stifte.karo, CircleShape),
        )
    }
}

// ================================================ 7.12 Weitermachen oder aufhoeren

/**
 * Weitermachen oder aufhoeren, DESIGN.md 7.12. Zentriert.
 */
data class WeiterUiState(
    val zyklusNummer: Int = 1,
    val zyklenGesamt: Int = 3,
    val server: Serverstand = Serverstand.Analysiert(1, 3, "Seiten"),
    val pausenwahl: Int = 10,
    val pausenmoeglichkeiten: List<Int> = listOf(5, 10, 15),
)

@Composable
fun WeiterOderAufhoerenScreen(
    state: WeiterUiState,
    beiPausenwahl: (Int) -> Unit,
    beiWeitermachen: () -> Unit,
    beiAufhoeren: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeftScaffold(
        titel = "",
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Mass.Seitenrand),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Zyklus ${state.zyklusNummer} von ${state.zyklenGesamt} geschafft.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Luft(Mass.Mittel)
            ServerStatusChip(state.server)

            Luft(Mass.Sehr)
            PrimaerButton(text = "Weitermachen: Pause starten", beiKlick = beiWeitermachen)
            Luft(Mass.Klein)
            SegmentWahl(
                werte = state.pausenmoeglichkeiten,
                gewaehlt = state.pausenwahl,
                beiWahl = beiPausenwahl,
            )

            Luft(Mass.Gross)
            ZweitButton(text = "Aufhören", beiKlick = beiAufhoeren)

            Luft(Mass.Gross)
            Hinweistext(
                text = "Deine Verbesserung ist nach der Pause bereit.",
                zentriert = true,
            )
        }
    }
}

// ================================================================== 7.13 Pause

/**
 * Pause, DESIGN.md 7.13. Tafel-Look.
 *
 * Der Auftragstext bricht in diesem Abschnitt mitten im Satz ab
 * ("Text: 'Aufstehen, trinken"). Der Rest des Satzes und die Buttons sind
 * hier sinngemaess ergaenzt und als abgeleitet markiert.
 */
data class PauseUiState(
    val restSekunden: Int = 552,
    val server: Serverstand = Serverstand.Analysiert(2, 3, "Seiten"),
    val verbesserungBereit: Boolean = false,
    val naechsterZyklus: String = "Zyklus 2 von 3: Englisch, Unit 3 Vokabeln",
)

@Composable
fun PauseScreen(
    state: PauseUiState,
    beiVerbesserungOeffnen: () -> Unit,
    beiPauseBeenden: () -> Unit,
    beiSpaeterVerbessern: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TafelScaffold(modifier = modifier) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Luft(Mass.Sehr)
            Text(
                text = "Pause",
                style = MaterialTheme.typography.headlineMedium,
                color = Kreide,
            )
            Luft(Mass.Mittel)
            Text(
                text = formatiereUhr(state.restSekunden),
                style = MaterialTheme.typography.displayLarge,
                color = Kreide,
            )

            Luft(Mass.Gross)
            Text(
                text = "Aufstehen, trinken, kurz aus dem Fenster schauen. Nicht scrollen.",
                style = MaterialTheme.typography.bodyLarge,
                color = KreideMatt,
                textAlign = TextAlign.Center,
            )

            Luft(Mass.Gross)
            ServerStatusChip(state.server)

            Box(Modifier.weight(1f))

            if (state.verbesserungBereit) {
                Text(
                    text = "Deine Verbesserung ist bereit.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Kreide,
                    textAlign = TextAlign.Center,
                )
                Luft(Mass.Klein)
                PrimaerButton(
                    text = "Verbesserung durchgehen",
                    beiKlick = beiVerbesserungOeffnen,
                )
                Luft(Mass.Klein)
                ZweitButton(
                    text = "Später verbessern",
                    beiKlick = beiSpaeterVerbessern,
                    farbe = KreideMatt,
                )
            } else {
                Text(
                    text = "Die Analyse läuft noch. Sie ist fertig, bevor die Pause vorbei ist.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KreideMatt,
                    textAlign = TextAlign.Center,
                )
                Luft(Mass.Klein)
                ZweitButton(
                    text = "Pause beenden, ${state.naechsterZyklus}",
                    beiKlick = beiPauseBeenden,
                    farbe = Kreide,
                )
            }
            Luft(Mass.Mittel)
        }
    }
}
