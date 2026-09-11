package de.kopfgeld.app.feature.session

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import de.kopfgeld.app.data.formatiereUhr
import de.kopfgeld.app.ui.components.BoxOverlay
import de.kopfgeld.app.ui.components.KameraPlatzhalter
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.SymbolBox
import de.kopfgeld.app.ui.components.TafelScaffold
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.KorrekturTafel
import de.kopfgeld.app.ui.theme.Kreide
import de.kopfgeld.app.ui.theme.KreideMatt
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius
import de.kopfgeld.app.ui.theme.TafelTheme

// ============================================================ 7.7 Box aufstellen

enum class Flaechenstand { Sucht, Gefunden, NichtErkannt }

/**
 * Box aufstellen, DESIGN.md 7.7.
 * Kamerabild ist ein Platzhalter, die Flaechenerkennung ist ein Mock-Zustand.
 */
data class BoxAufstellenUiState(
    val stand: Flaechenstand = Flaechenstand.Gefunden,
    val boxBreite: Dp = 180.dp,
    val boxHoehe: Dp = 120.dp,
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
    TafelTheme {
        Box(modifier.fillMaxSize()) {
            KameraPlatzhalter()

            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                BoxOverlay(
                    breite = state.boxBreite,
                    hoehe = state.boxHoehe,
                    versatzX = state.versatzX,
                    versatzY = state.versatzY,
                    halbdurchsichtig = state.stand != Flaechenstand.Gefunden,
                    ziehbar = state.stand == Flaechenstand.Gefunden,
                    beiVerschieben = beiVerschieben,
                    beiGroesse = beiGroesse,
                )
            }

            if (state.stand == Flaechenstand.Sucht) {
                Suchpunkte(Modifier.align(Alignment.Center))
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(Mass.Seitenrand),
            ) {
                Text(
                    text = "Richte die Kamera auf deinen Tisch und platziere die Box " +
                        "dort, wo dein Handy liegen soll.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Kreide,
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
                        Flaechenstand.NichtErkannt -> KorrekturTafel
                        Flaechenstand.Gefunden -> Kreide
                        else -> KreideMatt
                    },
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(Mass.Seitenrand),
            ) {
                MockHinweis(
                    text = "Zustand wechseln (nur zum Ausprobieren)",
                    beiKlick = beiStandWechseln,
                )
                Luft(Mass.Klein)
                PrimaerButton(
                    text = "Box hier platzieren",
                    beiKlick = beiPlatzieren,
                    aktiv = state.stand == Flaechenstand.Gefunden,
                )
                Luft(Mass.Klein)
                ZweitButton(text = "Abbrechen", beiKlick = beiAbbrechen, farbe = KreideMatt)
            }
        }
    }
}

@Composable
private fun Suchpunkte(modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            val leuchten = remember { Animatable(0.25f) }
            LaunchedEffect(index) {
                leuchten.animateTo(
                    targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700, delayMillis = index * 160),
                        repeatMode = RepeatMode.Reverse,
                    ),
                )
            }
            Box(
                Modifier
                    .size(6.dp)
                    .background(
                        Kreide.copy(alpha = leuchten.value),
                        CircleShape,
                    ),
            )
        }
    }
}

// ========================================================= 7.8 Handy hineinlegen

/**
 * Handy hineinlegen, DESIGN.md 7.8.
 * @param countdown null, solange nichts erkannt ist. Sonst 5 bis 1.
 */
data class HineinlegenUiState(
    val erkannt: Boolean = false,
    val countdown: Int? = null,
    val phasentitel: String = "Lernphase, Mathe",
)

@Composable
fun HineinlegenScreen(
    state: HineinlegenUiState,
    beiHandyErkannt: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reduziert = KopfgeldTheme.reduzierteBewegung

    TafelScaffold(modifier = modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable(enabled = !state.erkannt, onClick = beiHandyErkannt),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (state.countdown == null) {
                    SymbolBox(groesse = 72.dp, farbe = Kreide)
                    Luft(Mass.Gross)
                    Text(
                        text = "Leg dein Handy in die Box.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Kreide,
                        textAlign = TextAlign.Center,
                    )
                    Luft(Mass.Klein)
                    Text(
                        text = state.phasentitel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KreideMatt,
                    )
                } else {
                    // Die Ziffern wechseln mit kurzem Skalieren, DESIGN.md 4.4.
                    val skala = remember(state.countdown) { Animatable(if (reduziert) 1f else 1.22f) }
                    LaunchedEffect(state.countdown, reduziert) {
                        if (!reduziert) {
                            skala.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
                        }
                    }
                    Text(
                        text = state.countdown.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = Kreide,
                        modifier = Modifier.scale(skala.value),
                    )
                    Luft(Mass.Mittel)
                    Text(
                        text = "Nicht mehr bewegen.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = KreideMatt,
                    )
                }
            }

            if (state.countdown == null) {
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                ) {
                    MockHinweis(
                        text = "Tippen: Handy liegt in der Box",
                        beiKlick = beiHandyErkannt,
                    )
                    Luft(Mass.Klein)
                    ZweitButton(
                        text = "Abbrechen",
                        beiKlick = beiAbbrechen,
                        farbe = KreideMatt,
                    )
                }
            }
        }
    }
}

// =============================================================== 7.9 In der Box

enum class Boxstand { Laeuft, Unterbrochen, Fertig }

/**
 * In der Box, DESIGN.md 7.9.
 * Fast schwarz, kleiner Timer, Kaestchenreihe. Keine Buttons im Normalfall.
 */
data class InDerBoxUiState(
    val stand: Boxstand = Boxstand.Laeuft,
    val phasentitel: String = "Lernphase, Mathe",
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
    beiZyklusAbbrechen: () -> Unit,
    beiWeiter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Fast schwarz: Tafel mit 80 % Schwarz, DESIGN.md 7.9.
    TafelScaffold(modifier = modifier, abdunkeln = 0.8f) {
        Box(
            Modifier
                .fillMaxSize()
                .then(
                    if (state.stand == Boxstand.Unterbrochen) {
                        Modifier.border(2.dp, KorrekturTafel, Radius.Flaeche)
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
                            text = state.phasentitel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = KreideMatt.copy(alpha = 0.7f),
                        )
                        Luft(Mass.Klein)
                        Text(
                            text = formatiereUhr(state.restSekunden),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Kreide.copy(alpha = 0.85f),
                        )
                        Luft(Mass.Mittel)
                        Minutenkaestchen(
                            gesamt = state.phasenMinuten,
                            voll = state.vergangeneMinuten,
                        )
                        Luft(Mass.Sehr)
                        MockHinweis(
                            text = "Tippen: Handy herausnehmen",
                            beiKlick = beiHerausnehmen,
                            zentriert = true,
                        )
                    }

                    Boxstand.Unterbrochen -> {
                        Text(
                            text = "Du hast das Handy vor dem Ende herausgenommen.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = KorrekturTafel,
                            textAlign = TextAlign.Center,
                        )
                        Luft(Mass.Gross)
                        PrimaerButton(text = "Zurücklegen", beiKlick = beiZuruecklegen)
                        Luft(Mass.Klein)
                        ZweitButton(
                            text = "Zyklus abbrechen",
                            beiKlick = beiZyklusAbbrechen,
                            farbe = KorrekturTafel,
                        )
                        Luft(Mass.Mittel)
                        Text(
                            text = "Noch ${formatiereUhr(state.restSekunden)} in dieser Phase",
                            style = MaterialTheme.typography.labelSmall,
                            color = KreideMatt,
                        )
                    }

                    Boxstand.Fertig -> {
                        Text(
                            text = "Phase geschafft. Nimm dein Handy.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Kreide,
                            textAlign = TextAlign.Center,
                        )
                        Luft(Mass.Gross)
                        PrimaerButton(text = "Weiter", beiKlick = beiWeiter)
                    }
                }
            }
        }
    }
}

/** Eine Reihe Kaestchen, ein Kaestchen ist eine Minute. */
@Composable
private fun Minutenkaestchen(gesamt: Int, voll: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(gesamt.coerceAtMost(30)) { index ->
            Box(
                Modifier
                    .size(6.dp)
                    .background(
                        if (index < voll) {
                            Kreide.copy(alpha = 0.7f)
                        } else {
                            Kreide.copy(alpha = 0.18f)
                        },
                    ),
            )
        }
    }
}

// --- Gemeinsames -----------------------------------------------------------

/**
 * Phase 1 hat keine Sensoren und keine Kamera. Damit man die Zustaende
 * trotzdem durchlaufen kann, gibt es diese klar markierten Ersatzgriffe.
 * Sie verschwinden, sobald die echte Erkennung da ist.
 */
@Composable
fun MockHinweis(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
    zentriert: Boolean = false,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = beiKlick)
            .padding(vertical = 10.dp),
        horizontalArrangement = if (zentriert) Arrangement.Center else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 14.dp, height = 1.dp)
                .background(KreideMatt.copy(alpha = 0.6f)),
        )
        LuftBreit(8.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = KreideMatt.copy(alpha = 0.75f),
        )
    }
}

/** Dieselbe Markierung fuer Heft-Screens. */
@Composable
fun MockHinweisHeft(
    text: String,
    beiKlick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = beiKlick)
            .padding(horizontal = Mass.Seitenrand, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 14.dp, height = 1.dp)
                .background(stifte.blei.copy(alpha = 0.6f)),
        )
        LuftBreit(8.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = stifte.blei,
        )
    }
}
