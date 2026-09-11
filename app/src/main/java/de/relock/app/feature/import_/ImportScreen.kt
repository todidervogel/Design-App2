package de.relock.app.feature.import_

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.relock.app.data.Fach
import de.relock.app.data.Scan
import de.relock.app.data.Scanstand
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.Chip
import de.relock.app.ui.components.Fortschrittsstrich
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.Statuspunkt
import de.relock.app.ui.components.SubjectMark
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.WeiterZeile
import de.relock.app.ui.components.Zeile
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.components.seitenPlatzhalter
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

// ================================================================= 6.21 Import

/** Was importiert wird, Schritt 1. */
enum class Importart(val anzeige: String, val erklaerung: String) {
    Buch("Buch", "Ganze Seiten aus einem Schulbuch"),
    Arbeitsblatt("Arbeitsblatt", "Einzelne Blätter von heute"),
    Loesungen("Lösungen", "Ein Lösungsheft zu einem Buch"),
}

/** Die sechs Schritte des Imports, DESIGN.md 6.21. */
enum class Importschritt(val titel: String) {
    Art("Was importierst du?"),
    Ziel("Wohin?"),
    Seiten("Seiten"),
    Hochladen("Hochladen"),
    Warteschlange("Warteschlange"),
    Pruefen("Prüfen"),
}

/**
 * Import, DESIGN.md 6.21.
 *
 * Ein Assistent mit sechs Schritten. Es passiert nichts wirklich: die
 * Galerie ist ein Raster aus Platzhaltern, der Fortschritt eine Zahl.
 */
data class ImportUiState(
    val schritt: Importschritt = Importschritt.Art,
    val art: Importart? = null,
    val buchId: String? = null,
    val neuesBuch: Boolean = false,
    val titel: String = "",
    val fachId: String? = null,
    val bilder: Int = 0,
    val ersteSeite: Int = 81,
    val hochgeladen: Int = FakeData.IMPORT_HOCHGELADEN,
    val gesamt: Int = FakeData.IMPORT_GESAMT,
    val warteschlange: List<Scan> = FakeData.importWarteschlange,
    val abweichungen: List<FakeData.Abweichung> = FakeData.abweichungen,
    val stichprobe: List<Int> = listOf(81, 84, 88, 92, 96),
) {
    val schrittnummer: Int get() = schritt.ordinal + 1

    /** Ob es im aktuellen Schritt weitergehen kann. */
    val weiterMoeglich: Boolean
        get() = when (schritt) {
            Importschritt.Art -> art != null
            Importschritt.Ziel ->
                if (neuesBuch || art == Importart.Arbeitsblatt) {
                    titel.isNotBlank() && fachId != null
                } else {
                    buchId != null
                }

            Importschritt.Seiten -> bilder > 0
            Importschritt.Hochladen -> hochgeladen >= gesamt
            Importschritt.Warteschlange -> true
            Importschritt.Pruefen -> true
        }
}

@Composable
fun ImportScreen(
    state: ImportUiState,
    beiZurueck: () -> Unit,
    beiArt: (Importart) -> Unit,
    beiBuch: (String) -> Unit,
    beiNeuesBuch: () -> Unit,
    beiTitel: (String) -> Unit,
    beiFach: (String) -> Unit,
    beiGalerie: () -> Unit,
    beiScannen: () -> Unit,
    beiErsteSeite: (Int) -> Unit,
    beiAbweichungUebernehmen: (Int) -> Unit,
    beiAbweichungKorrigieren: (Int) -> Unit,
    beiWeiter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Geruest(
        titel = state.schritt.titel,
        unterzeile = "Schritt ${state.schrittnummer} von ${Importschritt.entries.size}",
        zurueck = beiZurueck,
        modifier = modifier,
        fussleiste = {
            PrimaryButton(
                text = when (state.schritt) {
                    Importschritt.Pruefen -> "Alle übernehmen"
                    Importschritt.Hochladen -> "Weiter"
                    else -> "Weiter"
                },
                beiKlick = beiWeiter,
                aktiv = state.weiterMoeglich,
            )
        },
    ) {
        // Ein feiner Strich zeigt, wie weit der Assistent ist.
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Fortschrittsstrich(
                anteil = state.schrittnummer.toFloat() / Importschritt.entries.size,
            )
        }
        Luft(Mass.Gross)

        when (state.schritt) {
            Importschritt.Art -> SchrittArt(state.art, beiArt)
            Importschritt.Ziel -> SchrittZiel(state, beiBuch, beiNeuesBuch, beiTitel, beiFach)
            Importschritt.Seiten -> SchrittSeiten(state, beiGalerie, beiScannen, beiErsteSeite)
            Importschritt.Hochladen -> SchrittHochladen(state)
            Importschritt.Warteschlange -> SchrittWarteschlange(state)
            Importschritt.Pruefen -> SchrittPruefen(
                state = state,
                beiUebernehmen = beiAbweichungUebernehmen,
                beiKorrigieren = beiAbweichungKorrigieren,
            )
        }

        Luft(Mass.Gross)
        // Der Tipp steht in jedem Schritt, DESIGN.md 6.21.
        Hinweis(
            text = "Tipp: Du kannst Bilder auch auf dem PC in den Ordner " +
                "import/Buchname legen.",
        )
        Luft(Mass.Gross)
    }
}

// ------------------------------------------------------- Schritt 1: Was?

@Composable
private fun SchrittArt(gewaehlt: Importart?, beiWahl: (Importart) -> Unit) {
    Trennlinie()
    Importart.entries.forEach { art ->
        WeiterZeile(
            titel = art.anzeige,
            unterzeile = art.erklaerung,
            wert = if (art == gewaehlt) "gewählt" else null,
            wertfarbe = Messing,
            beiKlick = { beiWahl(art) },
        )
    }
}

// ------------------------------------------------------ Schritt 2: Wohin?

@Composable
private fun SchrittZiel(
    state: ImportUiState,
    beiBuch: (String) -> Unit,
    beiNeuesBuch: () -> Unit,
    beiTitel: (String) -> Unit,
    beiFach: (String) -> Unit,
) {
    val eigenesFeld = state.neuesBuch || state.art == Importart.Arbeitsblatt

    if (state.art != Importart.Arbeitsblatt) {
        Abschnitt(text = "Vorhandene Bücher")
        Trennlinie()
        FakeData.buecher.forEach { buch ->
            WeiterZeile(
                titel = buch.titel,
                unterzeile = FakeData.fach(buch.fachId).name,
                wert = if (buch.id == state.buchId && !state.neuesBuch) "gewählt" else null,
                wertfarbe = Messing,
                beiKlick = { beiBuch(buch.id) },
            )
        }
        WeiterZeile(
            titel = "Neues Buch",
            wert = if (state.neuesBuch) "gewählt" else null,
            wertfarbe = Messing,
            beiKlick = beiNeuesBuch,
        )
    }

    if (eigenesFeld) {
        Luft(Mass.Gross)
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            de.relock.app.feature.onboarding.Eingabe(
                wert = state.titel,
                beiAenderung = beiTitel,
                beschriftung = "Titel",
                platzhalter = if (state.art == Importart.Arbeitsblatt) {
                    "Prozent und Zinsen"
                } else {
                    "Mathe live 10"
                },
            )
        }
        Luft(Mass.Mittel)
        Abschnitt(text = "Fach")
        Fachwahl(gewaehlt = state.fachId, beiWahl = beiFach)
    }
}

/** Die Faecher als Reihe von Chips mit ihrem Kuerzel. */
@Composable
private fun Fachwahl(gewaehlt: String?, beiWahl: (String) -> Unit) {
    Column(Modifier.padding(horizontal = Mass.Rand)) {
        FakeData.faecher.chunked(3).forEach { reihe ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                reihe.forEach { fach ->
                    Fachchip(
                        fach = fach,
                        gewaehlt = fach.id == gewaehlt,
                        beiKlick = { beiWahl(fach.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - reihe.size) {
                    Box(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Fachchip(
    fach: Fach,
    gewaehlt: Boolean,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Row(
        modifier
            .background(
                if (gewaehlt) farben.messingTief else farben.flaeche,
                Radius.Chip,
            )
            .border(
                1.dp,
                if (gewaehlt) Messing else farben.linie,
                Radius.Chip,
            )
            .leiseKlickbar(beiKlick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SubjectMark(fach = fach, groesse = 22.dp)
        LuftBreit(8.dp)
        Text(
            text = fach.name,
            style = MaterialTheme.typography.labelLarge,
            color = if (gewaehlt) Elfenbein else farben.matt,
        )
    }
}

// ----------------------------------------------------- Schritt 3: Seiten

@Composable
private fun SchrittSeiten(
    state: ImportUiState,
    beiGalerie: () -> Unit,
    beiScannen: () -> Unit,
    beiErsteSeite: (Int) -> Unit,
) {
    Column(Modifier.padding(horizontal = Mass.Rand)) {
        SecondaryButton(text = "Aus Galerie wählen", beiKlick = beiGalerie)
        Luft(Mass.Klein)
        SecondaryButton(text = "Jetzt scannen", beiKlick = beiScannen)
    }

    if (state.bilder == 0) {
        Luft(Mass.Gross)
        Hinweis(text = "Noch keine Bilder gewählt.", zentriert = true)
        return
    }

    Luft(Mass.Gross)
    Abschnitt(text = "${state.bilder} Bilder")

    // "Erste Seite ist Seite 81" - alle weiteren zaehlen von da hoch.
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Erste Seite ist Seite",
            style = MaterialTheme.typography.bodyLarge,
            color = Elfenbein,
            modifier = Modifier.weight(1f),
        )
        Zahlenfeld(wert = state.ersteSeite, beiAenderung = beiErsteSeite)
    }

    Luft(Mass.Mittel)
    Vorschauraster(bilder = state.bilder, ersteSeite = state.ersteSeite)
}

/** Ein kleines Zahlenfeld mit Minus und Plus, ohne Tastatur. */
@Composable
private fun Zahlenfeld(wert: Int, beiAenderung: (Int) -> Unit) {
    val farben = RelockTheme.farben

    Row(
        Modifier
            .background(farben.flaeche, Radius.Eingabe)
            .border(1.dp, farben.linie, Radius.Eingabe),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "−",
            style = MaterialTheme.typography.titleMedium,
            color = farben.matt,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(42.dp)
                .leiseKlickbar { beiAenderung((wert - 1).coerceAtLeast(1)) }
                .padding(vertical = 12.dp),
        )
        Text(
            text = wert.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Elfenbein,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(52.dp),
        )
        Text(
            text = "+",
            style = MaterialTheme.typography.titleMedium,
            color = farben.matt,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(42.dp)
                .leiseKlickbar { beiAenderung(wert + 1) }
                .padding(vertical = 12.dp),
        )
    }
}

/** Die gewaehlten Bilder als Raster, jedes mit seiner Seitenzahl. */
@Composable
private fun Vorschauraster(bilder: Int, ersteSeite: Int) {
    val farben = RelockTheme.farben
    val gezeigt = minOf(bilder, 12)

    Column(Modifier.padding(horizontal = Mass.Rand)) {
        (0 until gezeigt).chunked(4).forEach { reihe ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                reihe.forEach { index ->
                    Column(Modifier.weight(1f)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.72f)
                                .background(farben.flaecheHoch, Radius.Chip)
                                .border(1.dp, farben.linie, Radius.Chip)
                                .seitenPlatzhalter(farben.leise, dicht = true),
                        )
                        Luft(4.dp)
                        Text(
                            text = "S. ${ersteSeite + index}",
                            style = MaterialTheme.typography.labelSmall,
                            color = farben.matt,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                repeat(4 - reihe.size) { Box(Modifier.weight(1f)) }
            }
        }

        if (bilder > gezeigt) {
            Luft(Mass.Klein)
            Text(
                text = "und ${bilder - gezeigt} weitere",
                style = MaterialTheme.typography.labelSmall,
                color = farben.leise,
            )
        }
    }
}

// -------------------------------------------------- Schritt 4: Hochladen

@Composable
private fun SchrittHochladen(state: ImportUiState) {
    val farben = RelockTheme.farben
    val fertig = state.hochgeladen >= state.gesamt

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
    ) {
        Text(
            text = "${state.hochgeladen} von ${state.gesamt} hochgeladen",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )
        Luft(Mass.Mittel)
        Fortschrittsstrich(
            anteil = state.hochgeladen.toFloat() / state.gesamt.coerceAtLeast(1),
        )
        Luft(Mass.Mittel)
        Text(
            text = if (fertig) {
                "Alles oben. Der Server fängt gleich an."
            } else {
                "Lass das Handy im WLAN, dann läuft es von allein durch."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = farben.matt,
        )
    }
}

// ----------------------------------------------- Schritt 5: Warteschlange

@Composable
private fun SchrittWarteschlange(state: ImportUiState) {
    val farben = RelockTheme.farben

    Box(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Der Server erkennt deine Seiten. Bei einem ganzen Buch kann " +
                "das über Nacht dauern.",
            style = MaterialTheme.typography.bodyLarge,
            color = Elfenbein,
        )
    }

    Luft(Mass.Gross)
    Trennlinie()
    state.warteschlange.forEach { scan ->
        Zeile {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Seite ${scan.seite}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                    modifier = Modifier.weight(1f),
                )
                Statuspunkt(
                    farbe = when (scan.stand) {
                        Scanstand.Fertig -> Richtig
                        Scanstand.Laeuft -> Messing
                        Scanstand.Unscharf -> Signal
                        Scanstand.Wartet -> farben.leise
                    },
                    gefuellt = scan.stand != Scanstand.Wartet,
                )
                LuftBreit(8.dp)
                Text(
                    text = scan.stand.anzeige,
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
        }
    }
}

// -------------------------------------------------- Schritt 6: Pruefen

@Composable
private fun SchrittPruefen(
    state: ImportUiState,
    beiUebernehmen: (Int) -> Unit,
    beiKorrigieren: (Int) -> Unit,
) {
    val farben = RelockTheme.farben

    if (state.abweichungen.isNotEmpty()) {
        // Abweichungen zuerst, weil nur sie eine Entscheidung brauchen.
        Abschnitt(text = "Abweichungen")
        state.abweichungen.forEach { abweichung ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .width(52.dp)
                            .height(70.dp)
                            .background(farben.flaecheHoch, Radius.Chip)
                            .border(1.dp, Signal, Radius.Chip)
                            .seitenPlatzhalter(farben.leise, dicht = true),
                    )
                    LuftBreit(14.dp)
                    Text(
                        text = "Bild ${abweichung.bild} zeigt Seite " +
                            "${abweichung.erkannt}, erwartet ${abweichung.erwartet}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Elfenbein,
                        modifier = Modifier.weight(1f),
                    )
                }
                Luft(Mass.Klein)
                Row {
                    Text(
                        text = "Übernehmen",
                        style = MaterialTheme.typography.labelLarge,
                        color = Messing,
                        modifier = Modifier
                            .leiseKlickbar { beiUebernehmen(abweichung.bild) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                    )
                    LuftBreit(Mass.Gross)
                    Text(
                        text = "Korrigieren",
                        style = MaterialTheme.typography.labelLarge,
                        color = farben.matt,
                        modifier = Modifier
                            .leiseKlickbar { beiKorrigieren(abweichung.bild) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                    )
                }
            }
            Trennlinie()
        }
        Luft(Mass.Gross)
    }

    Abschnitt(text = "Stichprobe")
    state.stichprobe.forEach { seite ->
        val aufgaben = 4 + seite % 5
        Zeile {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .width(40.dp)
                        .height(54.dp)
                        .background(farben.flaecheHoch, Radius.Chip)
                        .border(1.dp, farben.linie, Radius.Chip)
                        .seitenPlatzhalter(farben.leise, dicht = true),
                )
                LuftBreit(14.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Seite $seite",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    Luft(2.dp)
                    Text(
                        text = "$aufgaben Aufgaben erkannt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = farben.matt,
                    )
                }
            }
        }
    }
}

/** Der Abbruch steht unten, weil ein Import lange dauert. */
@Composable
fun ImportAbbrechen(beiAbbrechen: () -> Unit) {
    QuietButton(text = "Import abbrechen", beiKlick = beiAbbrechen)
}
