package de.kopfgeld.app.feature.karteikarten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Karteikarte
import de.kopfgeld.app.data.Wiederholungswahl
import de.kopfgeld.app.ui.components.Flashcard
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.ReviewButtons
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Karteikarten wiederholen.
 *
 * Abgeleitet aus DESIGN.md 5 (Flashcard, ReviewButtons) und 7.3
 * ("Karteikarten wiederholen" mit "38 faellig"). Die Intervalle sind in
 * Phase 1 feste Texte, FSRS kommt spaeter.
 */
data class KarteikartenUiState(
    val karten: List<Karteikarte> = FakeData.faelligeKarteikarten,
    val index: Int = 0,
    val umgedreht: Boolean = false,
    val erledigt: Int = 0,
    val gesamtFaellig: Int = FakeData.faelligeKarten,
    val wahlen: List<Wiederholungswahl> = FakeData.wiederholungswahlen,
) {
    val aktuelle: Karteikarte? get() = karten.getOrNull(index)
    val fertig: Boolean get() = aktuelle == null
}

@Composable
fun KarteikartenScreen(
    state: KarteikartenUiState,
    beiZurueck: () -> Unit,
    beiUmdrehen: () -> Unit,
    beiBewertung: (Wiederholungswahl) -> Unit,
    beiFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val karte = state.aktuelle

    HeftScaffold(
        titel = "Karteikarten",
        unterzeile = if (karte == null) {
            null
        } else {
            "${state.erledigt + 1} von ${state.gesamtFaellig} fällig"
        },
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        if (karte == null) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = Mass.Seitenrand),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Alle fälligen Karten durch.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Luft(Mass.Mittel)
                Hinweistext(
                    text = "${state.erledigt} Karten wiederholt. Die nächsten kommen, " +
                        "wenn sie dran sind.",
                    zentriert = true,
                )
                Luft(Mass.Gross)
                PrimaerButton(text = "Fertig", beiKlick = beiFertig)
            }
            return@HeftScaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Mass.Seitenrand),
        ) {
            Fortschrittsbalken(
                erledigt = state.erledigt,
                gesamt = state.gesamtFaellig,
            )

            Luft(Mass.Mittel)
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectBadge(FakeData.fach(karte.fachId), groesse = 26.dp)
                LuftBreit(10.dp)
                Text(
                    text = FakeData.thema(karte.themaId).name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }

            Luft(Mass.Mittel)
            Flashcard(
                karte = karte,
                umgedreht = state.umgedreht,
                beiUmdrehen = beiUmdrehen,
            )

            Box(Modifier.weight(1f))

            if (state.umgedreht) {
                ReviewButtons(wahlen = state.wahlen, beiWahl = beiBewertung)
            } else {
                Hinweistext(
                    text = "Erst selbst antworten, dann umdrehen.",
                    zentriert = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Luft(Mass.Mittel)
        }
    }
}

@Composable
private fun Fortschrittsbalken(erledigt: Int, gesamt: Int) {
    val stifte = KopfgeldTheme.stifte
    val anteil = if (gesamt <= 0) 0f else erledigt.toFloat() / gesamt.toFloat()
    Box(
        Modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(stifte.karo),
    ) {
        Box(
            Modifier
                .fillMaxWidth(anteil.coerceIn(0f, 1f))
                .height(3.dp)
                .background(stifte.tinte),
        )
    }
}
