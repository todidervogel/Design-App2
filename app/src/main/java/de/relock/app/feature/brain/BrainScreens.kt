package de.relock.app.feature.brain

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.data.Arbeitsblatt
import de.relock.app.data.Aufgabe
import de.relock.app.data.Buch
import de.relock.app.data.Buchseite
import de.relock.app.data.Seitenstand
import de.relock.app.data.Serverzustand
import de.relock.app.data.fake.FakeData
import de.relock.app.data.zahl
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.Chip
import de.relock.app.ui.components.Fortschrittsstrich
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PageGrid
import de.relock.app.ui.components.PageGridLegende
import de.relock.app.ui.components.Reiter
import de.relock.app.ui.components.ServerStatus
import de.relock.app.ui.components.SubjectMark
import de.relock.app.ui.components.SymbolPlus
import de.relock.app.ui.components.SymbolWeiter
import de.relock.app.ui.components.Trennlinie
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

// ================================================================= 6.18 Brain

/**
 * Brain-Tab, DESIGN.md 6.18.
 * Drei Reiter: Buecher, Arbeitsblaetter, Faecher.
 */
data class BrainUiState(
    val reiter: Int = 0,
    val server: Serverzustand = FakeData.serverVerbunden,
    val buecher: List<Buch> = FakeData.buecher,
    val arbeitsblaetter: List<Arbeitsblatt> = FakeData.arbeitsblaetter,
)

@Composable
fun BrainScreen(
    state: BrainUiState,
    beiKatalog: () -> Unit,
    beiReiter: (Int) -> Unit,
    beiImport: () -> Unit,
    beiBuch: (Buch) -> Unit,
    beiArbeitsblatt: (Arbeitsblatt) -> Unit,
    beiFach: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Brain",
        aufTitelLangGedrueckt = beiKatalog,
        modifier = modifier,
        aktion = {
            Row(
                Modifier.leiseKlickbar(beiImport),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SymbolPlus()
                LuftBreit(4.dp)
                Text(
                    text = "Importieren",
                    style = MaterialTheme.typography.labelLarge,
                    color = Messing,
                )
            }
        },
    ) {
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            ServerStatus(state.server)
        }

        Luft(Mass.Mittel)
        Reiter(
            beschriftungen = listOf("Bücher", "Arbeitsblätter", "Fächer"),
            gewaehlt = state.reiter,
            beiWahl = beiReiter,
        )
        Trennlinie()

        when (state.reiter) {
            0 -> state.buecher.forEach { buch ->
                Buchzeile(buch = buch, beiKlick = { beiBuch(buch) })
            }

            1 -> {
                val nachFach = state.arbeitsblaetter.groupBy { it.fachId }
                nachFach.forEach { (fachId, blaetter) ->
                    Abschnitt(FakeData.fach(fachId).name)
                    blaetter.forEach { blatt ->
                        Zeile(beiKlick = { beiArbeitsblatt(blatt) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = blatt.titel,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Elfenbein,
                                    )
                                    Text(
                                        text = "${blatt.datum} · ${blatt.aufgaben} Aufgaben",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = farben.matt,
                                    )
                                }
                                SymbolWeiter()
                            }
                        }
                    }
                }
            }

            else -> FakeData.faecher.forEach { fach ->
                val themen = FakeData.themenVon(fach.id)
                val fehler = themen.sumOf { it.fehler }
                Zeile(beiKlick = { beiFach(fach.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SubjectMark(fach = fach, groesse = 30.dp)
                        LuftBreit(14.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = fach.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Elfenbein,
                            )
                            Text(
                                text = if (themen.isEmpty()) {
                                    "noch keine Themen"
                                } else {
                                    themen.joinToString(", ") { it.name }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = farben.matt,
                                maxLines = 1,
                            )
                        }
                        if (fehler > 0) {
                            LuftBreit(10.dp)
                            Text(
                                text = "$fehler Fehler",
                                style = MaterialTheme.typography.labelSmall,
                                color = Signal,
                            )
                        }
                    }
                }
            }
        }

        Luft(Mass.Gross)
    }
}

@Composable
private fun Buchzeile(buch: Buch, beiKlick: () -> Unit) {
    val farben = RelockTheme.farben
    val fach = FakeData.fach(buch.fachId)
    val anteil = if (buch.seitenGesamt <= 0) {
        0f
    } else {
        buch.seitenErfasst.toFloat() / buch.seitenGesamt.toFloat()
    }

    Zeile(beiKlick = beiKlick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubjectMark(fach = fach, groesse = 30.dp)
            LuftBreit(14.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    text = buch.titel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                )
                Text(
                    text = "${buch.seitenErfasst} von ${buch.seitenGesamt} Seiten · " +
                        "${zahl(buch.aufgaben)} Aufgaben",
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
                Luft(8.dp)
                Fortschrittsstrich(anteil)
            }
            LuftBreit(10.dp)
            SymbolWeiter()
        }
    }
}

// ============================================================ 6.19 Buch-Detail

enum class Seitenfilter(val anzeige: String) {
    Alle("alle"),
    Abweichungen("Abweichungen"),
    OhneAufgaben("ohne Aufgaben"),
    Fehlend("fehlend"),
}

data class BuchDetailUiState(
    val buch: Buch = FakeData.buecher.first(),
    val filter: Seitenfilter = Seitenfilter.Alle,
) {
    val sichtbar: List<Buchseite>
        get() = when (filter) {
            Seitenfilter.Alle -> buch.seiten
            Seitenfilter.Abweichungen -> buch.seiten.filter { it.stand == Seitenstand.Abweichung }
            Seitenfilter.OhneAufgaben -> buch.seiten.filter { it.aufgaben == 0 }
            Seitenfilter.Fehlend -> buch.seiten.filter { it.stand == Seitenstand.Fehlt }
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuchDetailScreen(
    state: BuchDetailUiState,
    beiZurueck: () -> Unit,
    beiFilter: (Seitenfilter) -> Unit,
    beiSeite: (Buchseite) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buch = state.buch
    val fach = FakeData.fach(buch.fachId)

    Geruest(
        titel = buch.titel,
        unterzeile = "${fach.name} · ${buch.seitenErfasst} von ${buch.seitenGesamt} " +
            "Seiten · ${zahl(buch.aufgaben)} Aufgaben",
        zurueck = beiZurueck,
        modifier = modifier,
    ) {
        Luft(Mass.Klein)
        FlowRow(
            modifier = Modifier.padding(horizontal = Mass.Rand),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Seitenfilter.entries.forEach { filter ->
                Chip(
                    text = filter.anzeige,
                    gewaehlt = filter == state.filter,
                    beiKlick = { beiFilter(filter) },
                )
            }
        }

        Luft(Mass.Gross)
        if (state.sichtbar.isEmpty()) {
            Hinweis(
                text = "Keine Seite passt zu diesem Filter.",
                modifier = Modifier.padding(horizontal = Mass.Rand),
            )
        } else {
            Box(Modifier.padding(horizontal = Mass.Rand)) {
                PageGrid(seiten = state.sichtbar, beiSeite = beiSeite)
            }
        }

        Luft(Mass.Gross)
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            PageGridLegende()
        }
        Luft(Mass.Gross)
    }
}

// ========================================================== 6.20 Seiten-Detail

data class SeitenDetailUiState(
    val buchTitel: String = FakeData.buecher.first().titel,
    val seite: Buchseite = Buchseite(84, Seitenstand.Erkannt, 4),
    val aufgaben: List<Aufgabe> = FakeData.aufgaben.take(4),
    val loesungOffen: Set<String> = emptySet(),
)

@Composable
fun SeitenDetailScreen(
    state: SeitenDetailUiState,
    beiZurueck: () -> Unit,
    beiLoesung: (String) -> Unit,
    beiSeitenzahlAendern: () -> Unit,
    beiNeuErkennen: () -> Unit,
    beiSeiteErsetzen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Seite ${state.seite.nummer}",
        unterzeile = state.buchTitel,
        zurueck = beiZurueck,
        modifier = modifier,
    ) {
        // Seitenbild. In Version 1 ein Platzhalter, spaeter zoombar.
        Box(
            Modifier
                .padding(horizontal = Mass.Rand)
                .fillMaxWidth()
                .height(300.dp)
                .background(farben.flaecheHoch, Radius.Flaeche)
                .border(1.dp, farben.linie, Radius.Flaeche)
                .seitenPlatzhalter(farben.leise, dicht = true),
        )

        Abschnitt("Erkannte Aufgaben")
        state.aufgaben.forEach { aufgabe ->
            Zeile {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = aufgabe.nummer,
                            style = MaterialTheme.typography.titleSmall,
                            color = Elfenbein,
                        )
                        LuftBreit(10.dp)
                        Text(
                            text = aufgabe.thema,
                            style = MaterialTheme.typography.labelSmall,
                            color = farben.matt,
                        )
                    }
                    Luft(4.dp)
                    Text(
                        text = aufgabe.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = farben.matt,
                    )
                    if (aufgabe.loesung != null) {
                        Luft(8.dp)
                        val offen = aufgabe.id in state.loesungOffen
                        Text(
                            text = if (offen) "Lösung verbergen" else "Lösung anzeigen",
                            style = MaterialTheme.typography.labelLarge,
                            color = Messing,
                            modifier = Modifier.leiseKlickbar { beiLoesung(aufgabe.id) },
                        )
                        if (offen) {
                            Luft(6.dp)
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        de.relock.app.ui.theme.RichtigTief,
                                        Radius.Eingabe,
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                            ) {
                                Text(
                                    text = aufgabe.loesung,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Richtig,
                                )
                            }
                        }
                    }
                }
            }
        }

        Abschnitt("Seite bearbeiten")
        Zeile(beiKlick = beiSeitenzahlAendern) {
            Aktionszeile("Seitenzahl ändern")
        }
        Zeile(beiKlick = beiNeuErkennen) {
            Aktionszeile("Neu erkennen")
        }
        Zeile(beiKlick = beiSeiteErsetzen) {
            Aktionszeile("Seite ersetzen")
        }

        Luft(Mass.Gross)
    }
}

@Composable
private fun Aktionszeile(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Elfenbein,
            modifier = Modifier.weight(1f),
        )
        SymbolWeiter()
    }
}
