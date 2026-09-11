package de.relock.app.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Langer Druck. Gebraucht fuer den Screen-Katalog: laut DESIGN.md 5 oeffnet
 * ein langer Druck auf "Relock" im Heute-Kopf die Liste aller Screens.
 */
fun Modifier.langerDruck(aktion: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onLongPress = { aktion() })
}
