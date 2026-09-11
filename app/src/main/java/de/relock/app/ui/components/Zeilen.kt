package de.relock.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.relock.app.data.Fach
import de.relock.app.data.Faelligkeit
import de.relock.app.data.Hausaufgabe
import de.relock.app.data.Zyklus
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Fach-Kuerzel im Kreis mit duennem Rahmen, DESIGN.md 3.
 */
@Composable
fun SubjectMark(
    fach: Fach,
    modifier: Modifier = Modifier,
    groesse: Dp = 34.dp,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) RelockTheme.farben.matt else farbe
    Box(
        modifier = modifier
            .size(groesse)
            .border(1.dp, RelockTheme.farben.linie, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = fach.kuerzel,
            style = MaterialTheme.typography.labelSmall,
            color = ton,
        )
    }
}

/**
 * Eine Hausaufgabe als Zeile, DESIGN.md 3.
 *
 * Fach-Kuerzel, Aufgabe, Abgabe. Ueberfaellig steht in Signal, erledigt ist
 * durchgestrichen. Wischen nach rechts hakt ab, nach links loescht.
 */
@Composable
fun HomeworkRow(
    hausaufgabe: Hausaufgabe,
    modifier: Modifier = Modifier,
    beiKlick: () -> Unit = {},
    beiErledigt: () -> Unit = {},
    beiLoeschen: () -> Unit = {},
) {
    val farben = RelockTheme.farben
    val fach = FakeData.fach(hausaufgabe.fachId)
    val ueberfaellig = hausaufgabe.faellig == Faelligkeit.Ueberfaellig && !hausaufgabe.erledigt

    val abgabe = when {
        hausaufgabe.erledigt -> "erledigt"
        else -> hausaufgabe.faellig.anzeige
    }
    val abgabefarbe = when {
        hausaufgabe.erledigt -> farben.leise
        ueberfaellig -> Signal
        else -> farben.matt
    }

    Column(modifier.fillMaxWidth()) {
        WischZeile(
            beiRechts = beiErledigt,
            beiLinks = beiLoeschen,
            rechtsText = if (hausaufgabe.erledigt) "Offen" else "Erledigt",
            rechtsFarbe = Richtig,
            linksText = "Löschen",
            linksFarbe = Signal,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(onClick = beiKlick)
                    .heightIn(min = 64.dp)
                    .padding(horizontal = Mass.Rand, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SubjectMark(
                    fach = fach,
                    farbe = if (hausaufgabe.erledigt) farben.leise else farben.matt,
                )
                LuftBreit(14.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = hausaufgabe.aufgabe,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (hausaufgabe.erledigt) farben.leise else Elfenbein,
                        textDecoration = if (hausaufgabe.erledigt) {
                            TextDecoration.LineThrough
                        } else {
                            null
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = abgabe,
                        style = MaterialTheme.typography.labelSmall,
                        color = abgabefarbe,
                    )
                }
                LuftBreit(12.dp)
                Statuspunkt(
                    farbe = when {
                        hausaufgabe.erledigt -> Richtig
                        ueberfaellig -> Signal
                        else -> farben.linie
                    },
                    gefuellt = hausaufgabe.erledigt || ueberfaellig,
                )
            }
        }
        Trennlinie()
    }
}

/** Kleiner Punkt, gefuellt oder als Ring. */
@Composable
fun Statuspunkt(
    farbe: Color,
    modifier: Modifier = Modifier,
    gefuellt: Boolean = true,
    groesse: Dp = 8.dp,
) {
    Box(
        modifier
            .size(groesse)
            .then(
                if (gefuellt) {
                    Modifier.background(farbe, CircleShape)
                } else {
                    Modifier.border(1.dp, farbe, CircleShape)
                },
            ),
    )
}

/**
 * Ein Zyklus in der Sessionplanung, DESIGN.md 3.
 * Typ-Symbol, Fach und Thema, Dauer, Ziehgriff rechts.
 */
@Composable
fun CycleRow(
    zyklus: Zyklus,
    modifier: Modifier = Modifier,
    beiKlick: () -> Unit = {},
    ziehgriffModifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val fach = FakeData.fach(zyklus.fachId)

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = beiKlick)
                .heightIn(min = 68.dp)
                .padding(start = Mass.Rand, end = Mass.Klein, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SymbolZyklustyp(zyklus.typ)
            LuftBreit(14.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${fach.name}: ${zyklus.thema}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildString {
                        append(zyklus.typ.anzeige)
                        if (zyklus.aufgabenAnzahl > 0) {
                            append(" · ${zyklus.aufgabenAnzahl} Aufgaben")
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
            LuftBreit(12.dp)
            Text(
                text = "${zyklus.minuten} Min",
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
            )
            Box(
                Modifier
                    .size(Mass.Tippziel)
                    .then(ziehgriffModifier),
                contentAlignment = Alignment.Center,
            ) {
                SymbolZiehgriff()
            }
        }
        Trennlinie()
    }
}

/**
 * Die schmale Zeile zwischen zwei Zyklen, DESIGN.md 3.
 * Bewusst leise: die Pause gehoert zum Plan, ist aber kein Block.
 */
@Composable
fun PauseRow(
    vonMinuten: Int,
    bisMinuten: Int,
    modifier: Modifier = Modifier,
    beiKlick: (() -> Unit)? = null,
) {
    val farben = RelockTheme.farben
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier)
            .padding(start = Mass.Rand + 36.dp, end = Mass.Rand, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 16.dp, height = 1.dp)
                .background(farben.linie),
        )
        LuftBreit(10.dp)
        Text(
            text = if (vonMinuten == bisMinuten) {
                "Pause $vonMinuten Min"
            } else {
                "Pause $vonMinuten–$bisMinuten Min"
            },
            style = MaterialTheme.typography.labelSmall,
            color = farben.leise,
        )
    }
}

/**
 * Wischen nach links und rechts mit je einer Aktion.
 *
 * Selbst gebaut statt SwipeToDismissBox, damit die Hintergrundfarben genau
 * Richtig und Signal treffen und der Ausloeseweg kurz bleibt.
 */
@Composable
fun WischZeile(
    beiRechts: () -> Unit,
    beiLinks: () -> Unit,
    rechtsText: String,
    rechtsFarbe: Color,
    linksText: String,
    linksFarbe: Color,
    modifier: Modifier = Modifier,
    inhalt: @Composable () -> Unit,
) {
    val dichte = LocalDensity.current
    val bereich = rememberCoroutineScope()
    val versatz = remember { Animatable(0f) }
    val schwelle = with(dichte) { 96.dp.toPx() }

    Box(modifier.fillMaxWidth()) {
        if (abs(versatz.value) > 1f) {
            val nachRechts = versatz.value > 0f
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        (if (nachRechts) rechtsFarbe else linksFarbe).copy(alpha = 0.16f),
                    )
                    .padding(horizontal = Mass.Rand),
                contentAlignment = if (nachRechts) Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                Text(
                    text = if (nachRechts) rechtsText else linksText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (nachRechts) rechtsFarbe else linksFarbe,
                )
            }
        }

        Box(
            Modifier
                .offset { IntOffset(versatz.value.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            bereich.launch {
                                val wert = versatz.value
                                versatz.animateTo(0f)
                                if (wert > schwelle) beiRechts()
                                if (wert < -schwelle) beiLinks()
                            }
                        },
                        onDragCancel = {
                            bereich.launch { versatz.animateTo(0f) }
                        },
                    ) { _, verschiebung ->
                        bereich.launch {
                            val neu = (versatz.value + verschiebung)
                                .coerceIn(-size.width.toFloat() / 2f, size.width.toFloat() / 2f)
                            versatz.snapTo(neu)
                        }
                    }
                },
        ) {
            inhalt()
        }
    }
}

/** Zeile, die zu einem weiteren Screen fuehrt: Text links, Pfeil rechts. */
@Composable
fun WeiterZeile(
    titel: String,
    modifier: Modifier = Modifier,
    unterzeile: String? = null,
    wert: String? = null,
    wertfarbe: Color = Color.Unspecified,
    beiKlick: () -> Unit,
) {
    val farben = RelockTheme.farben
    Zeile(modifier = modifier, beiKlick = beiKlick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = titel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                )
                if (unterzeile != null) {
                    Text(
                        text = unterzeile,
                        style = MaterialTheme.typography.labelSmall,
                        color = farben.matt,
                    )
                }
            }
            if (wert != null) {
                Text(
                    text = wert,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (wertfarbe == Color.Unspecified) farben.matt else wertfarbe,
                )
                LuftBreit(8.dp)
            }
            SymbolWeiter()
        }
    }
}

/** Flaeche mit Rahmen, fuer abgesetzte Bloecke wie das Tagesziel. */
@Composable
fun Feld(
    modifier: Modifier = Modifier,
    inhalt: @Composable () -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(RelockTheme.farben.flaeche, Radius.Flaeche)
            .padding(Mass.Mittel),
    ) {
        inhalt()
    }
}

/** Der Messing-Streifen fuer Hinweise wie "Relock 1.1 ist da". */
@Composable
fun Messingleiste(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(RelockTheme.farben.messingTief)
            .clickable(onClick = beiKlick)
            .padding(horizontal = Mass.Rand, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Messing,
            modifier = Modifier.weight(1f),
        )
        SymbolWeiter(farbe = Messing)
    }
}
