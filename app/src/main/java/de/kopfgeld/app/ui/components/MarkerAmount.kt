package de.kopfgeld.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme

/**
 * Zahl mit Textmarker-Streifen, DESIGN.md 5.
 *
 * Der Streifen sitzt unten, ist leicht schraeg und deckt ungefaehr 60 % der
 * Zahlenhoehe ab. Er zieht sich in 450 ms hinter die Zahl - einer der genau
 * zwei inszenierten Momente der App (DESIGN.md 4.4).
 *
 * Der Marker ist nie Textfarbe. Nur diese Flaeche.
 */
@Composable
fun MarkerAmount(
    wert: String,
    modifier: Modifier = Modifier,
    einheit: String? = null,
    animieren: Boolean = true,
    stil: TextStyle = MaterialTheme.typography.displayMedium,
) {
    val reduziert = KopfgeldTheme.reduzierteBewegung
    val sofort = !animieren || reduziert

    val fortschritt = remember { Animatable(if (sofort) 1f else 0f) }
    LaunchedEffect(wert, sofort) {
        if (sofort) {
            fortschritt.snapTo(1f)
        } else {
            fortschritt.snapTo(0f)
            fortschritt.animateTo(1f, tween(durationMillis = 450, easing = FastOutSlowInEasing))
        }
    }

    val markerfarbe = KopfgeldTheme.stifte.marker

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = wert,
            style = stil,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.drawBehind {
                val ueberstand = 5.dp.toPx()
                val streifenhoehe = size.height * 0.55f
                // Der Streifen sitzt unten, aber nicht ganz auf der Grundlinie.
                val oben = size.height * 0.78f - streifenhoehe
                val schraege = size.height * 0.05f
                val links = -ueberstand
                val rechts = (size.width + ueberstand * 2f) * fortschritt.value - ueberstand

                if (rechts <= links) return@drawBehind

                val pfad = Path().apply {
                    moveTo(links, oben + schraege)
                    lineTo(rechts, oben - schraege)
                    lineTo(rechts, oben - schraege + streifenhoehe)
                    lineTo(links, oben + schraege + streifenhoehe)
                    close()
                }
                drawPath(pfad, markerfarbe)
            },
        )
        if (einheit != null) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = einheit,
                style = MaterialTheme.typography.bodyLarge,
                color = KopfgeldTheme.stifte.blei,
                // Optisch auf die Grundlinie der grossen Zahl gesetzt.
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
    }
}
