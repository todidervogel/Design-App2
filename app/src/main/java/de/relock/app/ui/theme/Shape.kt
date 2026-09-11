package de.relock.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radien, DESIGN.md 2.4:
 *   Buttons 14 dp, Eingaben 12 dp, Sheets 28 dp oben, Karte 18 dp,
 *   Listenzeilen 0.
 */
object Radius {
    val Button = RoundedCornerShape(14.dp)
    val Eingabe = RoundedCornerShape(12.dp)
    val Chip = RoundedCornerShape(12.dp)
    val Flaeche = RoundedCornerShape(16.dp)
    val Karte = RoundedCornerShape(18.dp)
    val Sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Zeile = RoundedCornerShape(0.dp)
}

val RelockShapes = Shapes(
    extraSmall = Radius.Eingabe,
    small = Radius.Eingabe,
    medium = Radius.Button,
    large = Radius.Flaeche,
    extraLarge = Radius.Karte,
)
