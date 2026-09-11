package de.kopfgeld.app.feature.session

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Korrekturposten
import de.kopfgeld.app.data.Urteil
import de.kopfgeld.app.ui.components.CorrectionMark
import de.kopfgeld.app.ui.components.CorrectionNote
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Kaestchen
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.VerdictButtons
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Verbesserung.
 *
 * Abgeleitet aus DESIGN.md 2.1 und dem Komponentenkatalog (VerdictButtons,
 * CorrectionMark, CorrectionNote). Im uebermittelten Auftragstext fehlt der
 * eigene Abschnitt dazu, der Ablauf steht aber fest:
 *
 *   "pro Aufgabe richtig / Fehler / Luecke, man bestaetigt oder widerspricht.
 *    Fehler gehen ins Brain und werden zu Karteikarten"
 */
data class VerbesserungUiState(
    val titel: String = "Mathe, Zyklus 1",
    val posten: List<Korrekturposten> = FakeData.korrekturposten,
    val index: Int = 0,
    val urteile: Map<String, Urteil> = emptyMap(),
    val zuKarte: Set<String> = FakeData.korrekturposten
        .filter { it.wirdZuKarte }
        .map { it.id }
        .toSet(),
) {
    val aktueller: Korrekturposten? get() = posten.getOrNull(index)
    val istLetzter: Boolean get() = index >= posten.lastIndex

    fun urteilFuer(posten: Korrekturposten): Urteil =
        urteile[posten.id] ?: posten.vorschlag

    val anzahlFehler: Int
        get() = posten.count { urteilFuer(it) != Urteil.Richtig }
}

@Composable
fun VerbesserungScreen(
    state: VerbesserungUiState,
    beiZurueck: () -> Unit,
    beiUrteil: (String, Urteil) -> Unit,
    beiKarteUmschalten: (String) -> Unit,
    beiWeiter: () -> Unit,
    beiSpaeter: () -> Unit,
    beiAbschliessen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val posten = state.aktueller

    HeftScaffold(
        titel = "Verbesserung",
        unterzeile = if (posten == null) {
            state.titel
        } else {
            "${state.titel} · Aufgabe ${state.index + 1} von ${state.posten.size}"
        },
        zurueck = beiZurueck,
        karo = true,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(
                    text = if (state.istLetzter) {
                        "Fehler ins Brain übernehmen"
                    } else {
                        "Weiter"
                    },
                    beiKlick = if (state.istLetzter) beiAbschliessen else beiWeiter,
                )
                TextAktion(text = "Später verbessern", beiKlick = beiSpaeter)
            }
        },
    ) {
        if (posten == null) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Mass.Seitenrand),
            ) {
                Hinweistext("Nichts zu verbessern.")
            }
            return@HeftScaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Mass.Seitenrand),
        ) {
            Luft(Mass.Klein)

            // Der gescannte Zettel mit den roten Markierungen.
            CorrectionMark(markierungen = posten.markierungen)

            Luft(Mass.Mittel)
            Text(
                text = posten.quellenangabe,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = posten.aufgabentext,
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
            )

            Luft(Mass.Mittel)
            // Randnotiz der KI. Einzige Stelle mit Caveat.
            CorrectionNote(text = posten.notiz)

            Luft(Mass.Gross)
            VerdictButtons(
                gewaehlt = state.urteilFuer(posten),
                vorschlag = posten.vorschlag,
                beiWahl = { urteil -> beiUrteil(posten.id, urteil) },
            )

            if (state.urteilFuer(posten) != Urteil.Richtig) {
                Luft(Mass.Mittel)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(stifte.korrekturHauch, Radius.Flaeche)
                        .clickable { beiKarteUmschalten(posten.id) }
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Kaestchen(
                        angehakt = posten.id in state.zuKarte,
                        beiKlick = { beiKarteUmschalten(posten.id) },
                        farbe = stifte.korrektur,
                    )
                    LuftBreit(4.dp)
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Daraus eine Karteikarte machen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.korrektur,
                        )
                        Text(
                            text = "Landet im Brain und kommt morgen dran",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.korrektur.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            Luft(Mass.Gross)
            Fortschrittspunkte(
                gesamt = state.posten.size,
                aktuell = state.index,
            )
            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun Fortschrittspunkte(gesamt: Int, aktuell: Int) {
    val stifte = KopfgeldTheme.stifte
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(gesamt) { index ->
            Box(
                Modifier
                    .size(width = 20.dp, height = 3.dp)
                    .background(
                        when {
                            index < aktuell -> stifte.tinte
                            index == aktuell -> stifte.tinte
                            else -> stifte.karo
                        },
                    ),
            )
            LuftBreit(4.dp)
        }
    }
}
