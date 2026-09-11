package de.relock.app.feature.statistik

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.relock.app.data.Fachbilanz
import de.relock.app.data.Kartenstufe
import de.relock.app.data.Schwaeche
import de.relock.app.data.Tagesbilanz
import de.relock.app.data.dauer
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Kennzahl
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.StatBar
import de.relock.app.ui.components.Statuspunkt
import de.relock.app.ui.components.SubjectMark
import de.relock.app.ui.components.TierProgress
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.WeiterZeile
import de.relock.app.ui.components.Zeile
import de.relock.app.ui.components.leiseKlickbar
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

// ============================================================== 6.22 Statistik

/**
 * Statistik, DESIGN.md 6.22.
 *
 * Zahlen ohne Vorwurf. Die Scrollzeit steht daneben, nicht darunter, und
 * sie ist gedaempft - sie soll nicht anklagen.
 */
data class StatistikUiState(
    val stufe: Kartenstufe = FakeData.karte.stufe,
    val bisNaechsterStufeStunden: Int = FakeData.BIS_SILBER_STUNDEN,
    val stufenAnteil: Float = 0.62f,
    val woche: List<Tagesbilanz> = FakeData.woche,
    val hausaufgabenPuenktlich: Int = FakeData.HAUSAUFGABEN_PUENKTLICH,
    val hausaufgabenGesamt: Int = FakeData.HAUSAUFGABEN_GESAMT,
    val fachbilanzen: List<Fachbilanz> = FakeData.fachbilanzen,
    val schwaechen: List<Schwaeche> = FakeData.schwaechen,
    val notausgangWoche: Int = FakeData.NOTAUSGANG_WOCHE,
    val sperreAusMinuten: Int = FakeData.SPERRE_AUS_MINUTEN,
) {
    val naechsteStufe: Kartenstufe?
        get() = Kartenstufe.entries.getOrNull(stufe.ordinal + 1)

    val lernMinutenWoche: Int get() = woche.sumOf { it.lernMinuten }
    val scrollMinutenWoche: Int get() = woche.sumOf { it.scrollMinuten }

    /** Der laengste Tag bestimmt, wie lang ein voller Balken ist. */
    val tagesmaximum: Int
        get() = woche.maxOfOrNull { maxOf(it.lernMinuten, it.scrollMinuten) }
            ?.coerceAtLeast(1) ?: 1

    val richtigGesamt: Int get() = fachbilanzen.sumOf { it.richtig }
    val fehlerGesamt: Int get() = fachbilanzen.sumOf { it.fehler }
    val lueckenGesamt: Int get() = fachbilanzen.sumOf { it.luecken }
}

@Composable
fun StatistikScreen(
    state: StatistikUiState,
    beiSchwaeche: (Schwaeche) -> Unit,
    beiEinstellungen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Statistik",
        modifier = modifier,
        aktion = {
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.labelLarge,
                color = farben.matt,
                modifier = Modifier
                    .leiseKlickbar(beiEinstellungen)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
            )
        },
    ) {
        // --- Kopf: wie weit bis zur naechsten Stufe -------------------------
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            TierProgress(
                text = if (state.naechsteStufe != null) {
                    "Noch ${state.bisNaechsterStufeStunden} Std. bis " +
                        "${state.naechsteStufe?.anzeige}"
                } else {
                    "Obsidian erreicht"
                },
                anteil = state.stufenAnteil,
                naechste = state.naechsteStufe,
            )
        }

        // --- Diese Woche ---------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Diese Woche")
        Column(Modifier.padding(horizontal = Mass.Rand)) {
            state.woche.forEach { tag ->
                StatBar(
                    beschriftung = tag.tag,
                    wert = if (tag.lernMinuten == 0) "—" else dauer(tag.lernMinuten),
                    anteil = tag.lernMinuten.toFloat() / state.tagesmaximum,
                    farbe = Messing,
                    zweiterAnteil = tag.scrollMinuten.toFloat() / state.tagesmaximum,
                    zweiteFarbe = Signal.copy(alpha = 0.6f),
                )
            }
            Luft(Mass.Mittel)
            Text(
                text = "${dauer(state.lernMinutenWoche)} gelernt, " +
                    "${dauer(state.scrollMinutenWoche)} gescrollt.",
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
            )
            Luft(Mass.Klein)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Legendenpunkt(farbe = Messing, text = "Lernzeit")
                LuftBreit(Mass.Mittel)
                Legendenpunkt(farbe = Signal.copy(alpha = 0.6f), text = "Scrollzeit")
            }
        }

        // --- Hausaufgaben --------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Hausaufgaben")
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Text(
                text = "${state.hausaufgabenPuenktlich} von " +
                    "${state.hausaufgabenGesamt} pünktlich",
                style = MaterialTheme.typography.headlineMedium,
                color = Elfenbein,
            )
        }

        // --- Aufgaben pro Fach ---------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Aufgaben")
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Mass.Rand),
            horizontalArrangement = Arrangement.spacedBy(Mass.Mittel),
        ) {
            Kennzahl(
                wert = state.richtigGesamt.toString(),
                beschriftung = "richtig",
                farbe = Richtig,
                modifier = Modifier.weight(1f),
            )
            Kennzahl(
                wert = state.fehlerGesamt.toString(),
                beschriftung = "Fehler",
                farbe = Signal,
                modifier = Modifier.weight(1f),
            )
            Kennzahl(
                wert = state.lueckenGesamt.toString(),
                beschriftung = "Lücken",
                modifier = Modifier.weight(1f),
            )
        }

        Luft(Mass.Mittel)
        Trennlinie()
        state.fachbilanzen.forEach { bilanz ->
            Fachzeile(bilanz)
        }

        // --- Schwaechen ----------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Schwächen")
        state.schwaechen.forEach { schwaeche ->
            WeiterZeile(
                titel = schwaeche.thema,
                unterzeile = FakeData.fach(schwaeche.fachId).name,
                wert = "${schwaeche.fehler} Fehler",
                wertfarbe = Signal,
                beiKlick = { beiSchwaeche(schwaeche) },
            )
        }
        Box(Modifier.padding(horizontal = Mass.Rand, vertical = Mass.Klein)) {
            Text(
                text = "Antippen startet eine Session mit genau diesen Aufgaben.",
                style = MaterialTheme.typography.labelSmall,
                color = farben.leise,
            )
        }

        // --- Sperre --------------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Sperre")
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Text(
                text = "Notausgang diese Woche: ${state.notausgangWoche}× · " +
                    "Sperre aus: ${state.sperreAusMinuten} Min",
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
            )
        }

        Luft(Mass.Weit)
    }
}

/** Eine Zeile pro Fach mit den drei Zahlen rechts. */
@Composable
private fun Fachzeile(bilanz: Fachbilanz) {
    val farben = RelockTheme.farben
    val fach = FakeData.fach(bilanz.fachId)

    Zeile {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubjectMark(fach = fach, groesse = 30.dp)
            LuftBreit(14.dp)
            Text(
                text = fach.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Elfenbein,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = bilanz.richtig.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Richtig,
            )
            Text(
                text = " · ",
                style = MaterialTheme.typography.bodyMedium,
                color = farben.leise,
            )
            Text(
                text = bilanz.fehler.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Signal,
            )
            Text(
                text = " · ",
                style = MaterialTheme.typography.bodyMedium,
                color = farben.leise,
            )
            Text(
                text = bilanz.luecken.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
            )
        }
    }
}

/** Kleiner Punkt mit Wort, damit die zwei Balkenfarben erklaert sind. */
@Composable
private fun Legendenpunkt(farbe: Color, text: String) {
    val farben = RelockTheme.farben

    Row(verticalAlignment = Alignment.CenterVertically) {
        Statuspunkt(farbe = farbe, groesse = 7.dp)
        LuftBreit(6.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = farben.leise,
        )
    }
}
