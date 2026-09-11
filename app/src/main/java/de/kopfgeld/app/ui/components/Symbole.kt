package de.kopfgeld.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme

/*
 * Eigene Zeichen statt eines grossen Iconsatzes.
 *
 * Zwei Gruende: der Auftrag sagt "Raster und drei Stiftfarben reichen", und
 * selbst gezeichnete Striche passen besser zu Heft und Tafel als gefuellte
 * Material-Icons. Alles hier ist reine Geometrie, ein bis zwei Striche.
 */

/** Heute: ein Kalenderblatt, ein Tag markiert. */
@Composable
fun SymbolHeute(gewaehlt: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = symbolfarbe(gewaehlt)
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = if (gewaehlt) 2.dp.toPx() else 1.5.dp.toPx()
        val rand = s * 0.12f
        drawRect(
            color = farbe,
            topLeft = Offset(rand, rand * 1.6f),
            size = Size(s - rand * 2f, s - rand * 2.6f),
            style = Stroke(width = strich),
        )
        drawLine(
            farbe,
            Offset(rand, s * 0.40f),
            Offset(s - rand, s * 0.40f),
            strich,
        )
        if (gewaehlt) {
            drawRect(
                color = farbe,
                topLeft = Offset(s * 0.34f, s * 0.54f),
                size = Size(s * 0.22f, s * 0.20f),
            )
        }
    }
}

/** Lernen: eine Fuellerspitze. */
@Composable
fun SymbolLernen(gewaehlt: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = symbolfarbe(gewaehlt)
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = if (gewaehlt) 2.dp.toPx() else 1.5.dp.toPx()
        val feder = Path().apply {
            moveTo(s * 0.22f, s * 0.80f)
            lineTo(s * 0.66f, s * 0.16f)
            lineTo(s * 0.84f, s * 0.34f)
            lineTo(s * 0.34f, s * 0.86f)
            close()
        }
        drawPath(feder, farbe, style = Stroke(width = strich))
        drawLine(
            farbe,
            Offset(s * 0.30f, s * 0.60f),
            Offset(s * 0.50f, s * 0.78f),
            strich,
        )
        if (gewaehlt) {
            drawPath(feder, farbe.copy(alpha = 0.18f))
        }
    }
}

/** Brain: das Karoraster selbst. */
@Composable
fun SymbolBrain(gewaehlt: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = symbolfarbe(gewaehlt)
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = if (gewaehlt) 2.dp.toPx() else 1.5.dp.toPx()
        val rand = s * 0.14f
        val innen = s - rand * 2f
        drawRect(
            color = farbe,
            topLeft = Offset(rand, rand),
            size = Size(innen, innen),
            style = Stroke(width = strich),
        )
        val duenn = 1.dp.toPx()
        for (i in 1..2) {
            val v = rand + innen * i / 3f
            drawLine(farbe, Offset(v, rand), Offset(v, rand + innen), duenn)
            drawLine(farbe, Offset(rand, v), Offset(rand + innen, v), duenn)
        }
        if (gewaehlt) {
            drawRect(
                color = farbe,
                topLeft = Offset(rand + innen / 3f, rand + innen / 3f),
                size = Size(innen / 3f, innen / 3f),
            )
        }
    }
}

/** Fortschritt: drei steigende Balken. */
@Composable
fun SymbolFortschritt(gewaehlt: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = symbolfarbe(gewaehlt)
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = if (gewaehlt) 2.dp.toPx() else 1.5.dp.toPx()
        val breite = s * 0.16f
        val fuss = s * 0.84f
        val hoehen = listOf(0.30f, 0.50f, 0.70f)
        hoehen.forEachIndexed { i, anteil ->
            val x = s * 0.20f + i * (breite + s * 0.10f)
            val oben = fuss - s * anteil
            if (gewaehlt) {
                drawRect(farbe, Offset(x, oben), Size(breite, fuss - oben))
            } else {
                drawRect(
                    farbe,
                    Offset(x, oben),
                    Size(breite, fuss - oben),
                    style = Stroke(width = strich),
                )
            }
        }
    }
}

/** Mond fuer die Schlafenszeit. Gezeichnet, nicht als Emoji. */
@Composable
fun SymbolMond(modifier: Modifier = Modifier, groesse: Dp = 18.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) KopfgeldTheme.stifte.blei else farbe
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val voll = Path().apply {
            addOval(Rect(Offset(s * 0.10f, s * 0.10f), Size(s * 0.80f, s * 0.80f)))
        }
        val biss = Path().apply {
            addOval(Rect(Offset(s * 0.34f, s * 0.02f), Size(s * 0.78f, s * 0.78f)))
        }
        val sichel = Path().apply {
            op(voll, biss, PathOperation.Difference)
        }
        drawPath(sichel, ton)
    }
}

/** Ziehgriff zum Umsortieren: drei kurze Striche. */
@Composable
fun SymbolZiehgriff(modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = KopfgeldTheme.stifte.blei
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = 1.5.dp.toPx()
        listOf(0.34f, 0.50f, 0.66f).forEach { y ->
            drawLine(
                farbe,
                Offset(s * 0.22f, s * y),
                Offset(s * 0.78f, s * y),
                strich,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Box-Symbol fuer "Leg dein Handy in die Box": vier Eckwinkel. */
@Composable
fun SymbolBox(modifier: Modifier = Modifier, groesse: Dp = 48.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) MaterialTheme.colorScheme.onSurface else farbe
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = 2.5.dp.toPx()
        val rand = s * 0.14f
        val laenge = s * 0.22f
        val rechts = s - rand
        val unten = s - rand
        // oben links
        drawLine(ton, Offset(rand, rand + laenge), Offset(rand, rand), strich, StrokeCap.Square)
        drawLine(ton, Offset(rand, rand), Offset(rand + laenge, rand), strich, StrokeCap.Square)
        // oben rechts
        drawLine(ton, Offset(rechts - laenge, rand), Offset(rechts, rand), strich, StrokeCap.Square)
        drawLine(ton, Offset(rechts, rand), Offset(rechts, rand + laenge), strich, StrokeCap.Square)
        // unten rechts
        drawLine(ton, Offset(rechts, unten - laenge), Offset(rechts, unten), strich, StrokeCap.Square)
        drawLine(ton, Offset(rechts, unten), Offset(rechts - laenge, unten), strich, StrokeCap.Square)
        // unten links
        drawLine(ton, Offset(rand + laenge, unten), Offset(rand, unten), strich, StrokeCap.Square)
        drawLine(ton, Offset(rand, unten), Offset(rand, unten - laenge), strich, StrokeCap.Square)
    }
}

/** Kopfhoerer fuer Podcasts. Zwei Muscheln, ein Buegel. */
@Composable
fun SymbolPodcast(modifier: Modifier = Modifier, groesse: Dp = 20.dp) {
    val farbe = KopfgeldTheme.stifte.blei
    Canvas(modifier.size(groesse)) {
        val s = size.minDimension
        val strich = 1.5.dp.toPx()
        drawArc(
            color = farbe,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(s * 0.14f, s * 0.20f),
            size = Size(s * 0.72f, s * 0.60f),
            style = Stroke(width = strich),
        )
        drawRect(farbe, Offset(s * 0.12f, s * 0.50f), Size(s * 0.14f, s * 0.30f))
        drawRect(farbe, Offset(s * 0.74f, s * 0.50f), Size(s * 0.14f, s * 0.30f))
    }
}

@Composable
private fun symbolfarbe(gewaehlt: Boolean): Color =
    if (gewaehlt) KopfgeldTheme.stifte.tinte else KopfgeldTheme.stifte.blei
