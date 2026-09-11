package de.relock.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import de.relock.app.data.Buchung
import de.relock.app.data.Karte
import de.relock.app.data.Kartenzustand
import de.relock.app.data.dauer
import de.relock.app.ui.theme.Kartengravur
import de.relock.app.ui.theme.Kartenmaterial
import de.relock.app.ui.theme.Kartenwortmarke
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.materialFuer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Die Relock-Karte, DESIGN.md 4.
 *
 * Das einzige Element der App mit Materialwirkung. Alles andere ist flach.
 * Deshalb steckt hier auch die ganze Aufwaendigkeit: Metallverlauf,
 * Buerstung, Glanzstreifen, Gravur, Schatten, Drehung.
 *
 * Antippen dreht die Karte zur Rueckseite (Kontoauszug), langes Druecken
 * oeffnet "Freischalten".
 *
 * @param gedreht true zeigt die Rueckseite
 * @param skalierung 0.7 fuer den Sperrbildschirm, sonst 1
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RelockCard(
    karte: Karte,
    modifier: Modifier = Modifier,
    gedreht: Boolean = false,
    buchungen: List<Buchung> = emptyList(),
    skalierung: Float = 1f,
    beiTipp: (() -> Unit)? = null,
    beiLangemDruck: (() -> Unit)? = null,
    beiAlleBuchungen: () -> Unit = {},
) {
    val material = materialFuer(karte.stufe)
    val reduziert = RelockTheme.reduzierteBewegung

    val winkel by animateFloatAsState(
        targetValue = if (gedreht) 180f else 0f,
        animationSpec = tween(durationMillis = if (reduziert) 0 else 500),
        label = "Karte drehen",
    )

    // Der Glanzstreifen. Im Ruhezustand wandert er alle 8 Sekunden einmal
    // langsam ueber die Karte, beim Ziehen folgt er dem Finger.
    val glanz = remember { Animatable(0.25f) }
    val bereich = rememberCoroutineScope()
    LaunchedEffect(reduziert, gedreht) {
        if (reduziert || gedreht) return@LaunchedEffect
        while (true) {
            delay(8000)
            glanz.animateTo(1.15f, tween(1600, easing = LinearEasing))
            glanz.snapTo(-0.15f)
            glanz.animateTo(0.25f, tween(600, easing = LinearEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(Mass.KARTE_VERHAELTNIS)
            .graphicsLayer {
                scaleX = skalierung
                scaleY = skalierung
                rotationY = winkel
                cameraDistance = 14f * density
            }
            // Der einzige Schatten der App, DESIGN.md 2.4.
            .shadow(
                elevation = 24.dp,
                shape = Radius.Karte,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .kartenmetall(material, glanz.value)
            .then(
                if (beiTipp != null || beiLangemDruck != null) {
                    Modifier.combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { beiTipp?.invoke() },
                        onLongClick = beiLangemDruck,
                    )
                } else {
                    Modifier
                },
            )
            .pointerInput(reduziert) {
                if (reduziert) return@pointerInput
                detectHorizontalDragGestures { _, verschiebung ->
                    bereich.launch {
                        val neu = (glanz.value + verschiebung / size.width).coerceIn(-0.2f, 1.2f)
                        glanz.snapTo(neu)
                    }
                }
            },
    ) {
        if (winkel <= 90f) {
            Vorderseite(karte = karte, material = material)
        } else {
            // Zweite Drehung, damit die Rueckseite nicht spiegelverkehrt steht.
            Box(Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack(
                    material = material,
                    buchungen = buchungen,
                    beiAlleBuchungen = beiAlleBuchungen,
                )
            }
        }
    }
}

// --- Vorderseite -----------------------------------------------------------

@Composable
private fun Vorderseite(
    karte: Karte,
    material: Kartenmaterial,
) {
    val zustand = karte.zustand

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Gravur(
                text = "Relock",
                stil = Kartenwortmarke,
                material = material,
                modifier = Modifier.weight(1f),
            )
            Gravur(
                text = karte.stufe.anzeige,
                stil = MaterialTheme.typography.labelSmall,
                material = material,
                deckkraft = 0.85f,
            )
        }

        Abstand(10.dp)
        Schlossemblem(material)

        Box(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.Bottom) {
            Gravur(
                text = dauer(karte.guthabenMinuten),
                stil = MaterialTheme.typography.displayMedium,
                material = material,
                // In der Nachsperre ist das Guthaben da, aber noch nicht nutzbar.
                deckkraft = if (zustand is Kartenzustand.Nachsperre) 0.5f else 1f,
            )
        }
        Gravur(
            text = "verfügbar · ${dauer(karte.gesamtMinuten)} gesammelt",
            stil = MaterialTheme.typography.labelSmall,
            material = material,
            deckkraft = 0.8f,
        )

        when (zustand) {
            is Kartenzustand.Nachsperre -> Gravur(
                text = "frei ab ${zustand.freiAb}",
                stil = MaterialTheme.typography.labelSmall,
                material = material,
                deckkraft = 0.8f,
            )

            is Kartenzustand.Vorgemerkt -> Text(
                text = "+${zustand.minuten} Min vorgemerkt",
                style = MaterialTheme.typography.labelSmall,
                color = Messing,
            )

            is Kartenzustand.Freigeschaltet -> Text(
                text = "frei bis ${zustand.bis}",
                style = MaterialTheme.typography.labelSmall,
                color = Messing,
            )

            Kartenzustand.Normal -> Unit
        }

        Box(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Gravur(
                text = karte.name.uppercase(),
                stil = Kartengravur,
                material = material,
                modifier = Modifier.weight(1f),
            )
            Gravur(
                text = "SEIT ${karte.seit}",
                stil = Kartengravur,
                material = material,
                deckkraft = 0.85f,
            )
        }
    }
}

/**
 * Das Schloss-Emblem sitzt dort, wo bei echten Karten der Chip sitzt.
 * Abgerundetes Rechteck 40 x 30 dp mit feinen Linien, die ein Schloss andeuten,
 * in einem leicht helleren Ton des Materials.
 */
@Composable
private fun Schlossemblem(material: Kartenmaterial) {
    val ton = material.emblem
    val kontur = material.gravur.copy(alpha = 0.55f)

    Box(
        Modifier
            .size(width = 40.dp, height = 30.dp)
            .background(ton.copy(alpha = 0.75f), Radius.Eingabe)
            .border(1.dp, kontur.copy(alpha = 0.4f), Radius.Eingabe)
            .drawBehind {
                val strich = 1.5.dp.toPx()
                val breite = size.width
                val hoehe = size.height

                // Buegel
                val buegelBreite = breite * 0.30f
                val buegelHoehe = hoehe * 0.26f
                val mitte = breite / 2f
                val buegelUnten = hoehe * 0.44f
                drawArc(
                    color = kontur,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(mitte - buegelBreite / 2f, buegelUnten - buegelHoehe),
                    size = Size(buegelBreite, buegelHoehe * 2f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strich,
                        cap = StrokeCap.Round,
                    ),
                )

                // Korpus
                drawRoundRect(
                    color = kontur,
                    topLeft = Offset(mitte - breite * 0.22f, buegelUnten),
                    size = Size(breite * 0.44f, hoehe * 0.34f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strich),
                )
            },
    )
}

// --- Rueckseite ------------------------------------------------------------

/**
 * Die Rueckseite, DESIGN.md 3: Magnetstreifen-Andeutung oben und der
 * Kontoauszug mit den letzten sechs Buchungen.
 */
@Composable
fun CardBack(
    material: Kartenmaterial,
    buchungen: List<Buchung>,
    modifier: Modifier = Modifier,
    beiAlleBuchungen: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize().then(modifier)) {
        Abstand(14.dp)
        // Magnetstreifen
        Box(
            Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Color.Black.copy(alpha = 0.55f)),
        )

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            buchungen.take(6).forEach { buchung ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Gravur(
                        text = buchung.datum,
                        stil = MaterialTheme.typography.labelSmall,
                        material = material,
                        deckkraft = 0.7f,
                    )
                    Abstand(breite = 10.dp)
                    Gravur(
                        text = buchung.text,
                        stil = MaterialTheme.typography.labelSmall,
                        material = material,
                        modifier = Modifier.weight(1f),
                        deckkraft = 0.9f,
                    )
                    Gravur(
                        text = vorzeichen(buchung.minuten),
                        stil = MaterialTheme.typography.labelSmall,
                        material = material,
                    )
                }
            }

            Box(Modifier.weight(1f))

            Box(
                Modifier
                    .fillMaxWidth()
                    .combinedClickableEinfach(beiAlleBuchungen),
            ) {
                Gravur(
                    text = "Alle anzeigen",
                    stil = MaterialTheme.typography.labelSmall,
                    material = material,
                    deckkraft = 0.75f,
                )
            }
        }
    }
}

private fun vorzeichen(minuten: Int): String =
    if (minuten >= 0) "+$minuten" else "$minuten"

// --- Bausteine -------------------------------------------------------------

/**
 * Text mit Gravur-Effekt, DESIGN.md 4: ein Stueck heller nach unten
 * versetzt, ein Stueck dunkler nach oben, dazwischen der eigentliche Text.
 * So sieht die Schrift eingepraegt aus statt aufgedruckt.
 */
@Composable
private fun Gravur(
    text: String,
    stil: TextStyle,
    material: Kartenmaterial,
    modifier: Modifier = Modifier,
    deckkraft: Float = 1f,
) {
    Box(modifier) {
        Text(
            text = text,
            style = stil,
            color = Color.White.copy(alpha = 0.16f * deckkraft),
            modifier = Modifier.graphicsLayer { translationY = 1.dp.toPx() },
        )
        Text(
            text = text,
            style = stil,
            color = Color.Black.copy(alpha = 0.28f * deckkraft),
            modifier = Modifier.graphicsLayer { translationY = -1.dp.toPx() },
        )
        Text(
            text = text,
            style = stil,
            color = material.gravur.copy(alpha = deckkraft),
        )
    }
}

/**
 * Das Metall: Verlauf im Winkel 115 Grad, feine Buerstung, Glanzstreifen,
 * Rand. Bei Obsidian schimmert der Rand, sonst ist er einfarbig.
 *
 * @param glanz Position des Lichtstreifens, 0 bis 1 ueber die Breite
 */
private fun Modifier.kartenmetall(
    material: Kartenmaterial,
    glanz: Float,
): Modifier = this
    .drawBehind {
        // Verlauf 115 Grad: von oben links nach unten rechts, leicht flach.
        val ende = Offset(size.width, size.height * 0.92f)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = material.verlauf,
                start = Offset.Zero,
                end = ende,
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
        )

        // Buerstung: waagerechte Linien, 3 Prozent.
        val abstand = 3.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += abstand
        }
    }
    .drawWithContent {
        drawContent()
        // Glanzstreifen: diagonales Band, laeuft ueber die Karte.
        val mitte = size.width * glanz
        val breite = size.width * 0.22f
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.10f),
                    Color.White.copy(alpha = 0.16f),
                    Color.White.copy(alpha = 0.10f),
                    Color.Transparent,
                ),
                start = Offset(mitte - breite, size.height),
                end = Offset(mitte + breite, 0f),
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
        )
    }
    .kartenrand(material)

/** Einfarbiger Rand, bei Obsidian ein schimmernder Verlauf. */
private fun Modifier.kartenrand(material: Kartenmaterial): Modifier =
    if (material.rand.size == 1) {
        this.border(material.randBreite, material.rand.first(), Radius.Karte)
    } else {
        this.border(
            width = material.randBreite,
            brush = Brush.linearGradient(material.rand),
            shape = Radius.Karte,
        )
    }

/** Klickbar ohne Wellenring, damit auf der Karte nichts aufblitzt. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Modifier.combinedClickableEinfach(beiKlick: () -> Unit): Modifier =
    this.combinedClickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = beiKlick,
    )

/** Senkrechter oder waagerechter Abstand. */
@Composable
private fun Abstand(
    hoehe: androidx.compose.ui.unit.Dp = 0.dp,
    breite: androidx.compose.ui.unit.Dp = 0.dp,
) {
    Box(Modifier.height(hoehe).width(breite))
}
