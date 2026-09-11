package de.kopfgeld.app.feature.einstellungen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.kopfgeld.app.data.Berechtigung
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Verbindungstest
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.DurationSlider
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.PausenRangeSlider
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Einstellungen. In DESIGN.md 6 nur als Icon oben rechts auf Heute genannt,
 * der Inhalt folgt aus den Stellen, an denen "einstellbar" steht:
 * Pausenlaenge, Speicherzeit, Schlafenszeit, Server, Apps, Berechtigungen.
 */
data class EinstellungenUiState(
    val pauseVon: Int = 5,
    val pauseBis: Int = 15,
    val speicherzeitMinuten: Int = 30,
    val standardZyklusMinuten: Int = 25,
    val schlafenszeit: String = FakeData.schlafenszeit,
    val serverAdresse: String = FakeData.serverAdresse,
    val verbindung: Verbindungstest = Verbindungstest.Verbunden,
    val gesperrteApps: Int = FakeData.gesperrteApps.size,
    val berechtigungen: List<Berechtigung> = FakeData.berechtigungen,
) {
    val fehlendeBerechtigungen: Int get() = berechtigungen.count { !it.erteilt }
}

@Composable
fun EinstellungenScreen(
    state: EinstellungenUiState,
    beiZurueck: () -> Unit,
    beiPause: (Int, Int) -> Unit,
    beiSpeicherzeit: (Int) -> Unit,
    beiStandardZyklus: (Int) -> Unit,
    beiSchlafenszeit: () -> Unit,
    beiServer: () -> Unit,
    beiApps: () -> Unit,
    beiFaecher: () -> Unit,
    beiBerechtigungen: () -> Unit,
    beiOnboardingErneut: () -> Unit,
    beiKatalog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Einstellungen",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Abschnittstitel("Zeiten")
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                PausenRangeSlider(
                    vonMinuten = state.pauseVon,
                    bisMinuten = state.pauseBis,
                    beiAenderung = beiPause,
                )
                Hinweistext(
                    "Die Pause ist ein Rahmen, keine feste Zahl. Beim Weitermachen " +
                        "wählst du daraus.",
                )

                Luft(Mass.Gross)
                DurationSlider(
                    beschriftung = "Speicherzeit nach dem Aufhören",
                    minuten = state.speicherzeitMinuten,
                    beiAenderung = beiSpeicherzeit,
                    vonMinuten = 0,
                    bisMinuten = 60,
                )
                Hinweistext(
                    "So lange bleiben Scroll-Apps nach der Session noch zu, damit das " +
                        "Gelernte nicht direkt überschrieben wird.",
                )

                Luft(Mass.Gross)
                DurationSlider(
                    beschriftung = "Standard-Zyklus",
                    minuten = state.standardZyklusMinuten,
                    beiAenderung = beiStandardZyklus,
                    vonMinuten = 5,
                    bisMinuten = 60,
                )
            }

            Abschnittstitel("Sperre")
            EinstellungsZeile(
                titel = "Schlafenszeit",
                wert = "ab ${state.schlafenszeit}",
                beiKlick = beiSchlafenszeit,
            )
            EinstellungsZeile(
                titel = "Gesperrte Apps",
                wert = "${state.gesperrteApps} Apps",
                beiKlick = beiApps,
            )

            Abschnittstitel("Brain")
            EinstellungsZeile(
                titel = "Brain-Server",
                wert = state.serverAdresse,
                beiKlick = beiServer,
                nebentext = when (state.verbindung) {
                    Verbindungstest.Verbunden -> "verbunden"
                    Verbindungstest.NichtErreichbar -> "nicht erreichbar"
                    Verbindungstest.Prueft -> "prüft"
                    Verbindungstest.Ungeprueft -> "nicht geprüft"
                },
                nebenfarbe = when (state.verbindung) {
                    Verbindungstest.Verbunden -> stifte.tinte
                    Verbindungstest.NichtErreichbar -> stifte.korrektur
                    else -> stifte.blei
                },
            )
            EinstellungsZeile(
                titel = "Fächer und Stundenplan",
                wert = "${FakeData.faecher.count { it.aktiv }} Fächer",
                beiKlick = beiFaecher,
            )

            Abschnittstitel("System")
            EinstellungsZeile(
                titel = "Berechtigungen",
                wert = if (state.fehlendeBerechtigungen == 0) {
                    "alle erteilt"
                } else {
                    "${state.fehlendeBerechtigungen} fehlen"
                },
                beiKlick = beiBerechtigungen,
                nebenfarbe = if (state.fehlendeBerechtigungen == 0) {
                    stifte.blei
                } else {
                    stifte.korrektur
                },
            )

            Luft(Mass.Gross)
            Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
                TextAktion(text = "Einführung noch einmal ansehen", beiKlick = beiOnboardingErneut)
                TextAktion(text = "Screen-Katalog öffnen", beiKlick = beiKatalog)
                Luft(Mass.Klein)
                Hinweistext(
                    "Der Screen-Katalog zeigt alle Screens und Zustände. Du erreichst " +
                        "ihn auch mit einem langen Druck auf das Datum in Heute.",
                )
            }
            Luft(Mass.Gross)
        }
    }
}

@Composable
private fun EinstellungsZeile(
    titel: String,
    wert: String,
    beiKlick: () -> Unit,
    nebentext: String? = null,
    nebenfarbe: Color =
        Color.Unspecified,
) {
    val stifte = KopfgeldTheme.stifte
    val ton = if (nebenfarbe == Color.Unspecified) {
        stifte.blei
    } else {
        nebenfarbe
    }
    RasterZeile(beiKlick = beiKlick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = titel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = wert,
                    style = MaterialTheme.typography.bodyMedium,
                    color = stifte.blei,
                )
            }
            if (nebentext != null) {
                Box {
                    Text(
                        text = nebentext,
                        style = MaterialTheme.typography.labelSmall,
                        color = ton,
                    )
                }
            }
        }
    }
}
