package de.kopfgeld.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Wischen zum Loeschen, DESIGN.md 7.4.
 *
 * Bewusst selbst gebaut statt SwipeToDismissBox: der rote Hintergrund soll
 * genau die Korrekturfarbe sein und der Ausloeseweg kurz, damit man auf einem
 * schmalen Bildschirm nicht quer ueber die ganze Breite ziehen muss.
 */
@Composable
fun WischZumLoeschen(
    beiLoeschen: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
    inhalt: @Composable () -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    val dichte = LocalDensity.current
    val bereich = rememberCoroutineScope()
    val versatz = remember { Animatable(0f) }
    val schwelle = with(dichte) { 110.dp.toPx() }

    Box(modifier.fillMaxWidth()) {
        if (abs(versatz.value) > 1f) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(stifte.korrekturHauch)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text = "Löschen",
                    style = MaterialTheme.typography.labelLarge,
                    color = stifte.korrektur,
                )
            }
        }

        Box(
            Modifier
                .offset { androidx.compose.ui.unit.IntOffset(versatz.value.toInt(), 0) }
                .then(
                    if (aktiv) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    bereich.launch {
                                        if (abs(versatz.value) > schwelle) {
                                            versatz.snapTo(0f)
                                            beiLoeschen()
                                        } else {
                                            versatz.animateTo(0f)
                                        }
                                    }
                                },
                                onDragCancel = {
                                    bereich.launch { versatz.animateTo(0f) }
                                },
                            ) { _, verschiebung ->
                                bereich.launch {
                                    val neu = (versatz.value + verschiebung)
                                        .coerceIn(-size.width.toFloat(), 0f)
                                    versatz.snapTo(neu)
                                }
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            inhalt()
        }
    }
}
