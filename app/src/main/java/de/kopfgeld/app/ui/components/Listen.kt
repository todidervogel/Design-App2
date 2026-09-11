package de.kopfgeld.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Aufgabe
import de.kopfgeld.app.data.Aufgabenstand
import de.kopfgeld.app.data.Planart
import de.kopfgeld.app.data.Planposten
import de.kopfgeld.app.data.Scan
import de.kopfgeld.app.data.Scanstand
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Kaestchen zum Abhaken. Bewusst eckig und in Tinte gezeichnet statt der
 * runden Material-Checkbox - es soll aussehen wie ein Kaestchen im Heft.
 */
@Composable
fun Kaestchen(
    angehakt: Boolean,
    beiKlick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) KopfgeldTheme.stifte.tinte else farbe
    Box(
        modifier = modifier
            .size(Mass.Tippziel)
            .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(20.dp)
                .drawBehind {
                    val strich = 1.5.dp.toPx()
                    drawRect(
                        color = if (angehakt) ton else zart(ton),
                        size = Size(size.width, size.height),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strich),
                    )
                    if (angehakt) {
                        drawLine(
                            ton,
                            Offset(size.width * 0.22f, size.height * 0.52f),
                            Offset(size.width * 0.44f, size.height * 0.74f),
                            strich * 1.4f,
                            StrokeCap.Round,
                        )
                        drawLine(
                            ton,
                            Offset(size.width * 0.44f, size.height * 0.74f),
                            Offset(size.width * 0.80f, size.height * 0.26f),
                            strich * 1.4f,
                            StrokeCap.Round,
                        )
                    }
                },
        )
    }
}

/** Ungehakt erscheint das Kaestchen nur zart, damit die Liste ruhig bleibt. */
private fun zart(ton: Color): Color = ton.copy(alpha = 0.45f)

/**
 * Checklisten-Zeile des Tagesplans, DESIGN.md 5 und 7.2.
 * Kaestchen, Titel, Metazeile, Dauer rechts. Erledigt = Tinten-Durchstrich.
 */
@Composable
fun PlanRow(
    posten: Planposten,
    beiAnhaken: () -> Unit,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val schlafen = posten.art == Planart.Schlafenszeit

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(enabled = !schlafen, onClick = beiKlick)
                .padding(end = Mass.Seitenrand),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .width(Mass.Seitenrand + Mass.Tippziel)
                    .padding(start = Mass.Seitenrand - 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (schlafen) {
                    Box(Modifier.size(Mass.Tippziel), contentAlignment = Alignment.Center) {
                        SymbolMond()
                    }
                } else {
                    Kaestchen(angehakt = posten.erledigt, beiKlick = beiAnhaken)
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = posten.titel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (posten.erledigt) stifte.tinte else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (posten.erledigt) TextDecoration.LineThrough else null,
                )
                if (posten.meta.isNotBlank()) {
                    Text(
                        text = posten.meta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = stifte.blei,
                    )
                }
            }

            if (posten.dauer.isNotBlank()) {
                Text(
                    text = posten.dauer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }
        }
        Trennlinie()
    }
}

/**
 * Aufgabe aus dem Brain, DESIGN.md 5.
 * Quelle, erster Satz, Typ-Chip, Stand. Im Auswahlmodus mit Kaestchen.
 */
@Composable
fun TaskRow(
    aufgabe: Aufgabe,
    modifier: Modifier = Modifier,
    auswahlmodus: Boolean = false,
    ausgewaehlt: Boolean = false,
    beiKlick: () -> Unit = {},
) {
    val stifte = KopfgeldTheme.stifte
    val standfarbe = when (aufgabe.stand) {
        Aufgabenstand.Fehler -> stifte.korrektur
        Aufgabenstand.Sitzt -> stifte.richtig
        else -> stifte.blei
    }

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = beiKlick)
                .padding(end = Mass.Seitenrand),
            verticalAlignment = Alignment.Top,
        ) {
            if (auswahlmodus) {
                Box(
                    Modifier
                        .width(Mass.Seitenrand + Mass.Tippziel)
                        .padding(start = Mass.Seitenrand - 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Kaestchen(angehakt = ausgewaehlt, beiKlick = beiKlick)
                }
            } else {
                Box(Modifier.width(Mass.Seitenrand))
            }

            Column(
                Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    text = aufgabe.quellenangabe,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = aufgabe.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MerkChip(aufgabe.typ)
                    LuftBreit(8.dp)
                    Text(
                        text = aufgabe.stand.anzeige,
                        style = MaterialTheme.typography.labelSmall,
                        color = standfarbe,
                    )
                    if (aufgabe.hatLoesung) {
                        LuftBreit(8.dp)
                        Text(
                            text = "Lösung vorhanden",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                }
            }
        }
        Trennlinie()
    }
}

/**
 * Vorschaubild einer gescannten Seite, DESIGN.md 5.
 * Platzhalter mit Karo und angedeuteten Schriftlinien, Seitennummer,
 * Status-Punkt. Es gibt in Phase 1 keine echten Kamerabilder.
 */
@Composable
fun ScanThumb(
    scan: Scan,
    modifier: Modifier = Modifier,
    breite: androidx.compose.ui.unit.Dp = 64.dp,
    beiKlick: (() -> Unit)? = null,
) {
    val stifte = KopfgeldTheme.stifte
    val statusfarbe = when (scan.stand) {
        Scanstand.Wartet -> stifte.blei
        Scanstand.Analysiert -> stifte.tinte
        Scanstand.Fertig -> stifte.richtig
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
                .height(breite * 1.4f)
                .background(MaterialTheme.colorScheme.background, Radius.Chip)
                .border(1.dp, stifte.karo, Radius.Chip)
                .seitenPlatzhalter(stifte.karo, stifte.blei),
        )
        Row(
            Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .background(statusfarbe, androidx.compose.foundation.shape.CircleShape),
            )
            LuftBreit(5.dp)
            Text(
                text = "S. ${scan.seitennummer}",
                style = MaterialTheme.typography.labelSmall,
                color = stifte.blei,
            )
        }
    }
}

/**
 * Zeichnet einen Seiten-Platzhalter: Karo und ein paar Schriftlinien.
 * Steht ueberall dort, wo spaeter ein echtes Foto liegt.
 */
fun Modifier.seitenPlatzhalter(
    karofarbe: Color,
    schriftfarbe: Color,
): Modifier = this.drawBehind {
    val kaestchen = size.width / 5f
    val duenn = 1f
    var x = kaestchen
    while (x < size.width) {
        drawLine(karofarbe.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, size.height), duenn)
        x += kaestchen
    }
    var y = kaestchen
    while (y < size.height) {
        drawLine(karofarbe.copy(alpha = 0.5f), Offset(0f, y), Offset(size.width, y), duenn)
        y += kaestchen
    }
    // Angedeutete Handschrift: unterschiedlich lange Striche.
    val anteile = listOf(0.72f, 0.58f, 0.80f, 0.40f, 0.66f)
    anteile.forEachIndexed { i, anteil ->
        val zeile = size.height * (0.22f + i * 0.14f)
        if (zeile < size.height * 0.94f) {
            drawLine(
                schriftfarbe.copy(alpha = 0.55f),
                Offset(size.width * 0.12f, zeile),
                Offset(size.width * (0.12f + anteil * 0.78f), zeile),
                2f,
                StrokeCap.Round,
            )
        }
    }
}
