package de.kopfgeld.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Fach
import de.kopfgeld.app.data.Themastand
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Kuerzel-Quadrat, DESIGN.md 5. Pruefungsfaecher bekommen einen Tinten-Rahmen.
 */
@Composable
fun SubjectBadge(
    fach: Fach,
    modifier: Modifier = Modifier,
    groesse: Dp = Mass.Fachbadge,
) {
    val stifte = KopfgeldTheme.stifte
    Box(
        modifier = modifier
            .size(groesse)
            .background(stifte.tief, Radius.Chip)
            .then(
                if (fach.istPruefungsfach) {
                    Modifier.border(1.5.dp, stifte.tinte, Radius.Chip)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = fach.kuerzel,
            style = MaterialTheme.typography.titleSmall,
            color = if (fach.istPruefungsfach) stifte.tinte else MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Stand eines Themas, DESIGN.md 5:
 * neu = leerer Kreis, gelernt = halb, sitzt = voll.
 */
@Composable
fun TopicStatus(
    stand: Themastand,
    modifier: Modifier = Modifier,
    groesse: Dp = 14.dp,
) {
    val farbe = KopfgeldTheme.stifte.tinte
    Box(
        modifier
            .size(groesse)
            .drawBehind {
                val radius = size.minDimension / 2f
                val mitte = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = farbe,
                    radius = radius - 1.dp.toPx() / 2f,
                    center = mitte,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                )
                when (stand) {
                    Themastand.Neu -> Unit
                    Themastand.Gelernt -> drawArc(
                        color = farbe,
                        startAngle = 90f,
                        sweepAngle = 180f,
                        useCenter = true,
                    )

                    Themastand.Sitzt -> drawCircle(
                        color = farbe,
                        radius = radius,
                        center = mitte,
                    )
                }
            },
    )
}

/**
 * Testphasen-Banner, DESIGN.md 5: KorrekturHauch als Flaeche, roter linker Rand.
 * Rot heisst hier "ab jetzt nur noch testen", nicht "Fehler" - aber es ist
 * derselbe Ernst, deshalb dieselbe Farbe.
 */
@Composable
fun TestphaseBanner(
    text: String,
    modifier: Modifier = Modifier,
    zusatz: String? = null,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(stifte.korrekturHauch)
            .drawBehind {
                drawRect(
                    color = stifte.korrektur,
                    size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height),
                )
            }
            .padding(
                start = Mass.Seitenrand,
                end = Mass.Seitenrand,
                top = 12.dp,
                bottom = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.korrektur,
            )
            if (zusatz != null) {
                Text(
                    text = zusatz,
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.korrektur.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/** Fach plus Themenliste in einer Zeile, oft gebraucht. */
@Composable
fun FachZeileKopf(
    fach: Fach,
    themen: String,
    modifier: Modifier = Modifier,
    nebentext: String? = null,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        SubjectBadge(fach)
        Box(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = if (themen.isBlank()) fach.name else "${fach.name}: $themen",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (nebentext != null) {
                Text(
                    text = nebentext,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KopfgeldTheme.stifte.blei,
                )
            }
        }
    }
}

/** Farbe fuer einen Aufgabenstand. Rot nur fuer Fehler. */
@Composable
fun standfarbe(fehler: Boolean, sitzt: Boolean): Color {
    val stifte = KopfgeldTheme.stifte
    return when {
        fehler -> stifte.korrektur
        sitzt -> stifte.richtig
        else -> stifte.blei
    }
}
