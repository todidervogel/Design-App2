package de.relock.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import de.relock.app.data.Serverzustand
import de.relock.app.data.Sperrzustand
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal

/**
 * Der Sperrzustand als eine Zeile, DESIGN.md 3.
 *
 *   gesperrt       Signal-Punkt
 *   frei bis       Messing-Punkt
 *   Nachsperre     kleiner Ring, der leerlaeuft
 *   Session laeuft Messing-Punkt
 */
@Composable
fun LockStatusLine(
    zustand: Sperrzustand,
    modifier: Modifier = Modifier,
    gesamtNachsperre: Int = 30,
) {
    val farben = RelockTheme.farben

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        when (zustand) {
            Sperrzustand.Gesperrt -> {
                Statuspunkt(farbe = Signal)
                LuftBreit(8.dp)
                Text(
                    text = "Gesperrt",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Signal,
                )
            }

            is Sperrzustand.Frei -> {
                Statuspunkt(farbe = Messing)
                LuftBreit(8.dp)
                Text(
                    text = "Frei bis ${zustand.bis}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Messing,
                )
            }

            is Sperrzustand.Nachsperre -> {
                val anteil = if (gesamtNachsperre <= 0) {
                    0f
                } else {
                    zustand.restMinuten.toFloat() / gesamtNachsperre.toFloat()
                }
                Miniring(anteil = anteil.coerceIn(0f, 1f), farbe = farben.matt)
                LuftBreit(8.dp)
                Text(
                    text = "Nachsperre: noch ${zustand.restMinuten} Min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = farben.matt,
                )
            }

            Sperrzustand.SessionLaeuft -> {
                Statuspunkt(farbe = Messing, gefuellt = false)
                LuftBreit(8.dp)
                Text(
                    text = "Session läuft",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Messing,
                )
            }
        }
    }
}

/**
 * Der Zustand des Brain-Servers, DESIGN.md 3.
 *
 * Steht an vielen Stellen, weil der Server langsam ist: Warten ist der
 * Normalfall, nicht der Fehlerfall.
 */
@Composable
fun ServerStatus(
    zustand: Serverzustand,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        when (zustand) {
            Serverzustand.Verbunden -> {
                Statuspunkt(farbe = Messing, groesse = 6.dp)
                LuftBreit(8.dp)
                Text(
                    text = "Brain verbunden",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }

            is Serverzustand.Offline -> {
                Statuspunkt(farbe = farben.leise, groesse = 6.dp, gefuellt = false)
                LuftBreit(8.dp)
                Text(
                    text = "Server offline, ${zustand.wartendeScans} Scans warten",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }

            is Serverzustand.Korrigiert -> {
                val anteil = if (zustand.gesamt <= 0) {
                    0f
                } else {
                    zustand.fertig.toFloat() / zustand.gesamt.toFloat()
                }
                Miniring(anteil = anteil.coerceIn(0f, 1f), farbe = Messing, groesse = 11.dp)
                LuftBreit(8.dp)
                Text(
                    text = buildString {
                        append("Korrigiert ${zustand.fertig} von ${zustand.gesamt}")
                        if (zustand.einheit.isNotBlank()) append(" ${zustand.einheit}")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
        }
    }
}

/** Kleiner Ring, der einen Anteil zeigt. */
@Composable
private fun Miniring(
    anteil: Float,
    farbe: Color,
    groesse: androidx.compose.ui.unit.Dp = 12.dp,
) {
    val spur = farbe.copy(alpha = 0.25f)
    Canvas(Modifier.size(groesse)) {
        val staerke = 1.5.dp.toPx()
        val einzug = staerke / 2f
        val kante = Size(size.width - staerke, size.height - staerke)
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
