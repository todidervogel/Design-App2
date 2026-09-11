package de.kopfgeld.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import kotlin.math.roundToInt

/**
 * Slider mit 5-Min-Rastern und grosser Zahl darueber, DESIGN.md 5.
 *
 * Die Zahl steht bewusst gross ueber dem Regler: beim Planen einer Session
 * schaut man auf die Minuten, nicht auf den Knopf.
 */
@Composable
fun DurationSlider(
    beschriftung: String,
    minuten: Int,
    beiAenderung: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vonMinuten: Int = 0,
    bisMinuten: Int = 60,
    schrittMinuten: Int = 5,
    aktiv: Boolean = true,
) {
    val stifte = KopfgeldTheme.stifte
    val schritte = ((bisMinuten - vonMinuten) / schrittMinuten - 1).coerceAtLeast(0)

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = beschriftung,
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 6.dp),
            )
            Text(
                text = "$minuten",
                style = MaterialTheme.typography.displaySmall,
                color = if (aktiv) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    stifte.blei.copy(alpha = 0.5f)
                },
            )
            LuftBreit(6.dp)
            Text(
                text = "Min",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Slider(
            value = minuten.toFloat(),
            onValueChange = { neu ->
                val gerastert = (neu / schrittMinuten).roundToInt() * schrittMinuten
                beiAenderung(gerastert.coerceIn(vonMinuten, bisMinuten))
            },
            valueRange = vonMinuten.toFloat()..bisMinuten.toFloat(),
            steps = schritte,
            enabled = aktiv,
            colors = SliderDefaults.colors(
                thumbColor = stifte.tinte,
                activeTrackColor = stifte.tinte,
                inactiveTrackColor = stifte.karo,
                activeTickColor = stifte.tinte.copy(alpha = 0.4f),
                inactiveTickColor = stifte.karo,
            ),
        )
    }
}

/**
 * Zwei Griffe fuer den Pausenbereich, DESIGN.md 5.
 * Die Pause ist keine feste Zahl, sondern ein Rahmen: mindestens so viel,
 * hoechstens so viel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PausenRangeSlider(
    vonMinuten: Int,
    bisMinuten: Int,
    beiAenderung: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    kleinstes: Int = 5,
    groesstes: Int = 30,
    schrittMinuten: Int = 5,
) {
    val stifte = KopfgeldTheme.stifte
    val schritte = ((groesstes - kleinstes) / schrittMinuten - 1).coerceAtLeast(0)

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Pause",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 6.dp),
            )
            Text(
                text = "$vonMinuten–$bisMinuten",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            LuftBreit(6.dp)
            Text(
                text = "Min",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        RangeSlider(
            value = vonMinuten.toFloat()..bisMinuten.toFloat(),
            onValueChange = { bereich ->
                val neuVon = (bereich.start / schrittMinuten).roundToInt() * schrittMinuten
                val neuBis = (bereich.endInclusive / schrittMinuten).roundToInt() * schrittMinuten
                beiAenderung(
                    neuVon.coerceIn(kleinstes, groesstes),
                    neuBis.coerceIn(neuVon, groesstes),
                )
            },
            valueRange = kleinstes.toFloat()..groesstes.toFloat(),
            steps = schritte,
            colors = SliderDefaults.colors(
                thumbColor = stifte.tinte,
                activeTrackColor = stifte.tinte,
                inactiveTrackColor = stifte.karo,
                activeTickColor = stifte.tinte.copy(alpha = 0.4f),
                inactiveTickColor = stifte.karo,
            ),
        )
    }
}

/** Segmentwahl, z. B. Pausenlaenge 5 / 10 / 15 Min. */
@Composable
fun SegmentWahl(
    werte: List<Int>,
    gewaehlt: Int,
    beiWahl: (Int) -> Unit,
    modifier: Modifier = Modifier,
    einheit: String = "Min",
) {
    Row(modifier.fillMaxWidth()) {
        werte.forEachIndexed { index, wert ->
            TypChip(
                text = "$wert $einheit",
                gewaehlt = wert == gewaehlt,
                beiKlick = { beiWahl(wert) },
                zentriert = true,
                modifier = Modifier.weight(1f),
            )
            if (index < werte.lastIndex) LuftBreit(8.dp)
        }
    }
}
