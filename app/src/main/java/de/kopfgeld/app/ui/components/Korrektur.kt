package de.kopfgeld.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Markierung
import de.kopfgeld.app.data.Markierungsart
import de.kopfgeld.app.data.Rasterzeile
import de.kopfgeld.app.data.Urteil
import de.kopfgeld.app.ui.theme.CaveatFamilie
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Radius

/**
 * Gescannte Seite als Platzhalter mit roten Korrekturmarkierungen,
 * DESIGN.md 5 (CorrectionMark). Kreis oder Unterstreichung, nummeriert.
 *
 * In Phase 1 gibt es kein echtes Foto. Der Platzhalter zeigt Karo und
 * angedeutete Handschrift, damit die Markierungen etwas haben, worauf sie
 * sitzen koennen.
 */
@Composable
fun CorrectionMark(
    markierungen: List<Markierung>,
    modifier: Modifier = Modifier,
    hoehe: Dp = 220.dp,
) {
    val stifte = KopfgeldTheme.stifte

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(hoehe)
            .background(MaterialTheme.colorScheme.background, Radius.Flaeche)
            .border(1.dp, stifte.karo, Radius.Flaeche)
            .seitenPlatzhalter(stifte.karo, stifte.blei),
    ) {
        val flaechenbreite = maxWidth
        val flaechenhoehe = maxHeight

        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    markierungen.forEach { m ->
                        val x = size.width * m.xAnteil
                        val y = size.height * m.yAnteil
                        val breite = size.width * m.breiteAnteil
                        when (m.art) {
                            Markierungsart.Kreis -> drawRoundRect(
                                color = stifte.korrektur,
                                topLeft = Offset(x, y - 13.dp.toPx()),
                                size = Size(breite, 26.dp.toPx()),
                                cornerRadius = CornerRadius(13.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx()),
                            )

                            Markierungsart.Unterstrich -> drawLine(
                                stifte.korrektur,
                                Offset(x, y + 8.dp.toPx()),
                                Offset(x + breite, y + 8.dp.toPx()),
                                2.dp.toPx(),
                                StrokeCap.Round,
                            )
                        }
                    }
                },
        )

        // Nummern liegen als Text darauf, damit sie lesbar bleiben.
        markierungen.forEach { m ->
            Nummernkringel(
                nummer = m.nummer,
                modifier = Modifier.offset(
                    x = flaechenbreite * m.xAnteil - 26.dp,
                    y = flaechenhoehe * m.yAnteil - 11.dp,
                ),
            )
        }
    }
}

@Composable
private fun Nummernkringel(nummer: Int, modifier: Modifier = Modifier) {
    val stifte = KopfgeldTheme.stifte
    Box(
        modifier
            .background(MaterialTheme.colorScheme.background, CircleShape)
            .border(1.5.dp, stifte.korrektur, CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = nummer.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = stifte.korrektur,
        )
    }
}

/**
 * Randnotiz der KI, DESIGN.md 5 (CorrectionNote).
 * Caveat in Korrekturrot, um zwei Grad gedreht. Die einzige Stelle in der
 * ganzen App, an der Caveat vorkommt.
 */
@Composable
fun CorrectionNote(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontFamily = CaveatFamilie,
        fontWeight = FontWeight.Normal,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
        color = KopfgeldTheme.stifte.korrektur,
        modifier = modifier.rotate(-2f),
    )
}

/**
 * Drei Segmente je Aufgabe, DESIGN.md 5 (VerdictButtons).
 * Der KI-Vorschlag ist vorausgewaehlt und darunter mit "Vorschlag" markiert,
 * damit klar bleibt, wer was behauptet.
 */
@Composable
fun VerdictButtons(
    gewaehlt: Urteil,
    vorschlag: Urteil,
    beiWahl: (Urteil) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val urteile = listOf(Urteil.Richtig, Urteil.Fehler, Urteil.Luecke)

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .border(1.dp, stifte.karo, Radius.Chip),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            urteile.forEachIndexed { index, urteil ->
                val aktiv = urteil == gewaehlt
                val farbe = when (urteil) {
                    Urteil.Richtig -> stifte.richtig
                    Urteil.Fehler -> stifte.korrektur
                    Urteil.Luecke -> stifte.korrektur
                }
                Box(
                    Modifier
                        .weight(1f)
                        .background(if (aktiv) farbe.copy(alpha = 0.14f) else Color.Transparent)
                        .clickable { beiWahl(urteil) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = urteil.anzeige,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (aktiv) farbe else stifte.blei,
                    )
                }
                if (index < urteile.lastIndex) {
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(stifte.karo),
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            urteile.forEach { urteil ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (urteil == vorschlag) {
                        Text(
                            text = "Vorschlag",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bewertungskriterium mit Balken und Punktzahl, DESIGN.md 5 (RubricBar).
 * Beispiel: "Aufbau 7 von 10".
 */
@Composable
fun RubricBar(
    zeile: Rasterzeile,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val anteil = if (zeile.maximum <= 0) 0f else zeile.punkte.toFloat() / zeile.maximum.toFloat()

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = zeile.kriterium,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${zeile.punkte} von ${zeile.maximum}",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
            )
        }
        Luft(6.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .drawBehind {
                    val radius = CornerRadius(2.dp.toPx())
                    drawRoundRect(
                        color = stifte.tinte.copy(alpha = 0.18f),
                        size = size,
                        cornerRadius = radius,
                    )
                    drawRoundRect(
                        color = stifte.tinte,
                        size = Size(size.width * anteil.coerceIn(0f, 1f), size.height),
                        cornerRadius = radius,
                    )
                },
        )
    }
}
