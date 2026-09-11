package de.kopfgeld.app.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Langer Druck. Wird fuer den Screen-Katalog gebraucht: laut DESIGN.md 6
 * oeffnet ein langer Druck auf den Titel "Heute" die Liste aller Screens.
 */
fun Modifier.langerDruck(aktion: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onLongPress = { aktion() })
}
