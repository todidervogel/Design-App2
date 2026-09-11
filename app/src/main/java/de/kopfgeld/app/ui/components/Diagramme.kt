package de.kopfgeld.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Notenverlauf
import de.kopfgeld.app.data.Tagesbilanz
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Radius

/**
 * Wochenbilanz Lern- gegen Scrollzeit, DESIGN.md 5.
 *
 * Zwei Balken je Tag: Lernzeit in Tinte, Scrollzeit in Korrekturrot.
 * Rot heisst hier nicht Fehler, sondern: das ist die Zeit, um die es geht.
 */
@Composable
fun WeekBars(
    tage: List<Tagesbilanz>,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val hoechstwert = tage
        .flatMap { listOf(it.lernMinuten, it.scrollMinuten) }
        .maxOrNull()
        ?.coerceAtLeast(1) ?: 1

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            tage.forEach { tag ->
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                    ) {
                        val spalte = size.width / 2.6f
                        val luecke = size.width * 0.12f
                        val radius = CornerRadius(2.dp.toPx())

                        val lernHoehe = size.height * tag.lernMinuten / hoechstwert
                        val scrollHoehe = size.height * tag.scrollMinuten / hoechstwert

                        drawRoundRect(
                            color = stifte.tinte,
                            topLeft = Offset(0f, size.height - lernHoehe),
                            size = Size(spalte, lernHoehe),
                            cornerRadius = radius,
                        )
                        drawRoundRect(
                            color = stifte.korrektur.copy(alpha = 0.75f),
                            topLeft = Offset(spalte + luecke, size.height - scrollHoehe),
                            size = Size(spalte, scrollHoehe),
                            cornerRadius = radius,
                        )
                    }
                    Text(
                        text = tag.tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.blei,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        Luft(10.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Legendenpunkt(text = "Lernzeit", farbe = stifte.tinte)
            LuftBreit(16.dp)
            Legendenpunkt(text = "Scrollzeit", farbe = stifte.korrektur.copy(alpha = 0.75f))
        }
    }
}

@Composable
private fun Legendenpunkt(text: String, farbe: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .background(farbe, Radius.Chip),
        )
        LuftBreit(6.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = KopfgeldTheme.stifte.blei,
        )
    }
}

/**
 * Notenverlauf 1 bis 6 mit Zielnote, DESIGN.md 5.
 *
 * Die Skala steht auf dem Kopf: 1 ist oben, 6 unten. Die Zielnote ist eine
 * gestrichelte Linie, damit man sieht, wohin es gehen soll.
 */
@Composable
fun GradeTrail(
    verlauf: Notenverlauf,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val punkte = verlauf.punkte

    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            val oben = 8.dp.toPx()
            val unten = size.height - 8.dp.toPx()
            val nutzhoehe = unten - oben

            fun yFuer(note: Double): Float =
                oben + ((note - 1.0) / 5.0).toFloat().coerceIn(0f, 1f) * nutzhoehe

            // Hilfslinien fuer 1 bis 6
            for (note in 1..6) {
                val y = yFuer(note.toDouble())
                drawLine(
                    stifte.karo,
                    Offset(0f, y),
                    Offset(size.width, y),
                    1.dp.toPx(),
                )
            }

            // Zielnote gestrichelt
            drawLine(
                stifte.tinte.copy(alpha = 0.7f),
                Offset(0f, yFuer(verlauf.zielnote)),
                Offset(size.width, yFuer(verlauf.zielnote)),
                1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(8.dp.toPx(), 6.dp.toPx()),
                    0f,
                ),
            )

            if (punkte.isEmpty()) return@Canvas

            val schritt = if (punkte.size == 1) 0f else size.width / (punkte.size - 1)
            val stellen = punkte.mapIndexed { index, punkt ->
                Offset(schritt * index, yFuer(punkt.note))
            }

            stellen.zipWithNext { a, b ->
                drawLine(stifte.tinte, a, b, 2.dp.toPx(), StrokeCap.Round)
            }
            stellen.forEach { stelle ->
                drawCircle(stifte.tinte, radius = 4.dp.toPx(), center = stelle)
            }
        }

        Row(Modifier.fillMaxWidth()) {
            punkte.forEach { punkt ->
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = notenText(punkt.note),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = punkt.datum,
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.blei,
                    )
                }
            }
        }
    }
}

/** Note ohne Nachkommastelle, wenn sie ganz ist: 3.0 wird zu "3". */
fun notenText(note: Double): String =
    if (note % 1.0 == 0.0) note.toInt().toString() else note.toString().replace('.', ',')
