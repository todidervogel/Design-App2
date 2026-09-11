package de.kopfgeld.app.ui.theme

import androidx.compose.ui.unit.dp

/*
 * Raster und Abstaende, DESIGN.md 4.3.
 * Grundraster 8 dp. Alles hier ist ein Vielfaches davon, ausser dem
 * Seitenrand (20 dp) und dem Karokaestchen (20 dp), die so vorgegeben sind.
 */
object Mass {
    /** Seitenrand, linksbuendig. */
    val Seitenrand = 20.dp

    val Winzig = 4.dp
    val Klein = 8.dp
    val Mittel = 16.dp
    val Gross = 24.dp
    val Sehr = 32.dp

    /** Kantenlaenge eines Karokaestchens. */
    val Karokaestchen = 20.dp

    /** Staerke der Rasterlinien. */
    val Karolinie = 1.dp

    /** Deckkraft der Rasterlinien. */
    const val KaroDeckkraft = 0.6f

    /** Kleinstes Touch-Ziel. */
    val Tippziel = 48.dp

    /** Hoehe eines normalen Buttons. */
    val Buttonhoehe = 52.dp

    /** Kantenlaenge des SubjectBadge. */
    val Fachbadge = 32.dp
}
