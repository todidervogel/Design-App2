package de.relock.app.feature.einstellungen

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
import androidx.compose.ui.unit.dp
import de.relock.app.data.AppEintrag
import de.relock.app.data.Serverzustand
import de.relock.app.data.Updatezustand
import de.relock.app.data.fake.FakeData
import de.relock.app.feature.onboarding.Eingabe
import de.relock.app.feature.onboarding.Schalter
import de.relock.app.ui.components.Abschnitt
import de.relock.app.ui.components.Geruest
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.ServerStatus
import de.relock.app.ui.components.Statuspunkt
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.components.WeiterZeile
import de.relock.app.ui.components.Zeile
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

// ========================================================== 6.23 Einstellungen

/**
 * Einstellungen, DESIGN.md 6.23.
 *
 * Absichtlich wenige. Alles, was hier steht, aendert etwas, das man
 * taeglich merkt - der Rest gehoert nicht hierher.
 */
data class EinstellungenUiState(
    val name: String = FakeData.NAME,
    val apps: List<AppEintrag> = FakeData.apps,
    val zyklusMinuten: Int = FakeData.STANDARD_ZYKLUS_MINUTEN,
    val pauseVon: Int = FakeData.PAUSE_VON,
    val pauseBis: Int = FakeData.PAUSE_BIS,
    val nachsperreMinuten: Int = FakeData.NACHSPERRE_MINUTEN,
    val serverAdresse: String = FakeData.SERVER_ADRESSE,
    val server: Serverzustand = FakeData.serverVerbunden,
    val version: String = FakeData.VERSION,
    val geraeteAdminAktiv: Boolean = true,
    val update: Updatezustand = Updatezustand.Aktuell,
)

@Composable
fun EinstellungenScreen(
    state: EinstellungenUiState,
    beiZurueck: () -> Unit,
    beiName: (String) -> Unit,
    beiApp: (AppEintrag, Boolean) -> Unit,
    beiAppHinzufuegen: () -> Unit,
    beiZyklusdauer: () -> Unit,
    beiPause: () -> Unit,
    beiNachsperre: () -> Unit,
    beiServerAdresse: (String) -> Unit,
    beiNeuKoppeln: () -> Unit,
    beiUpdateSuchen: () -> Unit,
    beiGeraeteAdmin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Geruest(
        titel = "Einstellungen",
        zurueck = beiZurueck,
        modifier = modifier,
    ) {
        // --- Karte ---------------------------------------------------------
        Abschnitt(text = "Karte")
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Eingabe(
                wert = state.name,
                beiAenderung = beiName,
                beschriftung = "Name auf der Karte",
            )
        }
        Luft(Mass.Klein)
        Hinweis(text = "Der Name steht in Großbuchstaben auf der Karte, wie graviert.")

        // --- Erlaubte Apps -------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Erlaubte Apps")
        Trennlinie()
        state.apps.forEach { app ->
            Zeile(mitLinie = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Elfenbein,
                        )
                        if (app.fest) {
                            Luft(2.dp)
                            Text(
                                text = "lässt sich nicht abschalten",
                                style = MaterialTheme.typography.labelSmall,
                                color = farben.leise,
                            )
                        }
                    }
                    Schalter(
                        an = app.erlaubt,
                        beiAenderung = { an -> beiApp(app, an) },
                        aktiv = !app.fest,
                    )
                }
            }
        }
        WeiterZeile(titel = "App hinzufügen", beiKlick = beiAppHinzufuegen)

        // --- Session -------------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Session")
        Trennlinie()
        WeiterZeile(
            titel = "Standarddauer",
            wert = "${state.zyklusMinuten} Min",
            beiKlick = beiZyklusdauer,
        )
        WeiterZeile(
            titel = "Pause",
            wert = "${state.pauseVon}–${state.pauseBis} Min",
            beiKlick = beiPause,
        )
        WeiterZeile(
            titel = "Nachsperre",
            unterzeile = "Nach der Session bleibt das Handy noch so lange zu.",
            wert = "${state.nachsperreMinuten} Min",
            beiKlick = beiNachsperre,
        )

        // --- Brain-Server --------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Brain-Server")
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            Eingabe(
                wert = state.serverAdresse,
                beiAenderung = beiServerAdresse,
                beschriftung = "Adresse",
                platzhalter = "192.168.178.20:8000",
            )
        }
        Luft(Mass.Mittel)
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            ServerStatus(state.server)
        }
        Luft(Mass.Mittel)
        Box(Modifier.padding(horizontal = Mass.Rand)) {
            SecondaryButton(text = "Neu koppeln", beiKlick = beiNeuKoppeln)
        }

        // --- Relock --------------------------------------------------------
        Luft(Mass.Sehr)
        Abschnitt(text = "Relock")
        Trennlinie()
        Zeile {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Version",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = state.version,
                    style = MaterialTheme.typography.bodyMedium,
                    color = farben.matt,
                )
            }
        }
        WeiterZeile(
            titel = "Nach Updates suchen",
            unterzeile = when (val zustand = state.update) {
                Updatezustand.Aktuell -> "Relock ist auf dem neuesten Stand"
                is Updatezustand.Verfuegbar -> "Version ${zustand.version} liegt bereit"
                is Updatezustand.Laedt -> "Wird geladen … ${zustand.prozent} %"
                Updatezustand.BereitZumInstallieren -> "Bereit zum Installieren"
                Updatezustand.Fehlgeschlagen -> "Zuletzt fehlgeschlagen"
            },
            wertfarbe = Messing,
            beiKlick = beiUpdateSuchen,
        )
        Zeile(mitLinie = false, beiKlick = beiGeraeteAdmin) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Geräte-Admin",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    Luft(2.dp)
                    Text(
                        text = "Damit Relock sich nicht einfach deinstallieren lässt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = farben.matt,
                    )
                }
                LuftBreit(Mass.Mittel)
                Statuspunkt(
                    farbe = if (state.geraeteAdminAktiv) Richtig else Signal,
                )
                LuftBreit(8.dp)
                Text(
                    text = if (state.geraeteAdminAktiv) "aktiv" else "aus",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.geraeteAdminAktiv) Richtig else Signal,
                )
            }
        }

        Luft(Mass.Weit)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Mass.Rand),
        ) {
            Text(
                text = "Relock ${state.version}",
                style = MaterialTheme.typography.labelSmall,
                color = farben.leise,
            )
        }
        Luft(Mass.Weit)
    }
}
