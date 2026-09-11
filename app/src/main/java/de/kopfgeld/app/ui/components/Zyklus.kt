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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Zyklus
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Ein Zyklus in der Sessionplanung, DESIGN.md 5 und 7.4.
 *
 * Badge, Fach und Themen, Typzeile, Dauer-Aufteilung als Balken
 * (Lernphase hell, Aufgabenphase voll), Anzahl Aufgaben, Ziehgriff rechts.
 */
@Composable
fun CycleBlock(
    zyklus: Zyklus,
    modifier: Modifier = Modifier,
    ausgegraut: Boolean = false,
    ausgegrautHinweis: String? = null,
    beiKlick: () -> Unit = {},
    beiZiehgriff: () -> Unit = {},
) {
    val stifte = KopfgeldTheme.stifte
    val fach = FakeData.fach(zyklus.fachId)
    val themen = FakeData.themenNamen(zyklus.themaIds)
    val deckkraft = if (ausgegraut) 0.45f else 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Seitenrand)
            .background(stifte.tief, Radius.Flaeche)
            .border(1.dp, stifte.karo, Radius.Flaeche)
            .clickable(enabled = !ausgegraut, onClick = beiKlick)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.alpha(deckkraft)) { SubjectBadge(fach) }
            LuftBreit(12.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (themen.isBlank()) fach.name else "${fach.name}: $themen",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = deckkraft),
                )
                Text(
                    text = zyklus.typZeile,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei.copy(alpha = deckkraft),
                )
            }
            Box(
                Modifier
                    .size(Mass.Tippziel)
                    .clickable(onClick = beiZiehgriff),
                contentAlignment = Alignment.Center,
            ) {
                SymbolZiehgriff()
            }
        }

        Luft(10.dp)
        Dauerbalken(
            lernphase = zyklus.lernphaseMinuten,
            aufgabenphase = zyklus.aufgabenphaseMinuten,
            deckkraft = deckkraft,
        )

        Luft(6.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = dauerbeschriftung(zyklus),
                style = MaterialTheme.typography.labelSmall,
                color = stifte.blei.copy(alpha = deckkraft),
            )
            Box(Modifier.weight(1f))
            Text(
                text = aufgabenbeschriftung(zyklus),
                style = MaterialTheme.typography.labelSmall,
                color = stifte.blei.copy(alpha = deckkraft),
            )
        }

        if (ausgegraut && ausgegrautHinweis != null) {
            Luft(8.dp)
            Text(
                text = ausgegrautHinweis,
                style = MaterialTheme.typography.labelSmall,
                color = stifte.korrektur,
            )
        }
    }
}

private fun dauerbeschriftung(zyklus: Zyklus): String = when {
    zyklus.lernphaseMinuten > 0 && zyklus.aufgabenphaseMinuten > 0 ->
        "${zyklus.lernphaseMinuten} + ${zyklus.aufgabenphaseMinuten} Min"

    else -> "${zyklus.gesamtMinuten} Min"
}

private fun aufgabenbeschriftung(zyklus: Zyklus): String = when {
    !zyklus.typ.hatBoxAufgabenphase -> "läuft in der App"
    zyklus.aufgabenAutomatisch -> "Aufgaben automatisch aus dem Brain"
    zyklus.aufgabenIds.isEmpty() -> "keine Aufgaben ausgewählt"
    else -> "${zyklus.aufgabenIds.size} Aufgaben ausgewählt"
}

/**
 * Die Dauer-Aufteilung: Lernphase hell, Aufgabenphase voll.
 * Der Balken ist die einzige Stelle, an der man auf einen Blick sieht,
 * wie viel Zeit ins Lernen und wie viel in die Aufgaben geht.
 */
@Composable
fun Dauerbalken(
    lernphase: Int,
    aufgabenphase: Int,
    modifier: Modifier = Modifier,
    deckkraft: Float = 1f,
) {
    val stifte = KopfgeldTheme.stifte
    val gesamt = (lernphase + aufgabenphase).coerceAtLeast(1)
    val anteilLernen = lernphase.toFloat() / gesamt.toFloat()

    Box(
        modifier
            .fillMaxWidth()
            .height(10.dp)
            .drawBehind {
                val radius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                val grenze = size.width * anteilLernen
                drawRoundRect(
                    color = stifte.tinte.copy(alpha = 0.22f * deckkraft),
                    size = size,
                    cornerRadius = radius,
                )
                if (grenze < size.width) {
                    drawRoundRect(
                        color = stifte.tinte.copy(alpha = deckkraft),
                        topLeft = Offset(grenze, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width - grenze, size.height),
                        cornerRadius = radius,
                    )
                }
            },
    )
}

/**
 * Schmale Zeile zwischen zwei Zyklen, DESIGN.md 5.
 * Sie ist bewusst leise: die Pause ist Teil des Plans, aber kein Block.
 */
@Composable
fun PauseBlock(
    vonMinuten: Int,
    bisMinuten: Int,
    modifier: Modifier = Modifier,
    beiKlick: (() -> Unit)? = null,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier)
            .padding(horizontal = Mass.Seitenrand + 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 18.dp, height = 1.dp)
                .background(stifte.karo),
        )
        LuftBreit(10.dp)
        Text(
            text = if (vonMinuten == bisMinuten) {
                "Pause $vonMinuten Min"
            } else {
                "Pause $vonMinuten–$bisMinuten Min"
            },
            style = MaterialTheme.typography.labelSmall,
            color = stifte.blei,
        )
    }
}
