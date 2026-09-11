package de.relock.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.relock.app.data.Scan
import de.relock.app.data.Scanstand
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Nacht
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

/**
 * Vorschau einer gescannten Seite, DESIGN.md 3.
 * Seitennummer und Status-Punkt. In Version 1 gibt es keine echten Fotos,
 * der Platzhalter deutet Zeilen an.
 */
@Composable
fun ScanThumb(
    scan: Scan,
    modifier: Modifier = Modifier,
    breite: Dp = 62.dp,
    beiKlick: (() -> Unit)? = null,
) {
    val farben = RelockTheme.farben
    val statusfarbe = when (scan.stand) {
        Scanstand.Wartet -> farben.leise
        Scanstand.Laeuft -> Messing
        Scanstand.Fertig -> Richtig
        Scanstand.Unscharf -> Signal
    }

    Column(
        modifier = modifier
            .width(breite)
            .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .width(breite)
                .height(breite * 1.38f)
                .background(farben.flaecheHoch, Radius.Chip)
                .border(
                    1.dp,
                    if (scan.stand == Scanstand.Unscharf) Signal else farben.linie,
                    Radius.Chip,
                )
                .seitenPlatzhalter(farben.leise),
        )
        Luft(5.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Statuspunkt(farbe = statusfarbe, groesse = 5.dp)
            LuftBreit(5.dp)
            Text(
                text = "S. ${scan.seite}",
                style = MaterialTheme.typography.labelSmall,
                color = farben.matt,
            )
        }
    }
}

/**
 * Zeichnet eine angedeutete beschriebene Seite. Steht ueberall dort, wo
 * spaeter ein echtes Foto liegt.
 */
fun Modifier.seitenPlatzhalter(
    schriftfarbe: Color,
    dicht: Boolean = false,
): Modifier = this.drawBehind {
    val zeilen = if (dicht) 9 else 6
    val anteile = listOf(0.78f, 0.62f, 0.84f, 0.44f, 0.70f, 0.56f, 0.80f, 0.38f, 0.66f)
    repeat(zeilen) { index ->
        val y = size.height * (0.16f + index * (0.68f / zeilen))
        val anteil = anteile[index % anteile.size]
        drawLine(
            color = schriftfarbe.copy(alpha = 0.45f),
            start = Offset(size.width * 0.13f, y),
            end = Offset(size.width * (0.13f + anteil * 0.74f), y),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Kamera-Platzhalter: dunkles Bild mit angedeuteter Tischkante, DESIGN.md 6.7.
 * Version 1 hat keine Kamera.
 */
@Composable
fun KameraPlatzhalter(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF090C12))
            .drawBehind {
                // Tischflaeche, etwas heller als der Raum dahinter.
                drawRect(
                    color = Color(0xFF131820),
                    topLeft = Offset(0f, size.height * 0.58f),
                    size = Size(size.width, size.height * 0.42f),
                )
                // Tischkante als flache Diagonale.
                drawLine(
                    color = Color(0xFF1F2836),
                    start = Offset(0f, size.height * 0.60f),
                    end = Offset(size.width, size.height * 0.55f),
                    strokeWidth = 2.dp.toPx(),
                )
            },
    )
}

/**
 * Der gezeichnete Box-Rahmen ueber dem Kamerabild, DESIGN.md 3 und 6.7.
 *
 * Vier Eckwinkel in Messing, gestrichelte Kanten dazwischen, Ziehpunkte an
 * den Ecken. Verschieben durch Ziehen in der Mitte.
 *
 * @param geschlossen 0 bis 1. Bei 1 haben sich die Eckwinkel zu einer
 *   durchgehenden Linie geschlossen - der Moment, in dem der Countdown
 *   bei null ankommt (DESIGN.md 2.5).
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
    val linie = Messing.copy(alpha = deckkraft)
    val zu = geschlossen.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .offset(x = versatzX, y = versatzY)
            .size(width = breite, height = hoehe)
            .then(
                if (ziehbar) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures { _, verschiebung ->
                            with(dichte) {
                                beiVerschieben(verschiebung.x.toDp(), verschiebung.y.toDp())
                            }
                        }
                    }
                } else {
                    Modifier
                },
            )
            .drawBehind {
                val dick = 2.5.dp.toPx()
                val duenn = 1.dp.toPx()
                val winkelX = size.width * (0.20f + 0.30f * zu)
                val winkelY = size.height * (0.20f + 0.30f * zu)

                if (zu < 1f) {
                    val strichel = PathEffect.dashPathEffect(
                        floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                        0f,
                    )
                    val kante = linie.copy(alpha = deckkraft * (1f - zu) * 0.55f)
                    drawLine(kante, Offset(winkelX, 0f), Offset(size.width - winkelX, 0f), duenn, pathEffect = strichel)
                    drawLine(kante, Offset(winkelX, size.height), Offset(size.width - winkelX, size.height), duenn, pathEffect = strichel)
                    drawLine(kante, Offset(0f, winkelY), Offset(0f, size.height - winkelY), duenn, pathEffect = strichel)
                    drawLine(kante, Offset(size.width, winkelY), Offset(size.width, size.height - winkelY), duenn, pathEffect = strichel)
                }

                // Vier Eckwinkel
                drawLine(linie, Offset(0f, winkelY), Offset.Zero, dick, StrokeCap.Square)
                drawLine(linie, Offset.Zero, Offset(winkelX, 0f), dick, StrokeCap.Square)
                drawLine(linie, Offset(size.width - winkelX, 0f), Offset(size.width, 0f), dick, StrokeCap.Square)
                drawLine(linie, Offset(size.width, 0f), Offset(size.width, winkelY), dick, StrokeCap.Square)
                drawLine(linie, Offset(size.width, size.height - winkelY), Offset(size.width, size.height), dick, StrokeCap.Square)
                drawLine(linie, Offset(size.width, size.height), Offset(size.width - winkelX, size.height), dick, StrokeCap.Square)
                drawLine(linie, Offset(winkelX, size.height), Offset(0f, size.height), dick, StrokeCap.Square)
                drawLine(linie, Offset(0f, size.height), Offset(0f, size.height - winkelY), dick, StrokeCap.Square)
            },
    ) {
        if (ziehbar && zu == 0f) {
            Ziehpunkt(Alignment.TopStart) { dx, dy -> beiGroesse(-dx, -dy) }
            Ziehpunkt(Alignment.TopEnd) { dx, dy -> beiGroesse(dx, -dy) }
            Ziehpunkt(Alignment.BottomStart) { dx, dy -> beiGroesse(-dx, dy) }
            Ziehpunkt(Alignment.BottomEnd) { dx, dy -> beiGroesse(dx, dy) }
        }
    }
}

@Composable
private fun BoxScope.Ziehpunkt(
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
        Box(Modifier.size(10.dp).background(Messing, CircleShape))
    }
}

/** Der runde Ausloeser: Elfenbein mit Messing-Ring, DESIGN.md 6.11. */
@Composable
fun Ausloeser(
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(70.dp)
            .border(2.dp, Messing, CircleShape)
            .clickable(onClick = beiKlick),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(54.dp).background(Elfenbein, CircleShape))
    }
}

/**
 * Ein grosser Kreis-Countdown in Messing, DESIGN.md 6.16.
 * Feiner Ring, 2 dp, laeuft im Uhrzeigersinn leer.
 */
@Composable
fun Kreiszaehler(
    anteil: Float,
    modifier: Modifier = Modifier,
    groesse: Dp = 220.dp,
    inhalt: @Composable BoxScope.() -> Unit,
) {
    val farben = RelockTheme.farben
    Box(
        modifier
            .size(groesse)
            .drawBehind {
                val staerke = 2.dp.toPx()
                val einzug = staerke / 2f
                val kante = Size(size.width - staerke, size.height - staerke)
                drawArc(
                    color = farben.linie,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(einzug, einzug),
                    size = kante,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = staerke,
                        cap = StrokeCap.Round,
                    ),
                )
                drawArc(
                    color = Messing,
                    startAngle = -90f,
                    sweepAngle = 360f * anteil.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(einzug, einzug),
                    size = kante,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = staerke,
                        cap = StrokeCap.Round,
                    ),
                )
            },
        contentAlignment = Alignment.Center,
        content = inhalt,
    )
}

/**
 * Die drei Punkte, die waehrend der Flaechensuche pulsieren.
 */
@Composable
fun Suchpunkte(modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            val leuchten = remember { Animatable(0.25f) }
            LaunchedEffect(index) {
                leuchten.animateTo(
                    targetValue = 0.9f,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = tween(700, delayMillis = index * 160),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
                    ),
                )
            }
            Box(
                Modifier
                    .size(6.dp)
                    .background(Messing.copy(alpha = leuchten.value), CircleShape),
            )
        }
    }
}

/**
 * Ersatzgriff fuer Uebergaenge, die ohne Kamera und Sensoren nicht
 * erreichbar sind. Bewusst leise und als Behelf erkennbar - verschwindet,
 * sobald die echte Erkennung da ist.
 */
@Composable
fun Behelf(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    zentriert: Boolean = false,
) {
    val farben = RelockTheme.farben
    Row(
        modifier
            .clickable(onClick = beiKlick)
            .padding(horizontal = Mass.Rand, vertical = 10.dp),
        horizontalArrangement = if (zentriert) Arrangement.Center else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 12.dp, height = 1.dp)
                .background(farben.leise),
        )
        LuftBreit(8.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = farben.leise,
        )
    }
}

/** Dunkler Vollbild-Hintergrund fuer die Box-Zustaende. */
@Composable
fun Schwarzflaeche(modifier: Modifier = Modifier, inhalt: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black),
        content = inhalt,
    )
}

/** Nacht-Vollbild, fuer Sperrbildschirm, Pause und Nachsperre. */
@Composable
fun Nachtflaeche(modifier: Modifier = Modifier, inhalt: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(Nacht),
        content = inhalt,
    )
}
