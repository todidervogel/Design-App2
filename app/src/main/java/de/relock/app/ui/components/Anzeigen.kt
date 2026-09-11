package de.relock.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.relock.app.data.Kartenstufe
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal
import de.relock.app.ui.theme.materialFuer

/**
 * Das Tagesziel als Ring aus zwei Segmenten, DESIGN.md 3.
 *
 * Aussen die Hausaufgaben in Messing, innen die Lernminuten in Elfenbein.
 * Zwei getrennte Boegen statt eines geteilten Rings, damit man beide
 * gleichzeitig lesen kann.
 */
@Composable
fun GoalRing(
    hausaufgabenErledigt: Int,
    hausaufgabenGesamt: Int,
    minutenErreicht: Int,
    minutenGesamt: Int,
    modifier: Modifier = Modifier,
    groesse: Dp = 72.dp,
) {
    val farben = RelockTheme.farben
    val anteilHa = if (hausaufgabenGesamt <= 0) {
        0f
    } else {
        hausaufgabenErledigt.toFloat() / hausaufgabenGesamt.toFloat()
    }
    val anteilMin = if (minutenGesamt <= 0) {
        0f
    } else {
        minutenErreicht.toFloat() / minutenGesamt.toFloat()
    }

    Canvas(modifier.size(groesse)) {
        val staerke = 5.dp.toPx()
        val abstand = 3.dp.toPx()

        fun bogen(einzug: Float, anteil: Float, farbe: Color) {
            val versatz = einzug + staerke / 2f
            val kante = Size(size.width - versatz * 2f, size.height - versatz * 2f)
            drawArc(
                color = farbe.copy(alpha = 0.18f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(versatz, versatz),
                size = kante,
                style = Stroke(width = staerke, cap = StrokeCap.Round),
            )
            if (anteil > 0f) {
                drawArc(
                    color = farbe,
                    startAngle = -90f,
                    sweepAngle = 360f * anteil.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(versatz, versatz),
                    size = kante,
                    style = Stroke(width = staerke, cap = StrokeCap.Round),
                )
            }
        }

        bogen(0f, anteilHa, Messing)
        bogen(staerke + abstand, anteilMin, Elfenbein)
    }
}

/**
 * Fortschritt als Reihe feiner Striche, ein Strich pro Minute, DESIGN.md 3.
 *
 * Bei langen Phasen wird zusammengefasst, damit die Striche nicht zu Brei
 * werden - dann steht ein Strich fuer mehrere Minuten.
 */
@Composable
fun MinuteTrack(
    vergangen: Int,
    gesamt: Int,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
    hoehe: Dp = 14.dp,
) {
    val ton = if (farbe == Color.Unspecified) RelockTheme.farben.matt else farbe
    val striche = gesamt.coerceIn(1, 40)
    val proStrich = gesamt.toFloat() / striche.toFloat()
    val voll = if (proStrich <= 0f) 0 else (vergangen / proStrich).toInt()

    Canvas(
        modifier
            .fillMaxWidth()
            .height(hoehe),
    ) {
        if (striche == 0) return@Canvas
        val luecke = size.width / striche
        val breite = (luecke * 0.34f).coerceAtLeast(1f)
        repeat(striche) { index ->
            val x = luecke * index + luecke / 2f
            drawLine(
                color = if (index < voll) ton else ton.copy(alpha = 0.22f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = breite,
                cap = StrokeCap.Round,
            )
        }
    }
}

/**
 * Waagerechter Balken mit Beschriftung links und Wert rechts, DESIGN.md 3.
 */
@Composable
fun StatBar(
    beschriftung: String,
    wert: String,
    anteil: Float,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
    zweiterAnteil: Float? = null,
    zweiteFarbe: Color = Color.Unspecified,
) {
    val farben = RelockTheme.farben
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    val ton2 = if (zweiteFarbe == Color.Unspecified) Signal.copy(alpha = 0.6f) else zweiteFarbe

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = beschriftung,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = wert,
                style = MaterialTheme.typography.bodyMedium,
                color = Elfenbein,
            )
        }
        Luft(6.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (zweiterAnteil == null) 6.dp else 14.dp)
                .drawBehind {
                    val radius = CornerRadius(3.dp.toPx())
                    val balkenhoehe = if (zweiterAnteil == null) size.height else size.height / 2.6f
                    val luecke = if (zweiterAnteil == null) 0f else size.height - balkenhoehe

                    drawRoundRect(
                        color = ton.copy(alpha = 0.16f),
                        size = Size(size.width, balkenhoehe),
                        cornerRadius = radius,
                    )
                    drawRoundRect(
                        color = ton,
                        size = Size(size.width * anteil.coerceIn(0f, 1f), balkenhoehe),
                        cornerRadius = radius,
                    )

                    if (zweiterAnteil != null) {
                        drawRoundRect(
                            color = ton2.copy(alpha = 0.16f),
                            topLeft = Offset(0f, luecke),
                            size = Size(size.width, balkenhoehe),
                            cornerRadius = radius,
                        )
                        drawRoundRect(
                            color = ton2,
                            topLeft = Offset(0f, luecke),
                            size = Size(size.width * zweiterAnteil.coerceIn(0f, 1f), balkenhoehe),
                            cornerRadius = radius,
                        )
                    }
                },
        )
    }
}

/**
 * Fortschritt zur naechsten Kartenstufe, DESIGN.md 3.
 * Rechts ein kleines Rechteck im Material der naechsten Stufe.
 */
@Composable
fun TierProgress(
    text: String,
    anteil: Float,
    naechste: Kartenstufe?,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Elfenbein,
            )
            Luft(10.dp)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(farben.linie, Radius.Chip),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(anteil.coerceIn(0f, 1f))
                        .height(4.dp)
                        .background(Messing, Radius.Chip),
                )
            }
        }

        if (naechste != null) {
            LuftBreit(16.dp)
            val material = materialFuer(naechste)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(width = 40.dp, height = 26.dp)
                        .background(
                            Brush.linearGradient(material.verlauf),
                            Radius.Chip,
                        )
                        .border(
                            material.randBreite,
                            if (material.rand.size == 1) {
                                material.rand.first()
                            } else {
                                material.rand[1]
                            },
                            Radius.Chip,
                        ),
                )
                Luft(4.dp)
                Text(
                    text = naechste.anzeige,
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
        }
    }
}
