package de.kopfgeld.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Karoraster, DESIGN.md 4.3: 20-dp-Kaestchen, 1-dp-Linien, 60 % Deckkraft.
 *
 * Bewusst sparsam einsetzen. Laut Auftrag nur auf: Heute (oben), Scan-,
 * Import- und Verbesserungs-Screens, Probearbeit-Ergebnis. Ueberall sonst
 * wird daraus Tapete.
 */
fun Modifier.karoRaster(
    linienfarbe: Color,
    kaestchen: Dp = Mass.Karokaestchen,
    deckkraft: Float = Mass.KaroDeckkraft,
): Modifier = this.drawBehind {
    val schritt = kaestchen.toPx()
    if (schritt <= 0f) return@drawBehind
    val staerke = Mass.Karolinie.toPx()
    val farbe = linienfarbe.copy(alpha = deckkraft)

    var x = schritt
    while (x < size.width) {
        drawLine(farbe, Offset(x, 0f), Offset(x, size.height), staerke)
        x += schritt
    }
    var y = schritt
    while (y < size.height) {
        drawLine(farbe, Offset(0f, y), Offset(size.width, y), staerke)
        y += schritt
    }
}

/**
 * Flaeche mit Karoraster darunter. Der Inhalt liegt darauf.
 */
@Composable
fun KaroBackground(
    modifier: Modifier = Modifier,
    grundfarbe: Color = Color.Unspecified,
    linienfarbe: Color = Color.Unspecified,
    inhalt: @Composable BoxScope.() -> Unit,
) {
    val grund = if (grundfarbe == Color.Unspecified) {
        androidx.compose.material3.MaterialTheme.colorScheme.background
    } else {
        grundfarbe
    }
    val linie = if (linienfarbe == Color.Unspecified) KopfgeldTheme.stifte.karo else linienfarbe

    Box(
        modifier = modifier
            .background(grund)
            .karoRaster(linie),
        content = inhalt,
    )
}
