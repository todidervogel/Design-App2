package de.relock.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Die fuenf Kartenstufen, DESIGN.md 2.2.
 *
 * Jede Stufe ist ein Metall: ein linearer Verlauf aus drei Farbstopps im
 * Winkel 115 Grad, darueber feine Buerstung (waagerechte Linien, 3 Prozent)
 * und ein Glanzstreifen. Gezeichnet wird das in ui/components/RelockCard.kt.
 */
enum class Kartenstufe(val anzeige: String) {
    Graphit("Graphit"),
    Bronze("Bronze"),
    Silber("Silber"),
    Gold("Gold"),
    Obsidian("Obsidian"),
}

/**
 * @param verlauf drei Farbstopps, dunkel - hell - dunkel
 * @param gravur Schriftfarbe auf diesem Metall
 * @param rand Randfarben. Ein Wert heisst einfarbig, mehrere heissen
 *   schimmernder Verlauf (nur Obsidian).
 */
data class Kartenmaterial(
    val stufe: Kartenstufe,
    val verlauf: List<Color>,
    val gravur: Color,
    val rand: List<Color>,
    val randBreite: Dp = 1.dp,
) {
    /** Etwas hellerer Ton des Materials, fuer das Schloss-Emblem. */
    val emblem: Color get() = verlauf[1]
}

private val GRAPHIT = Kartenmaterial(
    stufe = Kartenstufe.Graphit,
    verlauf = listOf(Color(0xFF2B2F36), Color(0xFF3C414A), Color(0xFF262A30)),
    gravur = Color(0xFF9AA0A8),
    rand = listOf(Color(0xFF4A505A)),
)

private val BRONZE = Kartenmaterial(
    stufe = Kartenstufe.Bronze,
    verlauf = listOf(Color(0xFF5E3A25), Color(0xFF9A6440), Color(0xFF6B432B)),
    gravur = Color(0xFFF0D2B4),
    rand = listOf(Color(0xFFB07A52)),
)

private val SILBER = Kartenmaterial(
    stufe = Kartenstufe.Silber,
    verlauf = listOf(Color(0xFF8E949C), Color(0xFFD9DDE2), Color(0xFF9DA3AB)),
    gravur = Color(0xFF2C3036),
    rand = listOf(Color(0xFFE8EBEE)),
)

private val GOLD = Kartenmaterial(
    stufe = Kartenstufe.Gold,
    verlauf = listOf(Color(0xFF7F6328), Color(0xFFC9A35B), Color(0xFF8E6F2E)),
    gravur = Color(0xFF2A2110),
    rand = listOf(Color(0xFFE6CB8E)),
)

private val OBSIDIAN = Kartenmaterial(
    stufe = Kartenstufe.Obsidian,
    verlauf = listOf(Color(0xFF07080B), Color(0xFF15171D), Color(0xFF08090C)),
    gravur = Color(0xFFC9C3B6),
    // Der einzige schimmernde Rand der App. Deshalb auch doppelt so breit.
    rand = listOf(Color(0xFF5B6CFF), Color(0xFFB45BFF), Color(0xFF5BE0D0)),
    randBreite = 2.dp,
)

/** Material zu einer Stufe. */
fun materialFuer(stufe: Kartenstufe): Kartenmaterial = when (stufe) {
    Kartenstufe.Graphit -> GRAPHIT
    Kartenstufe.Bronze -> BRONZE
    Kartenstufe.Silber -> SILBER
    Kartenstufe.Gold -> GOLD
    Kartenstufe.Obsidian -> OBSIDIAN
}

/** Die naechste Stufe, oder null bei Obsidian. */
fun naechsteStufe(stufe: Kartenstufe): Kartenstufe? =
    Kartenstufe.entries.getOrNull(stufe.ordinal + 1)
