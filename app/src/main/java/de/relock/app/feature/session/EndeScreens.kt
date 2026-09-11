package de.relock.app.feature.session

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.relock.app.data.AppEintrag
import de.relock.app.data.Karte
import de.relock.app.data.dauer
import de.relock.app.data.fake.FakeData
import de.relock.app.data.uhr
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Kreiszaehler
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.Nachtflaeche
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.RelockCard
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.Segmente
import de.relock.app.ui.components.SymbolSchloss
import de.relock.app.ui.components.Trennlinie
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Signal

// =========================================================== 6.15 Session-Ende

/**
 * Session-Ende, DESIGN.md 6.15.
 *
 * Hier passiert der zweite der drei inszenierten Momente: die verdienten
 * Minuten fliegen als Zahl in die Karte, und das Guthaben zaehlt hoch.
 */
data class SessionEndeUiState(
    val karte: Karte = FakeData.karte,
    val verdient: Int = 30,
    val lernMinuten: Int = 65,
    val zyklen: Int = 3,
    val richtig: Int = 11,
    val fehler: Int = 2,
    val hausaufgaben: Int = 2,
    val nachsperreMinuten: Int = FakeData.NACHSPERRE_MINUTEN,
)

@Composable
fun SessionEndeScreen(
    state: SessionEndeUiState,
    beiFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben
    val reduziert = RelockTheme.reduzierteBewegung

    // 0 = Zahl steht unter der Karte, 1 = Zahl ist in der Karte angekommen.
    val flug = remember { Animatable(if (reduziert) 1f else 0f) }
    LaunchedEffect(reduziert) {
        if (!reduziert) {
            flug.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
    }

    // Waehrend die Zahl fliegt, zaehlt das Guthaben hoch.
    val vorher = (state.karte.guthabenMinuten - state.verdient).coerceAtLeast(0)
    val jetzt = vorher + (state.verdient * flug.value).toInt()

    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Luft(Mass.Gross)

            Box(contentAlignment = Alignment.BottomCenter) {
                RelockCard(karte = state.karte.copy(guthabenMinuten = jetzt))

                if (flug.value < 1f) {
                    Text(
                        text = "+${state.verdient} Min",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Messing,
                        modifier = Modifier.graphicsLayer {
                            // Fliegt von unten in die Karte und verblasst dabei.
                            translationY = (1f - flug.value) * 140f
                            alpha = 1f - flug.value
                            scaleX = 1f - flug.value * 0.3f
                            scaleY = 1f - flug.value * 0.3f
                        },
                    )
                }
            }

            Luft(Mass.Weit)
            Column(Modifier.fillMaxWidth()) {
                Bilanzzeile("Lernzeit", dauer(state.lernMinuten))
                Bilanzzeile("Zyklen", state.zyklen.toString())
                Bilanzzeile(
                    "Aufgaben",
                    "${state.richtig} richtig / ${state.fehler} Fehler",
                )
                Bilanzzeile("Hausaufgaben erledigt", state.hausaufgaben.toString())
            }

            Box(Modifier.weight(1f))

            Hinweis(
                text = "Deine Zeit ist gebucht. In ${state.nachsperreMinuten} Minuten " +
                    "kannst du sie nutzen.",
                zentriert = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Luft(Mass.Mittel)
            PrimaryButton(text = "Fertig", beiKlick = beiFertig)
            Luft(Mass.Klein)
        }
    }
}

@Composable
private fun Bilanzzeile(beschriftung: String, wert: String) {
    val farben = RelockTheme.farben
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = beschriftung,
                style = MaterialTheme.typography.bodyMedium,
                color = farben.matt,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = wert,
                style = MaterialTheme.typography.bodyLarge,
                color = Elfenbein,
            )
        }
        Trennlinie()
    }
}

// ============================================================== 6.16 Nachsperre

data class NachsperreUiState(
    val restSekunden: Int = 1624,
    val gesamtSekunden: Int = FakeData.NACHSPERRE_MINUTEN * 60,
) {
    val anteil: Float
        get() = if (gesamtSekunden <= 0) 0f else restSekunden.toFloat() / gesamtSekunden.toFloat()
}

/**
 * Nachsperre, DESIGN.md 6.16.
 * Der Text erklaert, warum gewartet wird - sonst fuehlt sich das wie Strafe an.
 */
@Composable
fun NachsperreScreen(
    state: NachsperreUiState,
    beiZurueck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Nachsperre",
                style = MaterialTheme.typography.headlineMedium,
                color = Elfenbein,
            )
            Luft(Mass.Weit)

            Kreiszaehler(anteil = state.anteil) {
                Text(
                    text = uhr(state.restSekunden),
                    style = MaterialTheme.typography.displayMedium,
                    color = Elfenbein,
                )
            }

            Luft(Mass.Weit)
            Text(
                text = "Dein Kopf speichert gerade, was du gelernt hast. " +
                    "Gut für jetzt: rausgehen, essen, Musik.",
                style = MaterialTheme.typography.bodyLarge,
                color = farben.matt,
                textAlign = TextAlign.Center,
            )

            Luft(Mass.Weit)
            QuietButton(text = "Zurück", beiKlick = beiZurueck)
        }
    }
}

// =========================================================== 6.17 Sperrbildschirm

/** Die fuenf Zustaende des Sperrbildschirms, DESIGN.md 6.17. */
sealed interface Sperrfall {
    data object GuthabenDa : Sperrfall
    data object KeinGuthaben : Sperrfall
    data class SessionLaeuft(val zyklus: Int) : Sperrfall
    data class Nachsperre(val restMinuten: Int) : Sperrfall
    data class Notausgang(val restSekunden: Int) : Sperrfall
}

data class SperrbildschirmUiState(
    val appName: String = "YouTube",
    val karte: Karte = FakeData.karte,
    val fall: Sperrfall = Sperrfall.GuthabenDa,
    val moeglichkeiten: List<Int> = listOf(5, 10, 15),
    val gewaehlt: Int = 1,
    val erlaubteApps: List<AppEintrag> = FakeData.apps,
) {
    val minuten: Int get() = moeglichkeiten.getOrElse(gewaehlt) { 10 }
}

@Composable
fun SperrbildschirmScreen(
    state: SperrbildschirmUiState,
    beiWahl: (Int) -> Unit,
    beiFreischalten: () -> Unit,
    beiSessionStarten: () -> Unit,
    beiZurueckZurSession: () -> Unit,
    beiStartbildschirm: () -> Unit,
    beiNotausgangAbbrechen: () -> Unit,
    beiNotausgang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = RelockTheme.farben

    Nachtflaeche(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Mass.Rand),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Oben die gesperrte App.
            Luft(Mass.Gross)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(22.dp)
                        .background(farben.flaecheHoch, Radius.Chip),
                )
                LuftBreit(10.dp)
                Text(
                    text = state.appName,
                    style = MaterialTheme.typography.labelSmall,
                    color = farben.matt,
                )
            }

            Luft(Mass.Weit)
            // Die Karte auf 70 Prozent, DESIGN.md 6.17.
            Box(Modifier.fillMaxWidth()) {
                RelockCard(karte = state.karte, skalierung = 0.7f)
            }

            Luft(Mass.Weit)
            when (val fall = state.fall) {
                Sperrfall.GuthabenDa -> {
                    SperrTitel("${state.appName} ist gesperrt.")
                    Luft(Mass.Gross)
                    Segmente(
                        beschriftungen = state.moeglichkeiten.map { "$it Min" },
                        gewaehlt = state.gewaehlt,
                        beiWahl = beiWahl,
                    )
                    Luft(Mass.Klein)
                    PrimaryButton(
                        text = "${state.minuten} Min freischalten",
                        beiKlick = beiFreischalten,
                        aktiv = state.minuten <= state.karte.guthabenMinuten,
                    )
                }

                Sperrfall.KeinGuthaben -> {
                    SperrTitel("Kein Guthaben.")
                    Luft(Mass.Klein)
                    Hinweis(text = "Ein Zyklus bringt 10 Min.", zentriert = true)
                    Luft(Mass.Gross)
                    PrimaryButton(text = "Session starten", beiKlick = beiSessionStarten)
                }

                is Sperrfall.SessionLaeuft -> {
                    SperrTitel("Du bist mitten in Zyklus ${fall.zyklus}.")
                    Luft(Mass.Gross)
                    PrimaryButton(text = "Zurück zur Session", beiKlick = beiZurueckZurSession)
                }

                is Sperrfall.Nachsperre -> {
                    SperrTitel("Noch ${fall.restMinuten} Min Nachsperre.")
                    Luft(Mass.Gross)
                    SecondaryButton(
                        text = "Zurück zum Startbildschirm",
                        beiKlick = beiStartbildschirm,
                    )
                }

                is Sperrfall.Notausgang -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SymbolSchloss(groesse = 20.dp, farbe = Signal, offen = true)
                        LuftBreit(8.dp)
                        SperrTitel("Notausgang in ${uhr(fall.restSekunden)}.", farbe = Signal)
                    }
                    Luft(Mass.Gross)
                    SecondaryButton(
                        text = "Abbrechen",
                        beiKlick = beiNotausgangAbbrechen,
                        farbe = Signal,
                    )
                }
            }

            Box(Modifier.weight(1f))

            // Der Notausgang steht in allen Zustaenden ganz unten.
            if (state.fall !is Sperrfall.Notausgang) {
                QuietButton(text = "Notausgang", beiKlick = beiNotausgang)
            }
            Luft(Mass.Klein)
        }
    }
}

@Composable
private fun SperrTitel(
    text: String,
    farbe: androidx.compose.ui.graphics.Color = Elfenbein,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = farbe,
        textAlign = TextAlign.Center,
    )
}

/**
 * Das Notausgang-Sheet, DESIGN.md 6.17.
 * Die Wartezeit ist der ganze Punkt: sie macht das Aufheben teuer genug,
 * dass man es nicht nebenbei tut.
 */
@Composable
fun NotausgangInhalt(
    beiWartezeitStarten: () -> Unit,
    beiDochNicht: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().padding(horizontal = Mass.Rand)) {
        Text(
            text = "Sperre trotzdem aufheben?",
            style = MaterialTheme.typography.titleMedium,
            color = Elfenbein,
        )
        Luft(Mass.Klein)
        Hinweis(
            text = "Das geht erst nach 15 Minuten Wartezeit. Während einer Session " +
                "endet sie damit.",
        )
        Luft(Mass.Gross)
        PrimaryButton(text = "Wartezeit starten", beiKlick = beiWartezeitStarten)
        QuietButton(text = "Doch nicht", beiKlick = beiDochNicht)
    }
}
