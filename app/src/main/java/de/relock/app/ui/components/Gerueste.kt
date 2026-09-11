package de.relock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Nacht
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme

/*
 * Gemeinsame Bausteine. Nicht im Komponentenkatalog von DESIGN.md, aber
 * noetig, damit Seitenrand, Titelgroesse und Buttonform nicht an vierzig
 * Stellen wiederholt werden.
 */

/**
 * Der Standardaufbau eines Screens: Titel in Fraunces, Seitenrand 24 dp,
 * viel Raum. Kein TopAppBar - dessen Typografie passt nicht zu den Tokens.
 */
@Composable
fun Geruest(
    titel: String,
    modifier: Modifier = Modifier,
    unterzeile: String? = null,
    zurueck: (() -> Unit)? = null,
    scrollbar: Boolean = true,
    aufTitelLangGedrueckt: (() -> Unit)? = null,
    aktion: @Composable RowScope.() -> Unit = {},
    fussleiste: @Composable (() -> Unit)? = null,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Kopf(
            titel = titel,
            unterzeile = unterzeile,
            zurueck = zurueck,
            aufTitelLangGedrueckt = aufTitelLangGedrueckt,
            aktion = aktion,
        )

        val inhaltModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .then(if (scrollbar) Modifier.verticalScroll(rememberScrollState()) else Modifier)

        Column(modifier = inhaltModifier, content = inhalt)

        if (fussleiste != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        start = Mass.Rand,
                        end = Mass.Rand,
                        top = Mass.Mittel,
                        bottom = Mass.Gross,
                    ),
            ) {
                fussleiste()
            }
        }
    }
}

@Composable
private fun Kopf(
    titel: String,
    unterzeile: String?,
    zurueck: (() -> Unit)?,
    aufTitelLangGedrueckt: (() -> Unit)?,
    aktion: @Composable RowScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = Mass.Rand, end = Mass.Rand, top = Mass.Mittel, bottom = Mass.Klein),
    ) {
        if (zurueck != null) {
            Box(
                Modifier
                    .size(Mass.Tippziel)
                    .clickable(onClick = zurueck),
                contentAlignment = Alignment.CenterStart,
            ) {
                SymbolZurueck()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = titel,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (aufTitelLangGedrueckt != null) {
                            Modifier.langerDruck(aufTitelLangGedrueckt)
                        } else {
                            Modifier
                        },
                    ),
            )
            aktion()
        }
        if (unterzeile != null) {
            Luft(2.dp)
            Text(
                text = unterzeile,
                style = MaterialTheme.typography.bodyMedium,
                color = RelockTheme.farben.matt,
            )
        }
    }
}

/** Feine Trennlinie. Listen sind Zeilen mit Linien, keine Kartenstapel. */
@Composable
fun Trennlinie(modifier: Modifier = Modifier, eingerueckt: Boolean = false) {
    Spacer(
        modifier
            .fillMaxWidth()
            .padding(start = if (eingerueckt) Mass.Rand else 0.dp)
            .height(1.dp)
            .background(RelockTheme.farben.linie),
    )
}

/**
 * Eine Listenzeile: Seitenrand, mindestens 48 dp hoch, darunter eine feine
 * Linie. Radius 0 - Zeilen sind keine Karten.
 */
@Composable
fun Zeile(
    modifier: Modifier = Modifier,
    beiKlick: (() -> Unit)? = null,
    mitLinie: Boolean = true,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier)
                .heightIn(min = Mass.Tippziel)
                .padding(horizontal = Mass.Rand, vertical = 14.dp),
            verticalArrangement = Arrangement.Center,
            content = inhalt,
        )
        if (mitLinie) Trennlinie()
    }
}

/**
 * Abschnittstitel. Sparsam einsetzen - DESIGN.md 1 verbietet ein Label ueber
 * jeder Sektion.
 */
@Composable
fun Abschnitt(
    text: String,
    modifier: Modifier = Modifier,
    aktion: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = Mass.Rand, end = Mass.Rand, top = Mass.Gross, bottom = Mass.Klein),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        if (aktion != null) aktion()
    }
}

/** Erklaerender Fliesstext. */
@Composable
fun Hinweis(
    text: String,
    modifier: Modifier = Modifier,
    zentriert: Boolean = false,
    farbe: Color = Color.Unspecified,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = if (farbe == Color.Unspecified) RelockTheme.farben.matt else farbe,
        textAlign = if (zentriert) TextAlign.Center else TextAlign.Start,
        modifier = modifier,
    )
}

// --- Buttons ---------------------------------------------------------------

/** Messing gefuellt, Text Nacht. Nur fuer die Hauptaktion eines Screens. */
@Composable
fun PrimaryButton(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
) {
    Button(
        onClick = beiKlick,
        enabled = aktiv,
        shape = Radius.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = Messing,
            contentColor = Nacht,
            disabledContainerColor = RelockTheme.farben.flaecheHoch,
            disabledContentColor = RelockTheme.farben.leise,
        ),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Mass.Buttonhoehe),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Rahmen in Linie, Text Elfenbein. */
@Composable
fun SecondaryButton(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) Elfenbein else farbe
    OutlinedButton(
        onClick = beiKlick,
        enabled = aktiv,
        shape = Radius.Button,
        border = androidx.compose.foundation.BorderStroke(1.dp, RelockTheme.farben.linie),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ton,
            disabledContentColor = RelockTheme.farben.leise,
        ),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Mass.Buttonhoehe),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Nur Text, matt. Fuer alles, was man auch weglassen koennte. */
@Composable
fun QuietButton(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    val ton = if (farbe == Color.Unspecified) RelockTheme.farben.matt else farbe
    TextButton(
        onClick = beiKlick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Mass.Tippziel),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = ton)
    }
}

/** Auswahl-Chip, z. B. Faecher oder Abgabetermine. */
@Composable
fun Chip(
    text: String,
    gewaehlt: Boolean,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
) {
    val farben = RelockTheme.farben
    val vordergrund = when {
        !aktiv -> farben.leise
        gewaehlt -> Messing
        else -> Elfenbein
    }
    Box(
        modifier = modifier
            .background(if (gewaehlt) farben.messingTief else Color.Transparent, Radius.Chip)
            .border(1.dp, if (gewaehlt) Messing else farben.linie, Radius.Chip)
            .clickable(enabled = aktiv, onClick = beiKlick)
            .heightIn(min = 40.dp)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = vordergrund)
    }
}

/**
 * Segmentwahl, z. B. 5 / 10 / 15 Min. Ein durchgehender Rahmen, innen
 * feine Trennstriche - kein Stapel einzelner Knoepfe.
 */
@Composable
fun Segmente(
    beschriftungen: List<String>,
    gewaehlt: Int,
    beiWahl: (Int) -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
) {
    val farben = RelockTheme.farben
    Row(
        modifier
            .fillMaxWidth()
            .border(1.dp, farben.linie, Radius.Button),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        beschriftungen.forEachIndexed { index, text ->
            val ist = index == gewaehlt
            Box(
                Modifier
                    .weight(1f)
                    .background(if (ist) farben.messingTief else Color.Transparent)
                    .clickable(enabled = aktiv) { beiWahl(index) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = when {
                        !aktiv -> farben.leise
                        ist -> Messing
                        else -> farben.matt
                    },
                )
            }
            if (index < beschriftungen.lastIndex) {
                Box(
                    Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(farben.linie),
                )
            }
        }
    }
}

/** Kleiner, nicht klickbarer Hinweis-Chip. */
@Composable
fun MerkChip(
    text: String,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
    flaeche: Color = Color.Unspecified,
) {
    val farben = RelockTheme.farben
    Box(
        modifier = modifier
            .background(
                if (flaeche == Color.Unspecified) farben.flaecheHoch else flaeche,
                Radius.Chip,
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (farbe == Color.Unspecified) farben.matt else farbe,
        )
    }
}

/** Senkrechter Abstand. */
@Composable
fun Luft(hoehe: Dp) {
    Spacer(Modifier.height(hoehe))
}

/** Waagerechter Abstand. */
@Composable
fun LuftBreit(breite: Dp) {
    Spacer(Modifier.width(breite))
}
