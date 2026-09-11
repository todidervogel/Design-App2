package de.relock.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.relock.app.data.Zyklustyp
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme

/*
 * Alle Zeichen der App sind gezeichnet, nicht aus einem Iconsatz.
 *
 * Zwei Gruende: DESIGN.md 5 verlangt "Symbole fein", und feine Striche
 * passen zur Privatbank, waehrend gefuellte Material-Icons dagegenstehen.
 * Nebeneffekt: eine Abhaengigkeit weniger und keine Ueberraschung, wenn ein
 * Iconname in einer Version anders heisst.
 *
 * Alle Zeichen sind auf ein Quadrat normiert und benutzen dieselbe
 * Strichstaerke, damit sie nebeneinander ruhig wirken.
 */

private const val STRICH_DP = 1.5f

@Composable
private fun Zeichen(
    groesse: Dp,
    modifier: Modifier = Modifier,
    inhalt: DrawScope.(seite: Float, strich: Float) -> Unit,
) {
    Canvas(modifier.size(groesse)) {
        val seite = size.minDimension
        inhalt(seite, STRICH_DP.dp.toPx())
    }
}

// --- Bottom Navigation -----------------------------------------------------

/** Heute: ein Kalenderblatt mit markiertem Tag. */
@Composable
fun SymbolHeute(aktiv: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = navfarbe(aktiv)
    Zeichen(groesse, modifier) { s, strich ->
        val rand = s * 0.14f
        drawRoundRect(
            color = farbe,
            topLeft = Offset(rand, rand * 1.5f),
            size = Size(s - rand * 2f, s - rand * 2.5f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strich),
        )
        drawLine(farbe, Offset(rand, s * 0.40f), Offset(s - rand, s * 0.40f), strich)
        drawLine(
            farbe,
            Offset(s * 0.34f, rand * 0.6f),
            Offset(s * 0.34f, rand * 2.2f),
            strich,
            cap = StrokeCap.Round,
        )
        drawLine(
            farbe,
            Offset(s * 0.66f, rand * 0.6f),
            Offset(s * 0.66f, rand * 2.2f),
            strich,
            cap = StrokeCap.Round,
        )
        if (aktiv) {
            drawRoundRect(
                color = farbe,
                topLeft = Offset(s * 0.36f, s * 0.56f),
                size = Size(s * 0.20f, s * 0.16f),
                cornerRadius = CornerRadius(1.dp.toPx()),
            )
        }
    }
}

/** Brain: ein aufgeschlagenes Buch von vorn. */
@Composable
fun SymbolBrain(aktiv: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = navfarbe(aktiv)
    Zeichen(groesse, modifier) { s, strich ->
        val oben = s * 0.24f
        val unten = s * 0.78f
        val mitte = s * 0.5f
        val links = s * 0.14f
        val rechts = s * 0.86f

        val seiten = Path().apply {
            moveTo(mitte, oben + s * 0.06f)
            cubicTo(mitte - s * 0.10f, oben - s * 0.02f, links + s * 0.06f, oben, links, oben)
            lineTo(links, unten - s * 0.04f)
            cubicTo(
                links + s * 0.08f, unten - s * 0.04f,
                mitte - s * 0.10f, unten - s * 0.02f,
                mitte, unten,
            )
            cubicTo(
                mitte + s * 0.10f, unten - s * 0.02f,
                rechts - s * 0.08f, unten - s * 0.04f,
                rechts, unten - s * 0.04f,
            )
            lineTo(rechts, oben)
            cubicTo(rechts - s * 0.06f, oben, mitte + s * 0.10f, oben - s * 0.02f, mitte, oben + s * 0.06f)
            close()
        }
        drawPath(seiten, farbe, style = Stroke(width = strich))
        drawLine(farbe, Offset(mitte, oben + s * 0.06f), Offset(mitte, unten), strich)
        if (aktiv) drawPath(seiten, farbe.copy(alpha = 0.16f))
    }
}

/** Statistik: drei steigende Striche. */
@Composable
fun SymbolStatistik(aktiv: Boolean, modifier: Modifier = Modifier, groesse: Dp = 24.dp) {
    val farbe = navfarbe(aktiv)
    Zeichen(groesse, modifier) { s, strich ->
        val fuss = s * 0.80f
        listOf(0.26f, 0.46f, 0.64f).forEachIndexed { i, anteil ->
            val x = s * 0.26f + i * s * 0.24f
            drawLine(
                farbe,
                Offset(x, fuss),
                Offset(x, fuss - s * anteil),
                if (aktiv) strich * 2f else strich,
                cap = StrokeCap.Round,
            )
        }
        drawLine(
            farbe.copy(alpha = 0.5f),
            Offset(s * 0.16f, fuss),
            Offset(s * 0.84f, fuss),
            strich,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun navfarbe(aktiv: Boolean): Color =
    if (aktiv) Messing else RelockTheme.farben.matt

// --- Zyklustypen -----------------------------------------------------------

/** Das Zeichen zu einem Zyklustyp, DESIGN.md 6.6. */
@Composable
fun SymbolZyklustyp(
    typ: Zyklustyp,
    modifier: Modifier = Modifier,
    groesse: Dp = 22.dp,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    when (typ) {
        Zyklustyp.Hausaufgabe -> SymbolHeft(modifier, groesse, ton)
        Zyklustyp.Aufgaben -> SymbolHaken(modifier, groesse, ton)
        Zyklustyp.Lernen -> SymbolBuch(modifier, groesse, ton)
        Zyklustyp.Blurting -> SymbolStift(modifier, groesse, ton)
        Zyklustyp.LautErklaeren -> SymbolSprechblase(modifier, groesse, ton)
    }
}

/** Heft: Rechteck mit Ringbindung links. */
@Composable
fun SymbolHeft(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Elfenbein) {
    Zeichen(groesse, modifier) { s, strich ->
        drawRoundRect(
            color = farbe,
            topLeft = Offset(s * 0.22f, s * 0.14f),
            size = Size(s * 0.62f, s * 0.72f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strich),
        )
        listOf(0.28f, 0.46f, 0.64f).forEach { y ->
            drawLine(
                farbe.copy(alpha = 0.7f),
                Offset(s * 0.20f, s * y),
                Offset(s * 0.30f, s * y),
                strich,
                cap = StrokeCap.Round,
            )
        }
        listOf(0.36f, 0.52f).forEach { y ->
            drawLine(
                farbe.copy(alpha = 0.55f),
                Offset(s * 0.40f, s * y),
                Offset(s * 0.74f, s * y),
                strich,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Haken im Kreis. */
@Composable
fun SymbolHaken(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Elfenbein) {
    Zeichen(groesse, modifier) { s, strich ->
        drawCircle(
            color = farbe,
            radius = s * 0.36f,
            center = Offset(s / 2f, s / 2f),
            style = Stroke(width = strich),
        )
        drawLine(
            farbe,
            Offset(s * 0.34f, s * 0.52f),
            Offset(s * 0.46f, s * 0.64f),
            strich * 1.2f,
            cap = StrokeCap.Round,
        )
        drawLine(
            farbe,
            Offset(s * 0.46f, s * 0.64f),
            Offset(s * 0.68f, s * 0.38f),
            strich * 1.2f,
            cap = StrokeCap.Round,
        )
    }
}

/** Aufgeschlagenes Buch. */
@Composable
fun SymbolBuch(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Elfenbein) {
    Zeichen(groesse, modifier) { s, strich ->
        val mitte = s * 0.5f
        val oben = s * 0.26f
        val unten = s * 0.76f
        drawLine(farbe, Offset(mitte, oben + s * 0.04f), Offset(mitte, unten), strich)
        val links = Path().apply {
            moveTo(mitte, oben + s * 0.04f)
            lineTo(s * 0.14f, oben)
            lineTo(s * 0.14f, unten - s * 0.04f)
            lineTo(mitte, unten)
        }
        val rechts = Path().apply {
            moveTo(mitte, oben + s * 0.04f)
            lineTo(s * 0.86f, oben)
            lineTo(s * 0.86f, unten - s * 0.04f)
            lineTo(mitte, unten)
        }
        drawPath(links, farbe, style = Stroke(width = strich))
        drawPath(rechts, farbe, style = Stroke(width = strich))
    }
}

/** Stift, diagonal. */
@Composable
fun SymbolStift(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Elfenbein) {
    Zeichen(groesse, modifier) { s, strich ->
        val koerper = Path().apply {
            moveTo(s * 0.24f, s * 0.78f)
            lineTo(s * 0.66f, s * 0.18f)
            lineTo(s * 0.82f, s * 0.32f)
            lineTo(s * 0.36f, s * 0.86f)
            close()
        }
        drawPath(koerper, farbe, style = Stroke(width = strich))
        drawLine(
            farbe.copy(alpha = 0.7f),
            Offset(s * 0.32f, s * 0.62f),
            Offset(s * 0.48f, s * 0.76f),
            strich,
        )
    }
}

/** Sprechblase. */
@Composable
fun SymbolSprechblase(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Elfenbein) {
    Zeichen(groesse, modifier) { s, strich ->
        drawRoundRect(
            color = farbe,
            topLeft = Offset(s * 0.14f, s * 0.20f),
            size = Size(s * 0.72f, s * 0.46f),
            cornerRadius = CornerRadius(s * 0.14f),
            style = Stroke(width = strich),
        )
        val zipfel = Path().apply {
            moveTo(s * 0.32f, s * 0.66f)
            lineTo(s * 0.30f, s * 0.84f)
            lineTo(s * 0.48f, s * 0.66f)
        }
        drawPath(zipfel, farbe, style = Stroke(width = strich, cap = StrokeCap.Round))
    }
}

// --- Bedienung -------------------------------------------------------------

/** Zahnrad, sehr fein. */
@Composable
fun SymbolEinstellungen(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Elfenbein else farbe
    Zeichen(groesse, modifier) { s, strich ->
        val mitte = Offset(s / 2f, s / 2f)
        drawCircle(ton, radius = s * 0.16f, center = mitte, style = Stroke(width = strich))
        drawCircle(ton, radius = s * 0.34f, center = mitte, style = Stroke(width = strich))
        for (i in 0 until 8) {
            val winkel = Math.PI / 4.0 * i
            val vonX = mitte.x + (s * 0.34f) * kotlin.math.cos(winkel).toFloat()
            val vonY = mitte.y + (s * 0.34f) * kotlin.math.sin(winkel).toFloat()
            val bisX = mitte.x + (s * 0.44f) * kotlin.math.cos(winkel).toFloat()
            val bisY = mitte.y + (s * 0.44f) * kotlin.math.sin(winkel).toFloat()
            drawLine(ton, Offset(vonX, vonY), Offset(bisX, bisY), strich, cap = StrokeCap.Round)
        }
    }
}

/** Zurueck-Pfeil. */
@Composable
fun SymbolZurueck(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Elfenbein else farbe
    Zeichen(groesse, modifier) { s, strich ->
        drawLine(ton, Offset(s * 0.22f, s * 0.5f), Offset(s * 0.78f, s * 0.5f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.22f, s * 0.5f), Offset(s * 0.44f, s * 0.30f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.22f, s * 0.5f), Offset(s * 0.44f, s * 0.70f), strich, cap = StrokeCap.Round)
    }
}

/** Schliessen-Kreuz. */
@Composable
fun SymbolSchliessen(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Elfenbein else farbe
    Zeichen(groesse, modifier) { s, strich ->
        drawLine(ton, Offset(s * 0.28f, s * 0.28f), Offset(s * 0.72f, s * 0.72f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.72f, s * 0.28f), Offset(s * 0.28f, s * 0.72f), strich, cap = StrokeCap.Round)
    }
}

/** Pfeil nach rechts, fuer Zeilen, die weiterfuehren. */
@Composable
fun SymbolWeiter(modifier: Modifier = Modifier, groesse: Dp = 18.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) RelockTheme.farben.leise else farbe
    Zeichen(groesse, modifier) { s, strich ->
        drawLine(ton, Offset(s * 0.40f, s * 0.30f), Offset(s * 0.62f, s * 0.5f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.62f, s * 0.5f), Offset(s * 0.40f, s * 0.70f), strich, cap = StrokeCap.Round)
    }
}

/** Plus fuer "Neu" und "+ Zyklus". */
@Composable
fun SymbolPlus(modifier: Modifier = Modifier, groesse: Dp = 18.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    Zeichen(groesse, modifier) { s, strich ->
        drawLine(ton, Offset(s * 0.5f, s * 0.24f), Offset(s * 0.5f, s * 0.76f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.24f, s * 0.5f), Offset(s * 0.76f, s * 0.5f), strich, cap = StrokeCap.Round)
    }
}

/** Ziehgriff zum Umsortieren: zwei kurze Striche. */
@Composable
fun SymbolZiehgriff(modifier: Modifier = Modifier, groesse: Dp = 22.dp) {
    val ton = RelockTheme.farben.leise
    Zeichen(groesse, modifier) { s, strich ->
        listOf(0.40f, 0.60f).forEach { y ->
            drawLine(
                ton,
                Offset(s * 0.28f, s * y),
                Offset(s * 0.72f, s * y),
                strich,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Schloss, fuer Sperrzustaende und die Box. */
@Composable
fun SymbolSchloss(
    modifier: Modifier = Modifier,
    groesse: Dp = 24.dp,
    farbe: Color = Color.Unspecified,
    offen: Boolean = false,
) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    Zeichen(groesse, modifier) { s, strich ->
        val buegelMitte = if (offen) s * 0.62f else s * 0.5f
        drawArc(
            color = ton,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(buegelMitte - s * 0.17f, s * 0.20f),
            size = Size(s * 0.34f, s * 0.34f),
            style = Stroke(width = strich * 1.3f, cap = StrokeCap.Round),
        )
        drawRoundRect(
            color = ton,
            topLeft = Offset(s * 0.26f, s * 0.44f),
            size = Size(s * 0.48f, s * 0.36f),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Stroke(width = strich * 1.3f),
        )
    }
}

/** Die Box, in die das Handy gelegt wird: vier Eckwinkel. */
@Composable
fun SymbolBox(modifier: Modifier = Modifier, groesse: Dp = 48.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    Zeichen(groesse, modifier) { s, strich ->
        val dick = strich * 1.6f
        val rand = s * 0.16f
        val laenge = s * 0.20f
        val rechts = s - rand
        val unten = s - rand
        drawLine(ton, Offset(rand, rand + laenge), Offset(rand, rand), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rand, rand), Offset(rand + laenge, rand), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rechts - laenge, rand), Offset(rechts, rand), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rechts, rand), Offset(rechts, rand + laenge), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rechts, unten - laenge), Offset(rechts, unten), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rechts, unten), Offset(rechts - laenge, unten), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rand + laenge, unten), Offset(rand, unten), dick, cap = StrokeCap.Square)
        drawLine(ton, Offset(rand, unten), Offset(rand, unten - laenge), dick, cap = StrokeCap.Square)
    }
}

/** Kamera-Ausloeser und Foto. */
@Composable
fun SymbolKamera(modifier: Modifier = Modifier, groesse: Dp = 22.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Elfenbein else farbe
    Zeichen(groesse, modifier) { s, strich ->
        drawRoundRect(
            color = ton,
            topLeft = Offset(s * 0.12f, s * 0.28f),
            size = Size(s * 0.76f, s * 0.48f),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Stroke(width = strich),
        )
        drawCircle(ton, radius = s * 0.14f, center = Offset(s * 0.5f, s * 0.52f), style = Stroke(width = strich))
        drawLine(ton, Offset(s * 0.34f, s * 0.28f), Offset(s * 0.40f, s * 0.20f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.40f, s * 0.20f), Offset(s * 0.60f, s * 0.20f), strich, cap = StrokeCap.Round)
        drawLine(ton, Offset(s * 0.60f, s * 0.20f), Offset(s * 0.66f, s * 0.28f), strich, cap = StrokeCap.Round)
    }
}

/** QR-Rahmen fuer die Serverkopplung. */
@Composable
fun SymbolQr(modifier: Modifier = Modifier, groesse: Dp = 48.dp, farbe: Color = Color.Unspecified) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    Zeichen(groesse, modifier) { s, strich ->
        listOf(
            Offset(s * 0.18f, s * 0.18f),
            Offset(s * 0.58f, s * 0.18f),
            Offset(s * 0.18f, s * 0.58f),
        ).forEach { ecke ->
            drawRoundRect(
                color = ton,
                topLeft = ecke,
                size = Size(s * 0.24f, s * 0.24f),
                cornerRadius = CornerRadius(2.dp.toPx()),
                style = Stroke(width = strich),
            )
        }
        drawRoundRect(
            color = ton.copy(alpha = 0.5f),
            topLeft = Offset(s * 0.58f, s * 0.58f),
            size = Size(s * 0.24f, s * 0.24f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strich),
        )
    }
}
