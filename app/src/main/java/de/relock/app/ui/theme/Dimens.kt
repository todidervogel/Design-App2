package de.relock.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Raster und Abstaende, DESIGN.md 2.4.
 * Grundraster 8 dp, Seitenrand 24 dp.
 */
object Mass {
    /** Seitenrand, links wie rechts. */
    val Rand = 24.dp

    val Winzig = 4.dp
    val Klein = 8.dp
    val Mittel = 16.dp
    val Gross = 24.dp
    val Sehr = 32.dp
    val Weit = 48.dp

    /** Kleinstes Touch-Ziel. */
    val Tippziel = 48.dp

    /** Hoehe eines normalen Buttons. */
    val Buttonhoehe = 54.dp

    /** Seitenverhaeltnis der Relock-Karte (Kreditkarte). */
    const val KARTE_VERHAELTNIS = 1.586f
}
