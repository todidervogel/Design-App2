package de.kopfgeld.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.data.Sperrstand
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Radius

/**
 * Sperrzustand als eine Zeile, DESIGN.md 5.
 *
 *   gesperrt      roter Punkt, Text in Korrekturrot
 *   Speicherzeit  Mini-Kreis-Countdown, Text in Blei
 *   frei          Tinte
 */
@Composable
fun LockStatusLine(
    stand: Sperrstand,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (stand) {
            is Sperrstand.Gesperrt -> {
                Punkt(stifte.korrektur)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Scroll-Apps gesperrt",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.korrektur,
                )
            }

            is Sperrstand.Speicherzeit -> {
                val anteil = if (stand.gesamtMinuten <= 0) {
                    0f
                } else {
                    stand.restMinuten.toFloat() / stand.gesamtMinuten.toFloat()
                }
                MiniKreis(anteil = anteil.coerceIn(0f, 1f), farbe = stifte.blei)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Speicherzeit: noch ${stand.restMinuten} Min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }

            is Sperrstand.Frei -> {
                Punkt(stifte.tinte, gefuellt = false)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Frei bis ${stand.bisUhrzeit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.tinte,
                )
            }
        }
    }
}

/**
 * Zustand des Brain-Servers als Chip, DESIGN.md 5.
 *
 * Der Chip ist oft sichtbar, weil der Server laut Auftrag langsam ist und
 * Warten der Normalfall, nicht der Fehlerfall.
 */
@Composable
fun ServerStatusChip(
    stand: Serverstand,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val (farbe, text) = when (stand) {
        is Serverstand.Verbunden -> stifte.tinte to "Brain verbunden"
        is Serverstand.NichtErreichbar ->
            stifte.blei to "Server nicht erreichbar, ${stand.wartendeScans} Scans warten"

        is Serverstand.Analysiert -> {
            val zusatz = if (stand.einheit.isBlank()) "" else " ${stand.einheit}"
            stifte.tinte to "Analysiert ${stand.fertig} von ${stand.gesamt}$zusatz"
        }
    }

    Row(
        modifier = modifier
            .background(
                color = if (stand is Serverstand.NichtErreichbar) stifte.tief else stifte.tinteHauch,
                shape = Radius.Chip,
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (stand) {
            is Serverstand.Analysiert -> {
                val anteil = if (stand.gesamt <= 0) {
                    0f
                } else {
                    stand.fertig.toFloat() / stand.gesamt.toFloat()
                }
                MiniKreis(anteil = anteil.coerceIn(0f, 1f), farbe = farbe)
            }

            else -> Punkt(farbe)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = farbe,
        )
    }
}

@Composable
private fun Punkt(
    farbe: Color,
    gefuellt: Boolean = true,
) {
    if (gefuellt) {
        Spacer(
            Modifier
                .size(8.dp)
                .background(farbe, CircleShape),
        )
    } else {
        Spacer(
            Modifier
                .size(8.dp)
                .border(1.dp, farbe, CircleShape),
        )
    }
}

/** Kleiner Kreis-Countdown: der Bogen zeigt den Rest. */
@Composable
private fun MiniKreis(
    anteil: Float,
    farbe: Color,
) {
    val spur = farbe.copy(alpha = 0.25f)
    Canvas(Modifier.size(12.dp)) {
        val staerke = 2.dp.toPx()
        val einzug = staerke / 2f
        val kante = Size(
            size.width - staerke,
            size.height - staerke,
        )
        drawArc(
            color = spur,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(einzug, einzug),
            size = kante,
            style = Stroke(width = staerke, cap = StrokeCap.Round),
        )
        drawArc(
            color = farbe,
            startAngle = -90f,
            sweepAngle = 360f * anteil,
            useCenter = false,
            topLeft = Offset(einzug, einzug),
            size = kante,
            style = Stroke(width = staerke, cap = StrokeCap.Round),
        )
    }
}
