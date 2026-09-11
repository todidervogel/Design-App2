package de.relock.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Langer Druck. Gebraucht fuer den Screen-Katalog: laut DESIGN.md 5 oeffnet
 * ein langer Druck auf "Relock" im Heute-Kopf die Liste aller Screens.
 */
fun Modifier.langerDruck(aktion: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onLongPress = { aktion() })
}

/**
 * Klickbar ohne Wellenring.
 *
 * Der Material-Ripple passt nicht zur Privatbank: dort blitzt nichts auf.
 * Ueberall, wo eine Flaeche selbst schon reagiert (Karte, Chips, Zeilen mit
 * eigener Farbe), wird stattdessen das hier benutzt.
 */
fun Modifier.leiseKlickbar(aktion: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() },
        indication = null,
        onClick = aktion,
    )
}
