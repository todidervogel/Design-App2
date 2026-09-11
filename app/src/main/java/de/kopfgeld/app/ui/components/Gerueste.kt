package de.kopfgeld.app.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius
import de.kopfgeld.app.ui.theme.TafelTheme

/*
 * Gemeinsame Geruestbausteine. Nicht im Komponentenkatalog von DESIGN.md,
 * aber noetig, damit Seitenrand, Titelgroesse und Buttonradius nicht an
 * vierzig Stellen wiederholt werden.
 */

/**
 * Heft-Look: Titel in headlineMedium, Seitenrand 20 dp, linksbuendig.
 * Kein TopAppBar, weil dessen Typografie nicht zu den Tokens passt.
 */
@Composable
fun HeftScaffold(
    titel: String,
    modifier: Modifier = Modifier,
    unterzeile: String? = null,
    zurueck: (() -> Unit)? = null,
    scrollbar: Boolean = true,
    karo: Boolean = false,
    aufTitelLangGedrueckt: (() -> Unit)? = null,
    aktion: @Composable (RowScope.() -> Unit)? = null,
    fussleiste: @Composable (() -> Unit)? = null,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Kopfzeile(
            titel = titel,
            unterzeile = unterzeile,
            zurueck = zurueck,
            karo = karo,
            aufTitelLangGedrueckt = aufTitelLangGedrueckt,
            aktion = aktion,
        )

        val inhaltModifier = if (scrollbar) {
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .fillMaxWidth()
                .weight(1f)
        }

        Column(modifier = inhaltModifier, content = inhalt)

        if (fussleiste != null) {
            Trennlinie()
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        start = Mass.Seitenrand,
                        end = Mass.Seitenrand,
                        top = Mass.Mittel,
                        bottom = Mass.Mittel,
                    ),
            ) {
                fussleiste()
            }
        }
    }
}

@Composable
private fun Kopfzeile(
    titel: String,
    unterzeile: String?,
    zurueck: (() -> Unit)?,
    karo: Boolean,
    aufTitelLangGedrueckt: (() -> Unit)?,
    aktion: @Composable (RowScope.() -> Unit)?,
) {
    val kopfModifier = Modifier
        .fillMaxWidth()
        .then(
            if (karo) {
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .karoRaster(KopfgeldTheme.stifte.karo)
            } else {
                Modifier.background(MaterialTheme.colorScheme.background)
            },
        )
        .padding(
            start = Mass.Seitenrand,
            end = Mass.Seitenrand,
            top = Mass.Mittel,
            bottom = Mass.Klein,
        )

    Column(kopfModifier) {
        if (zurueck != null) {
            Row(
                modifier = Modifier
                    .size(Mass.Tippziel)
                    .clickable(onClick = zurueck),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = titel,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
            if (aktion != null) aktion()
        }
        if (unterzeile != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = unterzeile,
                style = MaterialTheme.typography.bodyMedium,
                color = KopfgeldTheme.stifte.blei,
            )
        }
    }
}

/**
 * Tafel-Look: erzwingt die dunkle Farbwelt, unabhaengig vom Systemtheme.
 * Fuer Box, laufende Phasen, Countdown, Pause.
 *
 * @param abdunkeln 0 bis 1. Fuer "In der Box" wird laut DESIGN.md 7.9 mit
 *   80 % Schwarz abgedunkelt.
 */
@Composable
fun TafelScaffold(
    modifier: Modifier = Modifier,
    abdunkeln: Float = 0f,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    TafelTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            if (abdunkeln > 0f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = abdunkeln.coerceIn(0f, 1f))),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Mass.Seitenrand),
                content = inhalt,
            )
        }
    }
}

/** Dünne Rasterlinie statt Schatten. */
@Composable
fun Trennlinie(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(KopfgeldTheme.stifte.karo),
    )
}

/**
 * Eine Zeile auf dem Raster. Ersetzt die Card: Listen sind Zeilen,
 * getrennt durch Rasterlinien (DESIGN.md 3).
 */
@Composable
fun RasterZeile(
    modifier: Modifier = Modifier,
    beiKlick: (() -> Unit)? = null,
    mitTrenner: Boolean = true,
    innenabstand: Boolean = true,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .then(if (beiKlick != null) Modifier.clickable(onClick = beiKlick) else Modifier)
                .heightIn(min = Mass.Tippziel)
                .then(
                    if (innenabstand) {
                        Modifier.padding(
                            horizontal = Mass.Seitenrand,
                            vertical = 12.dp,
                        )
                    } else {
                        Modifier
                    },
                ),
            verticalArrangement = Arrangement.Center,
            content = inhalt,
        )
        if (mitTrenner) Trennlinie()
    }
}

/** Abschnittstitel. Sparsam einsetzen, nicht ueber jeder Sektion. */
@Composable
fun Abschnittstitel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(
            start = Mass.Seitenrand,
            end = Mass.Seitenrand,
            top = Mass.Gross,
            bottom = Mass.Klein,
        ),
    )
}

/** Erklaerender Fliesstext unter einer Ueberschrift oder ueber einem Button. */
@Composable
fun Hinweistext(
    text: String,
    modifier: Modifier = Modifier,
    zentriert: Boolean = false,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = KopfgeldTheme.stifte.blei,
        textAlign = if (zentriert) TextAlign.Center else TextAlign.Start,
        modifier = modifier,
    )
}

/** Voll gefuellter Button, Radius 12, Hoehe 52. Kein Pfeil im Text. */
@Composable
fun PrimaerButton(
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
            containerColor = KopfgeldTheme.stifte.tinte,
            contentColor = MaterialTheme.colorScheme.background,
        ),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Mass.Buttonhoehe),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Zweitrangiger Button mit Tintenrahmen. */
@Composable
fun ZweitButton(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
    farbe: Color = Color.Unspecified,
) {
    val stiftfarbe = if (farbe == Color.Unspecified) KopfgeldTheme.stifte.tinte else farbe
    OutlinedButton(
        onClick = beiKlick,
        enabled = aktiv,
        shape = Radius.Button,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = stiftfarbe),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Mass.Buttonhoehe),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Textlink, z. B. "Eigene Session planen". */
@Composable
fun TextAktion(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    val stiftfarbe = if (farbe == Color.Unspecified) KopfgeldTheme.stifte.tinte else farbe
    TextButton(
        onClick = beiKlick,
        modifier = modifier.defaultMinSize(minHeight = Mass.Tippziel),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = stiftfarbe)
    }
}

/** Chip fuer Auswahl und Filter. Radius 6, kein Versalien-Label. */
@Composable
fun TypChip(
    text: String,
    gewaehlt: Boolean,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
    zentriert: Boolean = false,
) {
    val stifte = KopfgeldTheme.stifte
    val vordergrund = when {
        !aktiv -> stifte.blei.copy(alpha = 0.5f)
        gewaehlt -> stifte.tinte
        else -> MaterialTheme.colorScheme.onSurface
    }
    val flaeche = if (gewaehlt) stifte.tinteHauch else Color.Transparent
    val rahmen = if (gewaehlt) stifte.tinte else stifte.karo

    Box(
        modifier = modifier
            .background(flaeche, Radius.Chip)
            .border(1.dp, rahmen, Radius.Chip)
            .clickable(enabled = aktiv, onClick = beiKlick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = if (zentriert) Alignment.Center else Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = vordergrund,
            textAlign = if (zentriert) TextAlign.Center else TextAlign.Start,
        )
    }
}

/** Kleiner, nicht klickbarer Hinweis-Chip, z. B. der Typ einer Aufgabe. */
@Composable
fun MerkChip(
    text: String,
    modifier: Modifier = Modifier,
    farbe: Color = Color.Unspecified,
) {
    val stifte = KopfgeldTheme.stifte
    val ton = if (farbe == Color.Unspecified) stifte.blei else farbe
    Box(
        modifier = modifier
            .background(stifte.tief, Radius.Chip)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = ton)
    }
}

/** Abstand in Hoehe des Grundrasters. */
@Composable
fun Luft(hoehe: androidx.compose.ui.unit.Dp) {
    Spacer(Modifier.height(hoehe))
}

/** Waagerechter Abstand. */
@Composable
fun LuftBreit(breite: androidx.compose.ui.unit.Dp) {
    Spacer(Modifier.width(breite))
}
