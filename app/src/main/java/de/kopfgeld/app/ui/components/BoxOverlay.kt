package de.kopfgeld.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.Kreide

/**
 * Der gezeichnete Box-Rahmen ueber dem Kamera-Platzhalter, DESIGN.md 5 und 7.7.
 *
 * Vier kraeftige Eckwinkel in Kreide, gestrichelte Kanten dazwischen,
 * Ziehpunkte an den Ecken. Verschieben durch Ziehen in der Mitte.
 *
 * @param geschlossen 0 bis 1. Bei 0 sieht man Eckwinkel, bei 1 hat sich der
 *   Rahmen zu einer durchgehenden Linie geschlossen - der Moment, in dem der
 *   Countdown 5-4-3-2-1 bei null ankommt (DESIGN.md 4.4).
 */
@Composable
fun BoxOverlay(
    breite: Dp,
    hoehe: Dp,
    versatzX: Dp,
    versatzY: Dp,
    modifier: Modifier = Modifier,
    halbdurchsichtig: Boolean = false,
    ziehbar: Boolean = true,
    geschlossen: Float = 0f,
    beiVerschieben: (Dp, Dp) -> Unit = { _, _ -> },
    beiGroesse: (Dp, Dp) -> Unit = { _, _ -> },
) {
    val dichte = LocalDensity.current
    val deckkraft = if (halbdurchsichtig) 0.45f else 1f
    val linie = Kreide.copy(alpha = deckkraft)
    val anteilGeschlossen = geschlossen.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .offset(x = versatzX, y = versatzY)
            .size(width = breite, height = hoehe)
            .then(
                if (ziehbar) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures { _, verschiebung ->
                            with(dichte) {
                                beiVerschieben(
                                    verschiebung.x.toDp(),
                                    verschiebung.y.toDp(),
                                )
                            }
                        }
                    }
                } else {
                    Modifier
                },
            )
            .drawBehind {
                val staerke = 3.dp.toPx()
                val duenn = 1.5.dp.toPx()

                // Eckwinkel: bei geschlossen = 1 treffen sie sich in der Mitte
                // jeder Kante und bilden eine durchgehende Linie.
                val winkelX = size.width * (0.22f + 0.28f * anteilGeschlossen)
                val winkelY = size.height * (0.22f + 0.28f * anteilGeschlossen)

                // Gestrichelte Kanten dazwischen, sie verschwinden beim Schliessen.
                if (anteilGeschlossen < 1f) {
                    val strichel = PathEffect.dashPathEffect(
                        floatArrayOf(6.dp.toPx(), 6.dp.toPx()),
                        0f,
                    )
                    val kantenfarbe = linie.copy(alpha = deckkraft * (1f - anteilGeschlossen) * 0.6f)
                    drawLine(
                        kantenfarbe,
                        Offset(winkelX, 0f),
                        Offset(size.width - winkelX, 0f),
                        duenn,
                        pathEffect = strichel,
                    )
                    drawLine(
                        kantenfarbe,
                        Offset(winkelX, size.height),
                        Offset(size.width - winkelX, size.height),
                        duenn,
                        pathEffect = strichel,
                    )
                    drawLine(
                        kantenfarbe,
                        Offset(0f, winkelY),
                        Offset(0f, size.height - winkelY),
                        duenn,
                        pathEffect = strichel,
                    )
                    drawLine(
                        kantenfarbe,
                        Offset(size.width, winkelY),
                        Offset(size.width, size.height - winkelY),
                        duenn,
                        pathEffect = strichel,
                    )
                }

                // Oben links
                drawLine(linie, Offset(0f, winkelY), Offset(0f, 0f), staerke, StrokeCap.Square)
                drawLine(linie, Offset(0f, 0f), Offset(winkelX, 0f), staerke, StrokeCap.Square)
                // Oben rechts
                drawLine(
                    linie,
                    Offset(size.width - winkelX, 0f),
                    Offset(size.width, 0f),
                    staerke,
                    StrokeCap.Square,
                )
                drawLine(
                    linie,
                    Offset(size.width, 0f),
                    Offset(size.width, winkelY),
                    staerke,
                    StrokeCap.Square,
                )
                // Unten rechts
                drawLine(
                    linie,
                    Offset(size.width, size.height - winkelY),
                    Offset(size.width, size.height),
                    staerke,
                    StrokeCap.Square,
                )
                drawLine(
                    linie,
                    Offset(size.width, size.height),
                    Offset(size.width - winkelX, size.height),
                    staerke,
                    StrokeCap.Square,
                )
                // Unten links
                drawLine(
                    linie,
                    Offset(winkelX, size.height),
                    Offset(0f, size.height),
                    staerke,
                    StrokeCap.Square,
                )
                drawLine(
                    linie,
                    Offset(0f, size.height),
                    Offset(0f, size.height - winkelY),
                    staerke,
                    StrokeCap.Square,
                )
            },
    ) {
        if (ziehbar && anteilGeschlossen == 0f) {
            Ziehpunkt(Alignment.TopStart) { dx, dy -> beiGroesse(-dx, -dy) }
            Ziehpunkt(Alignment.TopEnd) { dx, dy -> beiGroesse(dx, -dy) }
            Ziehpunkt(Alignment.BottomStart) { dx, dy -> beiGroesse(-dx, dy) }
            Ziehpunkt(Alignment.BottomEnd) { dx, dy -> beiGroesse(dx, dy) }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.Ziehpunkt(
    ausrichtung: Alignment,
    beiZug: (Dp, Dp) -> Unit,
) {
    val dichte = LocalDensity.current
    Box(
        Modifier
            .align(ausrichtung)
            .size(28.dp)
            .pointerInput(ausrichtung) {
                detectDragGestures { _, verschiebung ->
                    with(dichte) { beiZug(verschiebung.x.toDp(), verschiebung.y.toDp()) }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(12.dp)
                .background(Kreide, androidx.compose.foundation.shape.CircleShape),
        )
    }
}

/**
 * Kamera-Platzhalter: dunkle Flaeche mit angedeuteter Tischkante.
 * In Phase 1 gibt es kein Kamerabild.
 */
@Composable
fun KameraPlatzhalter(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF14100D))
            .drawBehind {
                // Tischkante: eine flache Diagonale im unteren Drittel.
                drawLine(
                    Color(0xFF3A3129),
                    Offset(0f, size.height * 0.62f),
                    Offset(size.width, size.height * 0.54f),
                    2.dp.toPx(),
                )
                // Tischflaeche etwas heller.
                drawRect(
                    color = Color(0xFF221C17),
                    topLeft = Offset(0f, size.height * 0.58f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.42f),
                )
                drawLine(
                    Color(0xFF3A3129),
                    Offset(0f, size.height * 0.62f),
                    Offset(size.width, size.height * 0.54f),
                    2.dp.toPx(),
                )
            },
    )
}
