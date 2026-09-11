package de.relock.app.feature.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.relock.app.data.AppEintrag
import de.relock.app.data.Berechtigung
import de.relock.app.data.Karte
import de.relock.app.data.Kartenstufe
import de.relock.app.data.Kopplung
import de.relock.app.data.fake.FakeData
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.RelockCard
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.Statuspunkt
import de.relock.app.ui.components.Suchpunkte
import de.relock.app.ui.components.SymbolQr
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Nacht
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal

/**
 * Onboarding, DESIGN.md 6.1. Sechs Schritte, Fortschritt als sechs feine
 * Striche oben.
 */
data class OnboardingUiState(
    val schritt: Int = 0,
    val name: String = "",
    val apps: List<AppEintrag> = FakeData.apps,
    val serverAdresse: String = "",
    val kopplung: Kopplung = Kopplung.Sucht,
    val berechtigungen: List<Berechtigung> = FakeData.berechtigungen,
) {
    val letzterSchritt: Int get() = 5

    /** Die Karte, die im Onboarding gezeigt wird: noch Graphit, ohne Guthaben. */
    val vorschaukarte: Karte
        get() = Karte(
            name = name.ifBlank { "Dein Name" },
            seit = FakeData.SEIT,
            stufe = Kartenstufe.Graphit,
            guthabenMinuten = 0,
            gesamtMinuten = 0,
        )
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    beiWeiter: () -> Unit,
    beiZurueck: () -> Unit,
    beiName: (String) -> Unit,
    beiAppUmschalten: (String) -> Unit,
    beiServerAdresse: (String) -> Unit,
    beiKopplungWechseln: () -> Unit,
    beiBerechtigung: (String) -> Unit,
    beiSpaeter: () -> Unit,
    beiFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(Nacht),
    ) {
        Fortschrittsstriche(
            schritt = state.schritt,
            gesamt = state.letzterSchritt + 1,
            modifier = Modifier.padding(
                start = Mass.Rand,
                end = Mass.Rand,
                top = Mass.Gross,
                bottom = Mass.Gross,
            ),
        )

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            when (state.schritt) {
                0 -> SchrittWillkommen(state)
                1 -> SchrittName(state, beiName)
                2 -> SchrittApps(state, beiAppUmschalten)
                3 -> SchrittServer(state, beiServerAdresse, beiKopplungWechseln, beiSpaeter)
                4 -> SchrittBerechtigungen(state, beiBerechtigung)
                else -> SchrittFertig(state)
            }
            Luft(Mass.Sehr)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = Mass.Rand, end = Mass.Rand, bottom = Mass.Gross),
        ) {
            PrimaryButton(
                text = when (state.schritt) {
                    0 -> "Los geht’s"
                    state.letzterSchritt -> "Zu Heute"
                    else -> "Weiter"
                },
                beiKlick = if (state.schritt == state.letzterSchritt) beiFertig else beiWeiter,
                aktiv = state.schritt != 1 || state.name.isNotBlank(),
            )
            if (state.schritt > 0) {
                QuietButton(text = "Zurück", beiKlick = beiZurueck)
            }
        }
    }
}

@Composable
private fun Fortschrittsstriche(schritt: Int, gesamt: Int, modifier: Modifier = Modifier) {
    val farben = RelockTheme.farben
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(gesamt) { index ->
            Box(
                Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(
                        if (index <= schritt) Messing else farben.linie,
                        Radius.Chip,
                    ),
            )
        }
    }
}

// --- 1 Willkommen ----------------------------------------------------------

@Composable
private fun ColumnScope.SchrittWillkommen(state: OnboardingUiState) {
    val reduziert = RelockTheme.reduzierteBewegung
    // Die Karte dreht sich langsam ins Bild, DESIGN.md 6.1.
    val winkel = remember { Animatable(if (reduziert) 0f else -28f) }
    LaunchedEffect(reduziert) {
        if (!reduziert) winkel.animateTo(0f, tween(1100, easing = FastOutSlowInEasing))
    }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand)
            .graphicsLayer {
                rotationY = winkel.value
                cameraDistance = 16f * density
            },
    ) {
        RelockCard(karte = state.vorschaukarte)
    }

    Luft(Mass.Weit)
    Column(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Deine Zeit ist etwas wert.",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )
        Luft(Mass.Mittel)
        Text(
            text = "Relock sperrt dein Handy. Freischalten kannst du es mit Zeit, " +
                "die du beim Lernen sammelst.",
            style = MaterialTheme.typography.bodyLarge,
            color = RelockTheme.farben.matt,
        )
    }
}

// --- 2 Name ----------------------------------------------------------------

@Composable
private fun ColumnScope.SchrittName(state: OnboardingUiState, beiName: (String) -> Unit) {
    Box(Modifier.padding(horizontal = Mass.Rand)) {
        RelockCard(karte = state.vorschaukarte)
    }

    Luft(Mass.Weit)
    Column(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Wie soll deine Karte heißen?",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )
        Luft(Mass.Gross)
        Eingabe(
            wert = state.name,
            beiAenderung = beiName,
            beschriftung = "Name",
        )
    }
}

// --- 3 Erlaubte Apps -------------------------------------------------------

@Composable
private fun ColumnScope.SchrittApps(
    state: OnboardingUiState,
    beiAppUmschalten: (String) -> Unit,
) {
    Column(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Diese Apps bleiben immer offen.",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )
    }
    Luft(Mass.Gross)

    state.apps.forEach { app ->
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    if (app.fest) {
                        Text(
                            text = "lässt sich nicht abschalten",
                            style = MaterialTheme.typography.labelSmall,
                            color = RelockTheme.farben.leise,
                        )
                    }
                }
                Schalter(
                    an = app.erlaubt,
                    aktiv = !app.fest,
                    beiAenderung = { beiAppUmschalten(app.name) },
                )
            }
            Trennlinie(eingerueckt = true)
        }
    }

    Luft(Mass.Gross)
    Hinweis(
        text = "Alle anderen Apps sind gesperrt. In Sessions ist dein Handy stumm, " +
            "Anrufe von Favoriten kommen durch.",
        modifier = Modifier.padding(horizontal = Mass.Rand),
    )
}

// --- 4 Server koppeln ------------------------------------------------------

@Composable
private fun ColumnScope.SchrittServer(
    state: OnboardingUiState,
    beiServerAdresse: (String) -> Unit,
    beiKopplungWechseln: () -> Unit,
    beiSpaeter: () -> Unit,
) {
    val farben = RelockTheme.farben

    Column(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Verbinde Relock mit deinem PC.",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )

        Luft(Mass.Gross)
        // QR-Scanner-Platzhalter. Version 1 hat keine Kamera.
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(farben.flaeche, Radius.Flaeche)
                .border(1.dp, farben.linie, Radius.Flaeche)
                .clickable(onClick = beiKopplungWechseln),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SymbolQr(groesse = 56.dp)
                Luft(Mass.Mittel)
                when (state.kopplung) {
                    Kopplung.Sucht -> {
                        Text(
                            text = "Sucht …",
                            style = MaterialTheme.typography.bodyMedium,
                            color = farben.matt,
                        )
                        Luft(Mass.Klein)
                        Suchpunkte()
                    }

                    Kopplung.Verbunden -> Row(verticalAlignment = Alignment.CenterVertically) {
                        Statuspunkt(farbe = Messing, groesse = 6.dp)
                        LuftBreit(8.dp)
                        Text(
                            text = "Brain verbunden",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Messing,
                        )
                    }

                    Kopplung.NichtGefunden -> Text(
                        text = "Nicht gefunden",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Signal,
                    )
                }
            }
        }

        Luft(Mass.Gross)
        Eingabe(
            wert = state.serverAdresse,
            beiAenderung = beiServerAdresse,
            beschriftung = "Adresse eingeben",
            platzhalter = FakeData.SERVER_ADRESSE,
        )

        Luft(Mass.Mittel)
        QuietButton(text = "Später", beiKlick = beiSpaeter)
    }
}

// --- 5 Berechtigungen ------------------------------------------------------

@Composable
private fun ColumnScope.SchrittBerechtigungen(
    state: OnboardingUiState,
    beiBerechtigung: (String) -> Unit,
) {
    val farben = RelockTheme.farben

    Column(Modifier.padding(horizontal = Mass.Rand)) {
        Text(
            text = "Relock braucht ein paar Rechte.",
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
        )
    }
    Luft(Mass.Gross)

    state.berechtigungen.forEach { recht ->
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Rand, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Statuspunkt(
                    farbe = if (recht.erteilt) Messing else farben.linie,
                    gefuellt = recht.erteilt,
                )
                LuftBreit(14.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = recht.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    Text(
                        text = recht.wofuer,
                        style = MaterialTheme.typography.labelSmall,
                        color = farben.matt,
                    )
                }
                LuftBreit(12.dp)
                if (recht.erteilt) {
                    Text(
                        text = "erlaubt",
                        style = MaterialTheme.typography.labelSmall,
                        color = Messing,
                    )
                } else {
                    Box(
                        Modifier
                            .border(1.dp, farben.linie, Radius.Chip)
                            .clickable { beiBerechtigung(recht.name) }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(
                            text = "Erlauben",
                            style = MaterialTheme.typography.labelSmall,
                            color = Elfenbein,
                        )
                    }
                }
            }
            Trennlinie(eingerueckt = true)
        }
    }
}

// --- 6 Fertig --------------------------------------------------------------

@Composable
private fun ColumnScope.SchrittFertig(state: OnboardingUiState) {
    Luft(Mass.Sehr)
    Box(Modifier.padding(horizontal = Mass.Rand)) {
        RelockCard(karte = state.vorschaukarte)
    }
    Luft(Mass.Weit)
    Text(
        text = "Deine Karte ist bereit.",
        style = MaterialTheme.typography.headlineMedium,
        color = Elfenbein,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
    )
}

// --- Kleinteile ------------------------------------------------------------

/** Eingabefeld im Relock-Look: Flaeche statt Rahmen, Messing als Fokusfarbe. */
@Composable
fun Eingabe(
    wert: String,
    beiAenderung: (String) -> Unit,
    beschriftung: String,
    modifier: Modifier = Modifier,
    platzhalter: String? = null,
    einzeilig: Boolean = true,
) {
    val farben = RelockTheme.farben
    OutlinedTextField(
        value = wert,
        onValueChange = beiAenderung,
        label = { Text(beschriftung) },
        placeholder = platzhalter?.let { { Text(it) } },
        singleLine = einzeilig,
        shape = Radius.Eingabe,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = farben.flaecheHoch,
            unfocusedContainerColor = farben.flaecheHoch,
            focusedIndicatorColor = Messing,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Messing,
            unfocusedLabelColor = farben.matt,
            focusedTextColor = Elfenbein,
            unfocusedTextColor = Elfenbein,
            cursorColor = Messing,
            focusedPlaceholderColor = farben.leise,
            unfocusedPlaceholderColor = farben.leise,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Schalter im Relock-Look. */
@Composable
fun Schalter(
    an: Boolean,
    beiAenderung: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
) {
    val farben = RelockTheme.farben
    Switch(
        checked = an,
        onCheckedChange = beiAenderung,
        enabled = aktiv,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Nacht,
            checkedTrackColor = Messing,
            checkedBorderColor = Messing,
            uncheckedThumbColor = farben.matt,
            uncheckedTrackColor = farben.flaecheHoch,
            uncheckedBorderColor = farben.linie,
            disabledCheckedThumbColor = Nacht,
            disabledCheckedTrackColor = Messing.copy(alpha = 0.5f),
            disabledCheckedBorderColor = Color.Transparent,
        ),
    )
}
