package de.kopfgeld.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.AppEintrag
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Sperrstand
import de.kopfgeld.app.data.formatiereDauer
import de.kopfgeld.app.data.formatiereUhr
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.LockStatusLine
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MarkerAmount
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.TafelScaffold
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.theme.Kreide
import de.kopfgeld.app.ui.theme.KreideMatt
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.KorrekturTafel
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Session-Ende mit Speicherzeit.
 *
 * Abgeleitet aus DESIGN.md 2.1: "Nach der letzten Verbesserung (oder 'spaeter
 * verbessern') startet die Speicherzeit: ein Timer (Standard 30 Min,
 * einstellbar), in dem Scroll-Apps gesperrt bleiben, damit das Gelernte nicht
 * direkt ueberschrieben wird."
 */
data class SessionEndeUiState(
    val zyklenGeschafft: Int = 2,
    val zyklenGeplant: Int = 3,
    val lernminuten: Int = 55,
    val verdientesGuthaben: Int = 42,
    val aufgabenRichtig: Int = 4,
    val aufgabenFehler: Int = 2,
    val neueKarten: Int = 2,
    val speicherzeitRestSekunden: Int = 1800,
    val speicherzeitGesamtMinuten: Int = 30,
    val offeneVerbesserung: Boolean = false,
) {
    val sperre: Sperrstand
        get() = Sperrstand.Speicherzeit(
            restMinuten = (speicherzeitRestSekunden + 59) / 60,
            gesamtMinuten = speicherzeitGesamtMinuten,
        )
}

@Composable
fun SessionEndeScreen(
    state: SessionEndeUiState,
    beiVerbesserungOeffnen: () -> Unit,
    beiFertig: () -> Unit,
    beiSpeicherzeitAendern: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Geschafft",
        unterzeile = "${state.zyklenGeschafft} von ${state.zyklenGeplant} Zyklen, " +
            formatiereDauer(state.lernminuten),
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(text = "Fertig", beiKlick = beiFertig)
                if (state.offeneVerbesserung) {
                    TextAktion(
                        text = "Verbesserung jetzt durchgehen",
                        beiKlick = beiVerbesserungOeffnen,
                    )
                }
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                Luft(Mass.Mittel)
                Text(
                    text = "Dein Guthaben",
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
                MarkerAmount(
                    wert = state.verdientesGuthaben.toString(),
                    einheit = "Min",
                    animieren = true,
                )
                Luft(Mass.Mittel)
                Hinweistext(
                    "Frei, sobald die Speicherzeit vorbei ist.",
                )
            }

            Luft(Mass.Gross)
            Trennlinie()

            // Speicherzeit
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(stifte.tief)
                    .padding(Mass.Seitenrand),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Speicherzeit",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Scroll-Apps bleiben zu, damit das Gelernte nicht " +
                                "direkt überschrieben wird.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = stifte.blei,
                        )
                    }
                    LuftBreit(12.dp)
                    Text(
                        text = formatiereUhr(state.speicherzeitRestSekunden),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Luft(Mass.Klein)
                LockStatusLine(state.sperre)
                Luft(Mass.Klein)
                TextAktion(text = "Speicherzeit ändern", beiKlick = beiSpeicherzeitAendern)
            }

            Trennlinie()

            // Bilanz des Tages
            Luft(Mass.Gross)
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                Text(
                    text = "Was daraus geworden ist",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Luft(Mass.Klein)
                Bilanzzeile("Richtig", state.aufgabenRichtig.toString(), stifte.richtig)
                Bilanzzeile("Fehler und Lücken", state.aufgabenFehler.toString(), stifte.korrektur)
                Bilanzzeile("Neue Karteikarten", state.neueKarten.toString(), stifte.tinte)
            }
            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun Bilanzzeile(
    beschriftung: String,
    wert: String,
    farbe: androidx.compose.ui.graphics.Color,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(farbe, CircleShape),
        )
        LuftBreit(10.dp)
        Text(
            text = beschriftung,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = wert,
            style = MaterialTheme.typography.titleMedium,
            color = farbe,
        )
    }
}

/**
 * Sperrbildschirm.
 *
 * In DESIGN.md 6 nur als Vollbild-Flow genannt. Er erscheint, wenn man
 * waehrend Zyklus, Pause oder Speicherzeit eine gesperrte App oeffnet.
 * Tafel-Look, Rot heisst hier wie ueberall: gesperrt.
 */
data class SperrbildschirmUiState(
    val appName: String = "TikTok",
    val grund: String = "Speicherzeit läuft",
    val restSekunden: Int = 1080,
    val erlaubteApps: List<AppEintrag> = FakeData.erlaubteApps,
    val guthabenMinuten: Int = FakeData.guthabenMinuten,
)

@Composable
fun SperrbildschirmScreen(
    state: SperrbildschirmUiState,
    beiZurueck: () -> Unit,
    beiGuthabenEinsetzen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TafelScaffold(modifier = modifier) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Scroll-Apps gesperrt",
                style = MaterialTheme.typography.headlineMedium,
                color = KorrekturTafel,
                textAlign = TextAlign.Center,
            )
            Luft(Mass.Klein)
            Text(
                text = "${state.appName} bleibt zu. ${state.grund}.",
                style = MaterialTheme.typography.bodyLarge,
                color = KreideMatt,
                textAlign = TextAlign.Center,
            )

            Luft(Mass.Sehr)
            Text(
                text = formatiereUhr(state.restSekunden),
                style = MaterialTheme.typography.displayLarge,
                color = Kreide,
            )

            Luft(Mass.Sehr)
            Text(
                text = "Immer erlaubt",
                style = MaterialTheme.typography.labelSmall,
                color = KreideMatt,
            )
            Luft(Mass.Klein)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.erlaubteApps.forEach { app ->
                    Box(
                        Modifier
                            .background(Kreide.copy(alpha = 0.12f), Radius.Chip)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Kreide,
                        )
                    }
                }
            }

            Luft(Mass.Sehr)
            PrimaerButton(text = "Zurück", beiKlick = beiZurueck)
            Luft(Mass.Klein)
            TextAktion(
                text = "${state.guthabenMinuten} Min Guthaben einsetzen",
                beiKlick = beiGuthabenEinsetzen,
                farbe = KreideMatt,
            )
        }
    }
}
