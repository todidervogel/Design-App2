package de.relock.app.feature.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.data.Faelligkeit
import de.relock.app.data.Hausaufgabe
import de.relock.app.data.Scan
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.ScanThumb
import de.relock.app.ui.components.SubjectMark
import de.relock.app.ui.components.seitenPlatzhalter
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

/**
 * Hausaufgabe im Detail, DESIGN.md 6.5.
 *
 * "Als erledigt markieren" fragt nach, weil ohne Scan kein Guthaben
 * entsteht - das ist der Punkt, an dem man sich selbst betruegen koennte.
 */
data class HausaufgabeDetailUiState(
    val hausaufgabe: Hausaufgabe = FakeData.hausaufgaben.first(),
    val scans: List<Scan> = emptyList(),
)

@Composable
fun HausaufgabeDetailScreen(
    state: HausaufgabeDetailUiState,
    beiZurueck: () -> Unit,
    beiSeiteOeffnen: () -> Unit,
    beiJetztMachen: () -> Unit,
    beiErledigt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val hausaufgabe = state.hausaufgabe
    val fach = FakeData.fach(hausaufgabe.fachId)
    var nachfrageOffen by remember { mutableStateOf(false) }

    Geruest(
        titel = hausaufgabe.aufgabe,
        zurueck = beiZurueck,
        scrollbar = true,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaryButton(text = "Jetzt machen", beiKlick = beiJetztMachen)
                if (!hausaufgabe.erledigt) {
                    QuietButton(
                        text = "Als erledigt markieren",
                        beiKlick = { nachfrageOffen = true },
                    )
                }
            }
        },
    ) {
        Row(
            Modifier.padding(horizontal = Mass.Rand),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubjectMark(fach = fach, groesse = 28.dp)
            LuftBreit(10.dp)
            Text(
                text = fach.name,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
            )
            LuftBreit(10.dp)
            Text(text = "·", style = MaterialTheme.typography.bodyMedium, color = farben.linie)
            LuftBreit(10.dp)
            Text(
                text = hausaufgabe.faellig.anzeige,
                style = MaterialTheme.typography.bodyMedium,
                color = if (hausaufgabe.faellig == Faelligkeit.Ueberfaellig) Signal else farben.matt,
            )
        }

        // Verknuepfte Buchseite
        if (hausaufgabe.bezug != null) {
            Abschnitt("Buchseite")
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .width(62.dp)
                        .height(84.dp)
                        .background(farben.flaecheHoch, Radius.Chip)
                        .seitenPlatzhalter(farben.leise, dicht = true),
                )
                LuftBreit(Mass.Mittel)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = hausaufgabe.bezug.buchTitel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    Text(
                        text = "Seite ${hausaufgabe.bezug.seite} · " +
                            "${hausaufgabe.bezug.aufgabenImBrain} Aufgaben im Brain",
                        style = MaterialTheme.typography.labelSmall,
                        color = farben.matt,
                    )
                    Luft(8.dp)
                    Text(
                        text = "Seite öffnen",
                        style = MaterialTheme.typography.labelLarge,
                        color = Messing,
                        modifier = Modifier.leiseKlickbar(beiSeiteOeffnen),
                    )
                }
            }
        } else {
            Abschnitt("Buchseite")
            Hinweis(
                text = "Diese Hausaufgabe ist mit keiner Seite im Brain verknüpft.",
                modifier = Modifier.padding(horizontal = Mass.Rand),
            )
        }

        // Foto
        if (hausaufgabe.hatFoto) {
            Abschnitt("Foto")
            Box(
                Modifier
                    .padding(horizontal = Mass.Rand)
                    .width(110.dp)
                    .height(150.dp)
                    .background(farben.flaecheHoch, Radius.Chip)
                    .seitenPlatzhalter(farben.leise, dicht = true),
            )
        }

        // Verlauf
        Abschnitt("Verlauf")
        if (state.scans.isEmpty() && hausaufgabe.ergebnis == null) {
            Hinweis(
                text = "Noch nichts gescannt.",
                modifier = Modifier.padding(horizontal = Mass.Rand),
            )
        } else {
            if (state.scans.isNotEmpty()) {
                Row(
                    Modifier.padding(horizontal = Mass.Rand),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.scans.forEach { scan -> ScanThumb(scan) }
                }
                Luft(Mass.Mittel)
            }
            if (hausaufgabe.ergebnis != null) {
                Row(
                    Modifier.padding(horizontal = Mass.Rand),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = hausaufgabe.ergebnis,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Richtig,
                    )
                }
            }
        }

        Luft(Mass.Gross)
    }

    if (nachfrageOffen) {
        AlertDialog(
            onDismissRequest = { nachfrageOffen = false },
            containerColor = farben.flaeche,
            titleContentColor = Elfenbein,
            textContentColor = farben.matt,
            shape = Radius.Flaeche,
            title = { Text("Ohne Scan gibt es kein Guthaben") },
            text = {
                Text(
                    "Du kannst die Aufgabe abhaken, aber ohne gescannte Lösung " +
                        "bekommst du keine Zeit gutgeschrieben.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    beiErledigt()
                    nachfrageOffen = false
                }) {
                    Text("Trotzdem abhaken", color = Messing)
                }
            },
            dismissButton = {
                TextButton(onClick = { nachfrageOffen = false }) {
                    Text("Doch nicht", color = farben.matt)
                }
            },
        )
    }
}
