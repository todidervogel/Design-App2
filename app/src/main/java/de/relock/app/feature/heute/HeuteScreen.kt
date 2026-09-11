package de.relock.app.feature.heute

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.data.Hausaufgabe
import de.relock.app.data.Karte
import de.relock.app.data.Serverzustand
import de.relock.app.data.Sperrzustand
import de.relock.app.data.fake.FakeData
import de.relock.app.data.uhr
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.GoalRing
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.HomeworkRow
import de.relock.app.ui.components.LockStatusLine
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.Messingleiste
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.RelockCard
import de.relock.app.ui.components.ServerStatus
import de.relock.app.ui.components.SymbolEinstellungen
import de.relock.app.ui.components.SymbolPlus
import de.relock.app.ui.components.SymbolWeiter
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.langerDruck
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Kartenwortmarke
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme

/** Eine gerade laufende Session, DESIGN.md 6.2. */
data class LaufendeSession(
    val zyklus: Int,
    val zyklenGesamt: Int,
    val restSekunden: Int,
)

/**
 * Heute, DESIGN.md 6.2.
 *
 * Zustaende: normal, Session laeuft, keine Hausaufgaben, Server offline,
 * Update verfuegbar.
 */
data class HeuteUiState(
    val datum: String = FakeData.DATUM,
    val karte: Karte = FakeData.karte,
    val kartenRueckseite: Boolean = false,
    val sperre: Sperrzustand = FakeData.sperreGesperrt,
    val server: Serverzustand = FakeData.serverVerbunden,
    val hausaufgaben: List<Hausaufgabe> = FakeData.hausaufgaben,
    val zielHausaufgaben: Int = FakeData.ZIEL_HAUSAUFGABEN,
    val zielHausaufgabenErledigt: Int = FakeData.ZIEL_HAUSAUFGABEN_ERLEDIGT,
    val zielMinuten: Int = FakeData.ZIEL_MINUTEN,
    val zielMinutenErreicht: Int = FakeData.ZIEL_MINUTEN_ERREICHT,
    val laufendeSession: LaufendeSession? = null,
    val updateHinweis: String? = null,
) {
    val offene: List<Hausaufgabe> get() = hausaufgaben.filterNot { it.erledigt }
    val keineHausaufgaben: Boolean get() = offene.isEmpty()
}

@Composable
fun HeuteScreen(
    state: HeuteUiState,
    beiEinstellungen: () -> Unit,
    beiKatalog: () -> Unit,
    beiKarteDrehen: () -> Unit,
    beiFreischalten: () -> Unit,
    beiAlleBuchungen: () -> Unit,
    beiHausaufgabeNeu: () -> Unit,
    beiHausaufgabe: (Hausaufgabe) -> Unit,
    beiHausaufgabeErledigt: (String) -> Unit,
    beiHausaufgabeLoeschen: (String) -> Unit,
    beiHausaufgabenStarten: () -> Unit,
    beiSessionPlanen: () -> Unit,
    beiLaufendeSession: () -> Unit,
    beiUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.updateHinweis != null) {
                Messingleiste(text = state.updateHinweis, beiKlick = beiUpdate)
            }

            if (state.laufendeSession != null) {
                Sessionleiste(session = state.laufendeSession, beiKlick = beiLaufendeSession)
            }

            // Kopf
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = Mass.Rand, end = Mass.Klein, top = Mass.Mittel),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Relock",
                    style = Kartenwortmarke,
                    color = Elfenbein,
                    // Langer Druck oeffnet den Screen-Katalog, DESIGN.md 5.
                    modifier = Modifier
                        .weight(1f)
                        .langerDruck(beiKatalog),
                )
                Box(
                    Modifier
                        .size(Mass.Tippziel)
                        .clickable(onClick = beiEinstellungen),
                    contentAlignment = Alignment.Center,
                ) {
                    SymbolEinstellungen()
                }
            }
            Text(
                text = state.datum,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
                modifier = Modifier.padding(horizontal = Mass.Rand),
            )

            Luft(Mass.Gross)
            Box(Modifier.padding(horizontal = Mass.Rand)) {
                RelockCard(
                    karte = state.karte,
                    gedreht = state.kartenRueckseite,
                    buchungen = FakeData.letzteBuchungen,
                    beiTipp = beiKarteDrehen,
                    beiLangemDruck = beiFreischalten,
                    beiAlleBuchungen = beiAlleBuchungen,
                )
            }

            Luft(Mass.Mittel)
            Row(
                Modifier.padding(horizontal = Mass.Rand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LockStatusLine(state.sperre)
                LuftBreit(12.dp)
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodyMedium,
                    color = farben.linie,
                )
                LuftBreit(12.dp)
                ServerStatus(state.server)
            }

            if (state.server is Serverzustand.Offline) {
                Luft(Mass.Klein)
                Hinweis(
                    text = "Lernen, Aufgaben und Sperre gehen trotzdem. Deine Scans " +
                        "warten, bis der PC wieder da ist.",
                    modifier = Modifier.padding(horizontal = Mass.Rand),
                )
            }

            Luft(Mass.Gross)
            Trennlinie()

            // Tagesziel
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(Mass.Rand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GoalRing(
                    hausaufgabenErledigt = state.zielHausaufgabenErledigt,
                    hausaufgabenGesamt = state.zielHausaufgaben,
                    minutenErreicht = state.zielMinutenErreicht,
                    minutenGesamt = state.zielMinuten,
                )
                LuftBreit(Mass.Mittel)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Tagesziel",
                        style = MaterialTheme.typography.titleMedium,
                        color = Elfenbein,
                    )
                    Luft(6.dp)
                    Zielzeile(
                        text = "${state.zielHausaufgabenErledigt} von " +
                            "${state.zielHausaufgaben} Hausaufgaben",
                        farbe = Messing,
                    )
                    Zielzeile(
                        text = "${state.zielMinutenErreicht} von " +
                            "${state.zielMinuten} Min gelernt",
                        farbe = Elfenbein,
                    )
                }
            }

            Trennlinie()

            // Hausaufgaben
            Abschnitt("Hausaufgaben") {
                Row(
                    Modifier.clickable(onClick = beiHausaufgabeNeu),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SymbolPlus()
                    LuftBreit(4.dp)
                    Text(
                        text = "Neu",
                        style = MaterialTheme.typography.labelLarge,
                        color = Messing,
                    )
                }
            }

            if (state.hausaufgaben.isEmpty()) {
                Hinweis(
                    text = "Keine Hausaufgaben offen. Zeit zum Lernen.",
                    modifier = Modifier.padding(horizontal = Mass.Rand, vertical = Mass.Klein),
                )
            } else {
                state.hausaufgaben.forEach { hausaufgabe ->
                    HomeworkRow(
                        hausaufgabe = hausaufgabe,
                        beiKlick = { beiHausaufgabe(hausaufgabe) },
                        beiErledigt = { beiHausaufgabeErledigt(hausaufgabe.id) },
                        beiLoeschen = { beiHausaufgabeLoeschen(hausaufgabe.id) },
                    )
                }
                if (state.keineHausaufgaben) {
                    Luft(Mass.Mittel)
                    Hinweis(
                        text = "Keine Hausaufgaben offen. Zeit zum Lernen.",
                        modifier = Modifier.padding(horizontal = Mass.Rand),
                    )
                }
            }

            Luft(Mass.Gross)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = Mass.Rand, end = Mass.Rand, bottom = Mass.Klein),
        ) {
            if (state.keineHausaufgaben) {
                PrimaryButton(text = "Lernsession planen", beiKlick = beiSessionPlanen)
            } else {
                PrimaryButton(text = "Hausaufgaben starten", beiKlick = beiHausaufgabenStarten)
                QuietButton(text = "Lernsession planen", beiKlick = beiSessionPlanen)
            }
        }
    }
}

@Composable
private fun Zielzeile(text: String, farbe: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 10.dp, height = 2.dp)
                .background(farbe),
        )
        LuftBreit(8.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = RelockTheme.farben.matt,
        )
    }
}

/** Die Hinweisleiste oben, wenn gerade eine Session laeuft. */
@Composable
private fun Sessionleiste(
    session: LaufendeSession,
    beiKlick: () -> Unit,
) {
    val farben = RelockTheme.farben
    Row(
        Modifier
            .fillMaxWidth()
            .background(farben.flaeche)
            .clickable(onClick = beiKlick)
            .padding(horizontal = Mass.Rand, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Zyklus ${session.zyklus} von ${session.zyklenGesamt}, " +
                    "noch ${uhr(session.restSekunden)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Elfenbein,
            )
            Text(
                text = "Session läuft",
                style = MaterialTheme.typography.labelSmall,
                color = Messing,
            )
        }
        SymbolWeiter(farbe = Messing)
    }
}
