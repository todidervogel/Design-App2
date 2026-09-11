package de.relock.app.feature.session

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.relock.app.data.uhr
import de.relock.app.ui.components.Behelf
import de.relock.app.ui.components.BoxOverlay
import de.relock.app.ui.components.KameraPlatzhalter
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.MinuteTrack
import de.relock.app.ui.components.Nachtflaeche
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.Schwarzflaeche
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.Suchpunkte
import de.relock.app.ui.components.SymbolBox
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.ElfenbeinLeise
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal

// ============================================================ 6.7 Box aufstellen

enum class Flaechenstand { Sucht, Gefunden, NichtErkannt }

/**
 * Box aufstellen, DESIGN.md 6.7.
 * Kamerabild ist ein Platzhalter, die Flaechenerkennung ist ein Mock-Zustand.
 */
data class BoxAufstellenUiState(
    val stand: Flaechenstand = Flaechenstand.Gefunden,
    val breite: Dp = 190.dp,
    val hoehe: Dp = 120.dp,
    val versatzX: Dp = 0.dp,
    val versatzY: Dp = 0.dp,
)

@Composable
fun BoxAufstellenScreen(
    state: BoxAufstellenUiState,
    beiAbbrechen: () -> Unit,
    beiVerschieben: (Dp, Dp) -> Unit,
    beiGroesse: (Dp, Dp) -> Unit,
    beiPlatzieren: () -> Unit,
    beiStandWechseln: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        KameraPlatzhalter()

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BoxOverlay(
                breite = state.breite,
                hoehe = state.hoehe,
                versatzX = state.versatzX,
                versatzY = state.versatzY,
                halbdurchsichtig = state.stand != Flaechenstand.Gefunden,
                ziehbar = state.stand == Flaechenstand.Gefunden,
                beiVerschieben = beiVerschieben,
                beiGroesse = beiGroesse,
            )
            if (state.stand == Flaechenstand.Sucht) {
                Suchpunkte()
            }
        }

        Column(
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(Mass.Rand),
        ) {
            Text(
                text = "Richte die Kamera auf deinen Tisch und platziere die Box.",
                style = MaterialTheme.typography.bodyLarge,
                color = Elfenbein,
            )
            Luft(Mass.Mittel)
            Text(
                text = when (state.stand) {
                    Flaechenstand.Sucht -> "Suche Tischfläche …"
                    Flaechenstand.Gefunden -> "Fläche gefunden"
                    Flaechenstand.NichtErkannt ->
                        "Keine Fläche erkannt. Mehr Licht oder Kamera langsam bewegen."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when (state.stand) {
                    Flaechenstand.Gefunden -> Messing
                    Flaechenstand.NichtErkannt -> Signal
                    Flaechenstand.Sucht -> ElfenbeinLeise
                },
            )
        }

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = Mass.Rand, end = Mass.Rand, bottom = Mass.Gross),
        ) {
            Behelf(
                text = "Zustand wechseln (nur zum Ausprobieren)",
                beiKlick = beiStandWechseln,
            )
            Luft(Mass.Klein)
            PrimaryButton(
                text = "Box platzieren",
                beiKlick = beiPlatzieren,
                aktiv = state.stand == Flaechenstand.Gefunden,
            )
            QuietButton(text = "Abbrechen", beiKlick = beiAbbrechen)
        }
    }
}

// =========================================================== 6.8 Handy einlegen

/**
 * Handy einlegen, DESIGN.md 6.8.
 * @param countdown null, solange nichts erkannt ist. Sonst 5 bis 1, dann 0.
 */
data class HandyEinlegenUiState(
    val countdown: Int? = null,
    val phase: String = "Aufgabenphase, Mathe",
)

@Composable
fun HandyEinlegenScreen(
    state: HandyEinlegenUiState,
    beiErkannt: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reduziert = RelockTheme.reduzierteBewegung

    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (val zahl = state.countdown) {
                null -> {
                    SymbolBox(groesse = 76.dp)
                    Luft(Mass.Weit)
                    Text(
                        text = "Leg dein Handy in die Box.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Elfenbein,
                        textAlign = TextAlign.Center,
                    )
                    Luft(Mass.Klein)
                    Text(
                        text = state.phase,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElfenbeinLeise,
                    )
                }

                0 -> Text(
                    text = "Gesperrt.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Messing,
                )

                else -> {
                    // Die Ziffern wechseln mit kurzem Skalieren, DESIGN.md 2.5.
                    val skala = remember(zahl) { Animatable(if (reduziert) 1f else 1.18f) }
                    LaunchedEffect(zahl, reduziert) {
                        if (!reduziert) {
                            skala.animateTo(1f, tween(200, easing = FastOutSlowInEasing))
                        }
                    }
                    Text(
                        text = zahl.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = Elfenbein,
                        modifier = Modifier.scale(skala.value),
                    )
                    Luft(Mass.Mittel)
                    Text(
                        text = "Nicht mehr bewegen.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ElfenbeinLeise,
                    )
                }
            }
        }

        if (state.countdown == null) {
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = Mass.Rand, end = Mass.Rand, bottom = Mass.Gross),
            ) {
                Behelf(text = "Tippen: Handy liegt in der Box", beiKlick = beiErkannt)
                QuietButton(text = "Abbrechen", beiKlick = beiAbbrechen)
            }
        }
    }
}

// ============================================================== 6.9 In der Box

enum class Boxstand { Laeuft, Unterbrochen, AnrufPausiert }

/**
 * In der Box, DESIGN.md 6.9.
 *
 * Schwarz, alles in ElfenbeinLeise. Niemand schaut hier hin - und wenn
 * doch, soll nichts leuchten.
 */
data class InDerBoxUiState(
    val stand: Boxstand = Boxstand.Laeuft,
    val phase: String = "Aufgabenphase, Mathe",
    val restSekunden: Int = 760,
    val phasenMinuten: Int = 20,
) {
    val vergangeneMinuten: Int
        get() = (phasenMinuten - (restSekunden + 59) / 60).coerceIn(0, phasenMinuten)
}

@Composable
fun InDerBoxScreen(
    state: InDerBoxUiState,
    beiHerausnehmen: () -> Unit,
    beiZuruecklegen: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Schwarzflaeche(modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand)
                .then(
                    if (state.stand == Boxstand.Unterbrochen) {
                        Modifier.border(1.5.dp, Signal, Radius.Flaeche)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when (state.stand) {
                    Boxstand.Laeuft -> {
                        Text(
                            text = state.phase,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElfenbeinLeise,
                        )
                        Luft(Mass.Mittel)
                        Text(
                            text = uhr(state.restSekunden),
                            style = MaterialTheme.typography.displaySmall,
                            color = ElfenbeinLeise,
                        )
                        Luft(Mass.Gross)
                        Box(Modifier.fillMaxWidth(0.6f)) {
                            MinuteTrack(
                                vergangen = state.vergangeneMinuten,
                                gesamt = state.phasenMinuten,
                                farbe = ElfenbeinLeise,
                            )
                        }
                        Luft(Mass.Weit)
                        Behelf(
                            text = "Tippen: Handy herausnehmen",
                            beiKlick = beiHerausnehmen,
                            zentriert = true,
                        )
                    }

                    Boxstand.Unterbrochen -> {
                        Text(
                            text = "Du hast das Handy zu früh herausgenommen.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Signal,
                            textAlign = TextAlign.Center,
                        )
                        Luft(Mass.Klein)
                        Text(
                            text = "Noch ${uhr(state.restSekunden)} in dieser Phase",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElfenbeinLeise,
                        )
                        Luft(Mass.Weit)
                        PrimaryButton(text = "Zurücklegen", beiKlick = beiZuruecklegen)
                        Luft(Mass.Klein)
                        SecondaryButton(
                            text = "Zyklus abbrechen",
                            beiKlick = beiAbbrechen,
                            farbe = Signal,
                        )
                    }

                    Boxstand.AnrufPausiert -> {
                        Text(
                            text = "Pausiert wegen Anruf",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ElfenbeinLeise,
                            textAlign = TextAlign.Center,
                        )
                        Luft(Mass.Mittel)
                        Text(
                            text = uhr(state.restSekunden),
                            style = MaterialTheme.typography.displaySmall,
                            color = ElfenbeinLeise,
                        )
                        Luft(Mass.Weit)
                        Behelf(
                            text = "Tippen: weiter",
                            beiKlick = beiZuruecklegen,
                            zentriert = true,
                        )
                    }
                }
            }
        }
    }
}
