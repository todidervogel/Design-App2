package de.relock.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.relock.app.data.Korrekturposten
import de.relock.app.data.Markierung
import de.relock.app.data.Serverzustand
import de.relock.app.data.Sicherheit
import de.relock.app.data.Urteil
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.Markierungskringel
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.ServerStatus
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.VerdictSelector
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.components.seitenPlatzhalter
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

/** Wie weit die Analyse ist, DESIGN.md 6.14. */
sealed interface Analysezustand {
    data class Laeuft(val etwaMinuten: Int) : Analysezustand
    data object Offline : Analysezustand
    data object Fertig : Analysezustand
}

/**
 * Verbesserung, DESIGN.md 6.14.
 *
 * Der Server ist langsam, deshalb hat dieser Screen drei Zustaende: die
 * Analyse laeuft noch, der Server ist offline, oder alles ist da.
 */
data class VerbesserungUiState(
    val analyse: Analysezustand = Analysezustand.Fertig,
    val server: Serverzustand = FakeData.serverVerbunden,
    val posten: List<Korrekturposten> = FakeData.korrektur,
    val markierungen: List<Markierung> = FakeData.markierungen,
    val urteile: Map<String, Urteil> = emptyMap(),
    val seite: Int = 1,
    val seitenGesamt: Int = 2,
) {
    fun urteilFuer(einPosten: Korrekturposten): Urteil? =
        urteile[einPosten.id] ?: if (analyse == Analysezustand.Fertig) einPosten.vorschlag else null

    val allesBewertet: Boolean get() = posten.all { urteilFuer(it) != null }

    val bilanz: String
        get() {
            val richtig = posten.count { urteilFuer(it) == Urteil.Richtig }
            val fehler = posten.count { urteilFuer(it) == Urteil.Fehler }
            val luecken = posten.count { urteilFuer(it) == Urteil.Luecke }
            return "$richtig richtig · $fehler Fehler · $luecken Lücke" +
                if (luecken == 1) "" else "n"
        }
}

@Composable
fun VerbesserungScreen(
    state: VerbesserungUiState,
    beiZurueck: () -> Unit,
    beiSeite: (Int) -> Unit,
    beiUrteil: (String, Urteil) -> Unit,
    beiAntwortBearbeiten: (String) -> Unit,
    beiSpaeter: () -> Unit,
    beiSelbstKorrigieren: () -> Unit,
    beiSpeichern: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Verbesserung",
        unterzeile = if (state.analyse == Analysezustand.Fertig) state.bilanz else null,
        zurueck = beiZurueck,
        modifier = modifier,
        fussleiste = {
            when (state.analyse) {
                is Analysezustand.Laeuft, Analysezustand.Offline -> Column {
                    SecondaryButton(text = "Selbst korrigieren", beiKlick = beiSelbstKorrigieren)
                    QuietButton(text = "Später", beiKlick = beiSpaeter)
                }

                Analysezustand.Fertig -> PrimaryButton(
                    text = "Speichern",
                    beiKlick = beiSpeichern,
                    aktiv = state.allesBewertet,
                )
            }
        },
    ) {
        // Der gescannte Zettel mit den nummerierten Markierungen.
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Scanblatt(markierungen = state.markierungen)
        }

        if (state.seitenGesamt > 1) {
            Luft(Mass.Klein)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(state.seitenGesamt) { index ->
                    val ist = index + 1 == state.seite
                    Box(
                        Modifier
                            .width(if (ist) 18.dp else 6.dp)
                            .height(3.dp)
                            .background(
                                if (ist) Messing else farben.linie,
                                Radius.Chip,
                            )
                            .leiseKlickbar { beiSeite(index + 1) },
                    )
                    if (index < state.seitenGesamt - 1) LuftBreit(5.dp)
                }
            }
        }

        when (val analyse = state.analyse) {
            is Analysezustand.Laeuft -> {
                Luft(Mass.Weit)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Mass.Rand),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Der Server braucht noch etwa ${analyse.etwaMinuten} Minuten",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                        textAlign = TextAlign.Center,
                    )
                    Luft(Mass.Mittel)
                    ServerStatus(state.server)
                }
            }

            Analysezustand.Offline -> {
                Luft(Mass.Weit)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Mass.Rand),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Dein PC ist gerade nicht erreichbar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                        textAlign = TextAlign.Center,
                    )
                    Luft(Mass.Klein)
                    Hinweis(
                        text = "Die Scans warten. Sobald er wieder da ist, geht es " +
                            "von allein weiter.",
                        zentriert = true,
                    )
                    Luft(Mass.Mittel)
                    ServerStatus(state.server)
                }
            }

            Analysezustand.Fertig -> {
                Luft(Mass.Gross)
                state.posten.forEach { posten ->
                    Korrekturblock(
                        posten = posten,
                        urteil = state.urteilFuer(posten),
                        beiUrteil = { urteil -> beiUrteil(posten.id, urteil) },
                        beiAntwortBearbeiten = { beiAntwortBearbeiten(posten.id) },
                    )
                }
            }
        }

        Luft(Mass.Gross)
    }
}

/** Das Scan-Blatt mit den nummerierten Kringeln darauf. */
@Composable
private fun Scanblatt(markierungen: List<Markierung>) {
    val farben = RelockTheme.farben

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(farben.flaecheHoch, Radius.Flaeche)
            .border(1.dp, farben.linie, Radius.Flaeche)
            .seitenPlatzhalter(farben.leise, dicht = true),
    ) {
        val breite = maxWidth
        val hoehe = maxHeight
        markierungen.forEach { markierung ->
            Markierungskringel(
                nummer = markierung.nummer,
                richtig = markierung.richtig,
                modifier = Modifier.offset(
                    x = breite * markierung.xAnteil,
                    y = hoehe * markierung.yAnteil,
                ),
            )
        }
    }
}

/** Ein Block je Aufgabe: Antwort, Kommentar, Loesung, Urteil. */
@Composable
private fun Korrekturblock(
    posten: Korrekturposten,
    urteil: Urteil?,
    beiUrteil: (Urteil) -> Unit,
    beiAntwortBearbeiten: () -> Unit,
) {
    val farben = RelockTheme.farben
    var loesungOffen by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = posten.nummer,
                style = MaterialTheme.typography.titleMedium,
                color = Elfenbein,
            )
            LuftBreit(10.dp)
            Text(
                text = posten.quelle,
                style = MaterialTheme.typography.labelSmall,
                color = farben.matt,
            )
        }

        Luft(Mass.Klein)
        Text(
            text = "Deine Antwort",
            style = MaterialTheme.typography.labelSmall,
            color = farben.leise,
        )
        Luft(4.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .background(farben.flaecheHoch, Radius.Eingabe)
                .leiseKlickbar(beiAntwortBearbeiten)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = posten.deineAntwort.ifBlank { "— nichts geschrieben —" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (posten.deineAntwort.isBlank()) farben.leise else Elfenbein,
            )
        }

        Luft(Mass.Klein)
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = posten.kommentar,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
                modifier = Modifier.weight(1f),
            )
            LuftBreit(10.dp)
            Text(
                text = posten.sicherheit.anzeige,
                style = MaterialTheme.typography.labelSmall,
                color = if (posten.sicherheit == Sicherheit.Unsicher) Signal else farben.leise,
            )
        }

        if (posten.loesung != null) {
            Luft(Mass.Klein)
            Text(
                text = if (loesungOffen) "Lösung verbergen" else "Lösung anzeigen",
                style = MaterialTheme.typography.labelLarge,
                color = Messing,
                modifier = Modifier.leiseKlickbar { loesungOffen = !loesungOffen },
            )
            if (loesungOffen) {
                Luft(6.dp)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(de.relock.app.ui.theme.RichtigTief, Radius.Eingabe)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = posten.loesung,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Richtig,
                    )
                }
            }
        }

        Luft(Mass.Mittel)
        VerdictSelector(
            gewaehlt = urteil,
            vorschlag = posten.vorschlag,
            beiWahl = beiUrteil,
        )

        Luft(Mass.Gross)
        Trennlinie()
        Luft(Mass.Gross)
    }
}
