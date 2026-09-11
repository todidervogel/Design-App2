package de.relock.app.feature.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.data.Faelligkeit
import de.relock.app.data.Karte
import de.relock.app.data.Seitenbezug
import de.relock.app.data.dauer
import de.relock.app.data.fake.FakeData
import de.relock.app.feature.onboarding.Eingabe
import de.relock.app.feature.onboarding.Schalter
import de.relock.app.ui.components.Chip
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.RelockCard
import de.relock.app.ui.components.Segmente
import de.relock.app.ui.components.SubjectMark
import de.relock.app.ui.components.seitenPlatzhalter
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme

// =============================================================== 6.3 Freischalten

/**
 * Freischalten, DESIGN.md 6.3.
 * In der Nachsperre ist der Button aus und sagt, wie lange noch.
 */
data class FreischaltenUiState(
    val karte: Karte = FakeData.karte,
    val moeglichkeiten: List<Int> = listOf(5, 10, 15),
    val gewaehlt: Int = 1,
    val nachsperreRestMinuten: Int = 0,
) {
    val minuten: Int get() = moeglichkeiten.getOrElse(gewaehlt) { 10 }
    val guthabenDanach: Int get() = (karte.guthabenMinuten - minuten).coerceAtLeast(0)
    val gesperrt: Boolean get() = nachsperreRestMinuten > 0
    val zuWenig: Boolean get() = minuten > karte.guthabenMinuten
}

@Composable
fun FreischaltenInhalt(
    state: FreischaltenUiState,
    beiWahl: (Int) -> Unit,
    beiFreischalten: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Column(modifier.fillMaxWidth().padding(horizontal = Mass.Rand)) {
        // Karte klein oben.
        Box(Modifier.fillMaxWidth(0.62f)) {
            RelockCard(karte = state.karte)
        }

        Luft(Mass.Gross)
        Text(
            text = "Wie lange willst du freischalten?",
            style = MaterialTheme.typography.titleMedium,
            color = Elfenbein,
        )

        Luft(Mass.Mittel)
        Segmente(
            beschriftungen = state.moeglichkeiten.map { "$it Min" },
            gewaehlt = state.gewaehlt,
            beiWahl = beiWahl,
            aktiv = !state.gesperrt,
        )

        Luft(Mass.Mittel)
        Hinweis(
            text = if (state.zuWenig) {
                "Dafür reicht dein Guthaben nicht."
            } else {
                "Guthaben danach: ${dauer(state.guthabenDanach)}"
            },
            farbe = if (state.zuWenig) MaterialTheme.colorScheme.error else farben.matt,
        )

        Luft(Mass.Gross)
        PrimaryButton(
            text = if (state.gesperrt) {
                "Noch ${state.nachsperreRestMinuten} Min Nachsperre"
            } else {
                "${state.minuten} Min freischalten"
            },
            beiKlick = beiFreischalten,
            aktiv = !state.gesperrt && !state.zuWenig,
        )
    }
}

// ========================================================= 6.4 Hausaufgabe anlegen

/**
 * Hausaufgabe anlegen, DESIGN.md 6.4.
 *
 * Der interessante Teil ist die Zeile in der Mitte: waehrend man tippt,
 * sucht Relock die passende Buchseite im Brain und bietet an, sie zu
 * verknuepfen. Findet es nichts, fuehrt der Weg direkt in den Import.
 */
data class HausaufgabeAnlegenUiState(
    val fachId: String = "m",
    val aufgabe: String = "",
    val bezug: Seitenbezug? = null,
    val verknuepfen: Boolean = true,
    val faellig: Faelligkeit = Faelligkeit.Morgen,
    val hatFoto: Boolean = false,
) {
    val moeglicheTermine = listOf(
        Faelligkeit.Morgen,
        Faelligkeit.Uebermorgen,
        Faelligkeit.NaechsteWoche,
    )
    val speicherbar: Boolean get() = aufgabe.isNotBlank()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HausaufgabeAnlegenInhalt(
    state: HausaufgabeAnlegenUiState,
    beiFach: (String) -> Unit,
    beiAufgabe: (String) -> Unit,
    beiVerknuepfen: (Boolean) -> Unit,
    beiSeiteImportieren: () -> Unit,
    beiFaellig: (Faelligkeit) -> Unit,
    beiFoto: () -> Unit,
    beiDatumWaehlen: () -> Unit,
    beiSpeichern: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Column(modifier.fillMaxWidth()) {
        // Fach als waagerecht scrollbare Kuerzel.
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Mass.Rand),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FakeData.faecher.forEach { fach ->
                val gewaehlt = fach.id == state.fachId
                Box(
                    Modifier
                        .background(
                            if (gewaehlt) farben.messingTief else androidx.compose.ui.graphics.Color.Transparent,
                            Radius.Chip,
                        ),
                ) {
                    Box(
                        Modifier.padding(4.dp),
                    ) {
                        SubjectMark(
                            fach = fach,
                            groesse = 40.dp,
                            farbe = if (gewaehlt) Messing else farben.matt,
                            modifier = Modifier.leiseKlickbar { beiFach(fach.id) },
                        )
                    }
                }
            }
        }

        Luft(Mass.Gross)
        Column(Modifier.padding(horizontal = Mass.Rand)) {
            Eingabe(
                wert = state.aufgabe,
                beiAenderung = beiAufgabe,
                beschriftung = "Aufgabe",
                platzhalter = "z. B. Buch S. 84, Nr. 3a–d",
            )

            Luft(Mass.Mittel)
            Verknuepfungszeile(
                bezug = state.bezug,
                verknuepfen = state.verknuepfen,
                beiVerknuepfen = beiVerknuepfen,
                beiSeiteImportieren = beiSeiteImportieren,
            )

            Luft(Mass.Gross)
            Text(
                text = "Abgabe",
                style = MaterialTheme.typography.titleMedium,
                color = Elfenbein,
            )
            Luft(Mass.Klein)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.moeglicheTermine.forEach { termin ->
                    Chip(
                        text = when (termin) {
                            Faelligkeit.Morgen -> "Morgen"
                            Faelligkeit.Uebermorgen -> "Übermorgen"
                            Faelligkeit.NaechsteWoche -> "Nächste Woche"
                            else -> termin.anzeige
                        },
                        gewaehlt = termin == state.faellig,
                        beiKlick = { beiFaellig(termin) },
                    )
                }
                Chip(text = "Datum wählen", gewaehlt = false, beiKlick = beiDatumWaehlen)
            }

            Luft(Mass.Gross)
            if (state.hatFoto) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .width(54.dp)
                            .height(74.dp)
                            .background(farben.flaecheHoch, Radius.Chip)
                            .seitenPlatzhalter(farben.leise),
                    )
                    LuftBreit(Mass.Mittel)
                    Text(
                        text = "Foto hinzugefügt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = farben.matt,
                    )
                }
            } else {
                QuietButton(text = "Foto hinzufügen", beiKlick = beiFoto)
            }

            Luft(Mass.Gross)
            PrimaryButton(
                text = "Hausaufgabe speichern",
                beiKlick = beiSpeichern,
                aktiv = state.speicherbar,
            )
        }
    }
}

@Composable
private fun Verknuepfungszeile(
    bezug: Seitenbezug?,
    verknuepfen: Boolean,
    beiVerknuepfen: (Boolean) -> Unit,
    beiSeiteImportieren: () -> Unit,
) {
    val farben = RelockTheme.farben

    Row(
        Modifier
            .fillMaxWidth()
            .background(farben.flaecheHoch, Radius.Eingabe)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            if (bezug == null) {
                Text(
                    text = "Seite nicht im Brain",
                    style = MaterialTheme.typography.bodyMedium,
                    color = farben.matt,
                )
            } else {
                Text(
                    text = "${bezug.buchTitel}, Seite ${bezug.seite}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Elfenbein,
                )
                Text(
                    text = "${bezug.aufgabenImBrain} Aufgaben im Brain",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
        }
        LuftBreit(12.dp)
        if (bezug == null) {
            Text(
                text = "Seite importieren",
                style = MaterialTheme.typography.labelLarge,
                color = Messing,
                modifier = Modifier.leiseKlickbar(beiSeiteImportieren),
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Schalter(an = verknuepfen, beiAenderung = beiVerknuepfen)
                Text(
                    text = "Verknüpfen",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }
        }
    }
}
