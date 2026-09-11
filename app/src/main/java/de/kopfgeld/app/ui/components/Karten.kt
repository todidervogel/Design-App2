package de.kopfgeld.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Karteikarte
import de.kopfgeld.app.data.Wiederholungswahl
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Karteikarte mit Vorder- und Rueckseite, DESIGN.md 5.
 * Umdrehen per Tipp. Die Rueckseite steht nicht daneben, sondern ersetzt die
 * Vorderseite - sonst faengt man an zu schielen statt zu denken.
 */
@Composable
fun Flashcard(
    karte: Karteikarte,
    umgedreht: Boolean,
    beiUmdrehen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp)
            .background(stifte.tief, Radius.Flaeche)
            .border(1.dp, stifte.karo, Radius.Flaeche)
            .clickable(onClick = beiUmdrehen)
            .padding(Mass.Gross),
    ) {
        Crossfade(targetState = umgedreht, label = "Karte umdrehen") { rueckseite ->
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (rueckseite) "Rückseite" else "Vorderseite",
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.blei,
                )
                Luft(12.dp)
                Text(
                    text = if (rueckseite) karte.rueckseite else karte.vorderseite,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                if (rueckseite && karte.herkunft.isNotBlank()) {
                    Luft(12.dp)
                    Text(
                        text = karte.herkunft,
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.blei,
                        textAlign = TextAlign.Center,
                    )
                }
                if (!rueckseite) {
                    Luft(20.dp)
                    Text(
                        text = "Tippen zum Umdrehen",
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.blei,
                    )
                }
            }
        }
    }
}

/**
 * Vier Bewertungen mit ihrem naechsten Intervall, DESIGN.md 5.
 * Das Intervall steht unter dem Wort, damit man sieht, was die Wahl kostet.
 */
@Composable
fun ReviewButtons(
    wahlen: List<Wiederholungswahl>,
    beiWahl: (Wiederholungswahl) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        wahlen.forEachIndexed { index, wahl ->
            val betont = index == 2 // "Gut" ist der Normalfall
            Column(
                Modifier
                    .weight(1f)
                    .background(
                        if (betont) stifte.tinteHauch else MaterialTheme.colorScheme.background,
                        Radius.Button,
                    )
                    .border(
                        1.dp,
                        if (betont) stifte.tinte else stifte.karo,
                        Radius.Button,
                    )
                    .clickable { beiWahl(wahl) }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = wahl.anzeige,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (betont) stifte.tinte else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = wahl.intervall,
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.blei,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
