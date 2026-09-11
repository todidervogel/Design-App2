package de.kopfgeld.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/*
 * Radien nach Hierarchie, DESIGN.md 4.3:
 *   Chips / Inputs  6 dp
 *   Buttons        12 dp
 *   Sheets oben    20 dp
 *   Listenzeilen    0
 */
object Radius {
    val Chip = RoundedCornerShape(6.dp)
    val Eingabe = RoundedCornerShape(6.dp)
    val Button = RoundedCornerShape(12.dp)
    val Flaeche = RoundedCornerShape(12.dp)
    val Sheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val Zeile = RoundedCornerShape(0.dp)
}

val KopfgeldShapes = Shapes(
    extraSmall = Radius.Chip,
    small = Radius.Chip,
    medium = Radius.Button,
    large = Radius.Flaeche,
    extraLarge = RoundedCornerShape(20.dp),
)
