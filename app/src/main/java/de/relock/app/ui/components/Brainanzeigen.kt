package de.relock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.relock.app.data.Buchseite
import de.relock.app.data.Seitenstand
import de.relock.app.data.Urteil
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Nacht
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

/**
 * Richtig / Fehler / Luecke als Segmente, DESIGN.md 3.
 * Der Vorschlag der Erkennung ist vorausgewaehlt und traegt einen
 * Messing-Rahmen mit der Beschriftung "Vorschlag".
 */
@Composable
fun VerdictSelector(
    gewaehlt: Urteil?,
    vorschlag: Urteil,
    beiWahl: (Urteil) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val urteile = listOf(Urteil.Richtig, Urteil.Fehler, Urteil.Luecke)

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            urteile.forEach { urteil ->
                val ist = urteil == gewaehlt
                val istVorschlag = urteil == vorschlag
                val eigenfarbe = when (urteil) {
                    Urteil.Richtig -> Richtig
                    Urteil.Fehler -> Signal
                    Urteil.Luecke -> farben.matt
                }
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                if (ist) eigenfarbe.copy(alpha = 0.16f) else Color.Transparent,
                                Radius.Button,
                            )
                            .border(
                                width = if (istVorschlag) 1.5.dp else 1.dp,
                                color = when {
                                    ist -> eigenfarbe
                                    istVorschlag -> Messing
                                    else -> farben.linie
                                },
                                shape = Radius.Button,
                            )
                            .clickable { beiWahl(urteil) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = urteil.anzeige,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (ist) eigenfarbe else farben.matt,
                        )
                    }
                    if (istVorschlag) {
                        Luft(4.dp)
                        Text(
                            text = "Vorschlag",
                            style = MaterialTheme.typography.labelSmall,
                            color = Messing,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Das Seitenraster eines Buchs, DESIGN.md 3 und 6.19.
 * Kacheln mit Seitenzahl und Status. Abweichungen stehen in Signal.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageGrid(
    seiten: List<Buchseite>,
    modifier: Modifier = Modifier,
    spalten: Int = 4,
    beiSeite: (Buchseite) -> Unit = {},
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = spalten,
    ) {
        seiten.forEach { seite ->
            Seitenkachel(
                seite = seite,
                beiKlick = { beiSeite(seite) },
                modifier = Modifier.weight(1f),
            )
        }
        // Auffuellen, damit die letzte Reihe nicht auseinandergezogen wird.
        val rest = seiten.size % spalten
        if (rest != 0) {
            repeat(spalten - rest) {
                Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Seitenkachel(
    seite: Buchseite,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val (rahmen, schrift) = when (seite.stand) {
        Seitenstand.Erkannt -> farben.linie to Elfenbein
        Seitenstand.Importiert -> farben.linie to farben.matt
        Seitenstand.Abweichung -> Signal to Signal
        Seitenstand.Fehlt -> farben.linie.copy(alpha = 0.5f) to farben.leise
    }

    Box(
        modifier
            .aspectRatio(0.78f)
            .background(
                if (seite.stand == Seitenstand.Fehlt) Color.Transparent else farben.flaeche,
                Radius.Chip,
            )
            .border(1.dp, rahmen, Radius.Chip)
            .clickable(onClick = beiKlick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = seite.nummer.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = schrift,
            )
            if (seite.stand == Seitenstand.Erkannt && seite.aufgaben > 0) {
                Text(
                    text = "${seite.aufgaben}",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.leise,
                )
            }
            if (seite.stand == Seitenstand.Abweichung) {
                Luft(2.dp)
                Statuspunkt(farbe = Signal, groesse = 5.dp)
            }
        }
    }
}

/** Legende unter dem Seitenraster. */
@Composable
fun PageGridLegende(modifier: Modifier = Modifier) {
    val farben = RelockTheme.farben
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Legende("erkannt", Elfenbein)
        Legende("importiert", farben.matt)
        Legende("Abweichung", Signal)
        Legende("fehlt", farben.leise)
    }
}

@Composable
private fun Legende(text: String, farbe: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Statuspunkt(farbe = farbe, groesse = 5.dp)
        LuftBreit(5.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = RelockTheme.farben.leise,
        )
    }
}

/**
 * Ein Fortschrittsstrich, wie er hinter den Buechern im Brain steht.
 */
@Composable
fun Fortschrittsstrich(
    anteil: Float,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) Messing else farbe
    Box(
        modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(RelockTheme.farben.linie, Radius.Chip),
    ) {
        Box(
            Modifier
                .fillMaxWidth(anteil.coerceIn(0f, 1f))
                .height(3.dp)
                .background(ton, Radius.Chip),
        )
    }
}

/**
 * Segmentierte Umschaltung im Kopf eines Screens, z. B.
 * "Bücher / Arbeitsblätter / Fächer". Flacher als Segmente, ohne Rahmen.
 */
@Composable
fun Reiter(
    beschriftungen: List<String>,
    gewaehlt: Int,
    beiWahl: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        beschriftungen.forEachIndexed { index, text ->
            val ist = index == gewaehlt
            Column(
                Modifier.clickable { beiWahl(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (ist) Elfenbein else farben.matt,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
                Box(
                    Modifier
                        .width(if (ist) 20.dp else 0.dp)
                        .height(2.dp)
                        .background(Messing, Radius.Chip),
                )
            }
        }
    }
}

/** Ein Wert mit Beschriftung darunter, fuer Bilanzzeilen. */
@Composable
fun Kennzahl(
    wert: String,
    beschriftung: String,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    Column(modifier) {
        Text(
            text = wert,
            style = MaterialTheme.typography.headlineSmall,
            color = if (farbe == Color.Unspecified) Elfenbein else farbe,
        )
        Text(
            text = beschriftung,
            style = MaterialTheme.typography.labelSmall,
            color = RelockTheme.farben.matt,
        )
    }
}

/** Der Kreis mit Zahl, wie er als Markierung auf einem Scan sitzt. */
@Composable
fun Markierungskringel(
    nummer: Int,
    richtig: Boolean,
    modifier: Modifier = Modifier,
) {
    val farbe = if (richtig) Richtig else Signal
    Box(
        modifier
            .background(Nacht.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)
            .border(1.5.dp, farbe, androidx.compose.foundation.shape.CircleShape)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = nummer.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = farbe,
        )
    }
}
