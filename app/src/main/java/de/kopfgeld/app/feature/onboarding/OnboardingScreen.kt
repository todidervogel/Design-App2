package de.kopfgeld.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.AppEintrag
import de.kopfgeld.app.data.Berechtigung
import de.kopfgeld.app.data.Fach
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Stundenplanfeld
import de.kopfgeld.app.data.Verbindungstest
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Kaestchen
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Onboarding, DESIGN.md 7.1. Fuenf Seiten, Fortschritt als fuenf Kaestchen.
 */
data class OnboardingUiState(
    val seite: Int = 0,
    val faecher: List<Fach> = FakeData.faecher,
    val stundenplan: List<Stundenplanfeld> = FakeData.stundenplan,
    val gesperrteApps: List<AppEintrag> = FakeData.gesperrteApps,
    val erlaubteApps: List<AppEintrag> = FakeData.erlaubteApps,
    val serverAdresse: String = FakeData.serverAdresse,
    val verbindungstest: Verbindungstest = Verbindungstest.Ungeprueft,
    val berechtigungen: List<Berechtigung> = FakeData.berechtigungen,
) {
    val letzteSeite: Int get() = 4
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    beiWeiter: () -> Unit,
    beiZurueck: () -> Unit,
    beiFachUmschalten: (String) -> Unit,
    beiPruefungsfachUmschalten: (String) -> Unit,
    beiStundenplanfeld: (Int, Int) -> Unit,
    beiAppUmschalten: (String) -> Unit,
    beiServerAdresse: (String) -> Unit,
    beiVerbindungTesten: () -> Unit,
    beiSpaeterEinrichten: () -> Unit,
    beiBerechtigung: (String) -> Unit,
    beiFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Fortschrittskaestchen(
            seite = state.seite,
            gesamt = state.letzteSeite + 1,
            modifier = Modifier.padding(
                start = Mass.Seitenrand,
                end = Mass.Seitenrand,
                top = Mass.Gross,
                bottom = Mass.Mittel,
            ),
        )

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            when (state.seite) {
                0 -> SeiteIdee()
                1 -> SeiteFaecher(
                    faecher = state.faecher,
                    stundenplan = state.stundenplan,
                    beiFachUmschalten = beiFachUmschalten,
                    beiPruefungsfachUmschalten = beiPruefungsfachUmschalten,
                    beiStundenplanfeld = beiStundenplanfeld,
                )

                2 -> SeiteApps(
                    gesperrt = state.gesperrteApps,
                    erlaubt = state.erlaubteApps,
                    beiAppUmschalten = beiAppUmschalten,
                )

                3 -> SeiteServer(
                    adresse = state.serverAdresse,
                    test = state.verbindungstest,
                    beiAdresse = beiServerAdresse,
                    beiTesten = beiVerbindungTesten,
                    beiSpaeter = beiSpaeterEinrichten,
                )

                else -> SeiteBerechtigungen(
                    berechtigungen = state.berechtigungen,
                    beiBerechtigung = beiBerechtigung,
                )
            }
            Luft(Mass.Gross)
        }

        Trennlinie()
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Mass.Seitenrand),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.seite > 0) {
                ZweitButton(
                    text = "Zurück",
                    beiKlick = beiZurueck,
                    modifier = Modifier.weight(1f),
                )
                LuftBreit(12.dp)
            }
            PrimaerButton(
                text = if (state.seite == state.letzteSeite) "Los geht's" else "Weiter",
                beiKlick = if (state.seite == state.letzteSeite) beiFertig else beiWeiter,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Fortschrittskaestchen(seite: Int, gesamt: Int, modifier: Modifier = Modifier) {
    val stifte = KopfgeldTheme.stifte
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(gesamt) { index ->
            Box(
                Modifier
                    .size(width = 26.dp, height = 6.dp)
                    .background(
                        if (index <= seite) stifte.tinte else stifte.karo,
                        Radius.Chip,
                    ),
            )
        }
    }
}

// --- Seite 1 ---------------------------------------------------------------

@Composable
private fun ColumnScope.SeiteIdee() {
    Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
        Text(
            text = "Erst lernen, dann scrollen.",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Luft(Mass.Mittel)
        Text(
            text = "Du lernst in Zyklen, dein Handy liegt dabei in einer Box auf " +
                "dem Tisch. Alles, was du lernst, landet in deinem Brain.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// --- Seite 2 ---------------------------------------------------------------

@Composable
private fun ColumnScope.SeiteFaecher(
    faecher: List<Fach>,
    stundenplan: List<Stundenplanfeld>,
    beiFachUmschalten: (String) -> Unit,
    beiPruefungsfachUmschalten: (String) -> Unit,
    beiStundenplanfeld: (Int, Int) -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    Text(
        text = "Fächer und Stundenplan",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Klein)
    Hinweistext(
        text = "Prüfungsfächer bekommen einen Rahmen. Aus dem Stundenplan " +
            "schlägt dir die App Blurting-Zyklen für den Tag vor.",
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Mittel)

    faecher.forEach { fach ->
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = Mass.Seitenrand, end = Mass.Seitenrand, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SubjectBadge(fach)
                LuftBreit(12.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = fach.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (fach.aktiv) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            stifte.blei
                        },
                    )
                    Row(
                        Modifier.clickable { beiPruefungsfachUmschalten(fach.id) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Kaestchen(
                            angehakt = fach.istPruefungsfach,
                            beiKlick = { beiPruefungsfachUmschalten(fach.id) },
                            modifier = Modifier.size(28.dp),
                        )
                        LuftBreit(6.dp)
                        Text(
                            text = "Prüfungsfach",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                }
                Schalter(
                    an = fach.aktiv,
                    beiAenderung = { beiFachUmschalten(fach.id) },
                )
            }
            Trennlinie()
        }
    }

    Luft(Mass.Gross)
    Text(
        text = "Stundenplan",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Klein)
    Wochenraster(
        stundenplan = stundenplan,
        beiFeld = beiStundenplanfeld,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
}

@Composable
private fun Wochenraster(
    stundenplan: List<Stundenplanfeld>,
    beiFeld: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val stunden = (stundenplan.maxOfOrNull { it.stunde } ?: 5) + 1

    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            FakeData.wochentage.forEach { tag ->
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = stifte.blei,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Luft(4.dp)
        repeat(stunden) { stunde ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FakeData.wochentage.indices.forEach { tag ->
                    val feld = stundenplan.firstOrNull { it.tag == tag && it.stunde == stunde }
                    val fach = FakeData.fachOderNull(feld?.fachId)
                    Box(
                        Modifier
                            .weight(1f)
                            .height(34.dp)
                            .background(
                                if (fach == null) stifte.tief else stifte.tinteHauch,
                                Radius.Chip,
                            )
                            .clickable { beiFeld(tag, stunde) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = fach?.kuerzel ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (fach == null) stifte.blei else stifte.tinte,
                        )
                    }
                }
            }
        }
    }
}

// --- Seite 3 ---------------------------------------------------------------

@Composable
private fun ColumnScope.SeiteApps(
    gesperrt: List<AppEintrag>,
    erlaubt: List<AppEintrag>,
    beiAppUmschalten: (String) -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    Text(
        text = "Apps",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Klein)
    Hinweistext(
        text = "Gesperrte Apps gehen während Zyklen, Pausen und Speicherzeit " +
            "nicht auf. Erlaubte Apps bleiben immer offen.",
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )

    Luft(Mass.Gross)
    Text(
        text = "Gesperrt",
        style = MaterialTheme.typography.titleMedium,
        color = stifte.korrektur,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Klein)
    gesperrt.forEach { eintrag ->
        AppZeile(eintrag = eintrag, beiUmschalten = { beiAppUmschalten(eintrag.name) })
    }

    Luft(Mass.Gross)
    Text(
        text = "Immer erlaubt",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Klein)
    erlaubt.forEach { eintrag ->
        AppZeile(eintrag = eintrag, beiUmschalten = { beiAppUmschalten(eintrag.name) })
    }
}

@Composable
private fun AppZeile(eintrag: AppEintrag, beiUmschalten: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Mass.Seitenrand, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = eintrag.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Schalter(an = eintrag.gesperrt, beiAenderung = { beiUmschalten() })
        }
        Trennlinie()
    }
}

// --- Seite 4 ---------------------------------------------------------------

@Composable
private fun ColumnScope.SeiteServer(
    adresse: String,
    test: Verbindungstest,
    beiAdresse: (String) -> Unit,
    beiTesten: () -> Unit,
    beiSpaeter: () -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    Column(Modifier.padding(horizontal = Mass.Seitenrand)) {
        Text(
            text = "Brain-Server",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Luft(Mass.Mittel)
        OutlinedTextField(
            value = adresse,
            onValueChange = beiAdresse,
            label = { Text("Adresse") },
            singleLine = true,
            shape = Radius.Eingabe,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
        )
        Luft(Mass.Mittel)
        ZweitButton(text = "Verbindung testen", beiKlick = beiTesten)

        Luft(Mass.Mittel)
        val (farbe, meldung) = when (test) {
            Verbindungstest.Ungeprueft -> stifte.blei to "Noch nicht geprüft."
            Verbindungstest.Prueft -> stifte.blei to "Prüft …"
            Verbindungstest.Verbunden -> stifte.tinte to "Verbunden."
            Verbindungstest.NichtErreichbar -> stifte.korrektur to "Nicht erreichbar."
        }
        Text(text = meldung, style = MaterialTheme.typography.bodyMedium, color = farbe)

        Luft(Mass.Gross)
        Hinweistext(
            text = "Ohne Server funktioniert alles außer der Analyse. Scans " +
                "warten, bis der Server erreichbar ist.",
        )
        Luft(Mass.Klein)
        TextAktion(text = "Später einrichten", beiKlick = beiSpaeter)
    }
}

// --- Seite 5 ---------------------------------------------------------------

@Composable
private fun ColumnScope.SeiteBerechtigungen(
    berechtigungen: List<Berechtigung>,
    beiBerechtigung: (String) -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    Text(
        text = "Berechtigungen",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = Mass.Seitenrand),
    )
    Luft(Mass.Mittel)

    berechtigungen.forEach { berechtigung ->
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { beiBerechtigung(berechtigung.name) }
                    .padding(horizontal = Mass.Seitenrand, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = berechtigung.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = berechtigung.erklaerung,
                        style = MaterialTheme.typography.bodyMedium,
                        color = stifte.blei,
                    )
                }
                LuftBreit(12.dp)
                Box(
                    Modifier
                        .background(
                            if (berechtigung.erteilt) stifte.tinteHauch else stifte.tief,
                            Radius.Chip,
                        )
                        .border(
                            1.dp,
                            if (berechtigung.erteilt) stifte.tinte else stifte.karo,
                            Radius.Chip,
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = if (berechtigung.erteilt) "erteilt" else "fehlt",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (berechtigung.erteilt) stifte.tinte else stifte.blei,
                    )
                }
            }
            Trennlinie()
        }
    }
}

// --- Kleinteile ------------------------------------------------------------

@Composable
private fun Schalter(an: Boolean, beiAenderung: (Boolean) -> Unit) {
    val stifte = KopfgeldTheme.stifte
    Switch(
        checked = an,
        onCheckedChange = beiAenderung,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.background,
            checkedTrackColor = stifte.tinte,
            uncheckedThumbColor = stifte.blei,
            uncheckedTrackColor = stifte.tief,
            uncheckedBorderColor = stifte.karo,
        ),
    )
}
