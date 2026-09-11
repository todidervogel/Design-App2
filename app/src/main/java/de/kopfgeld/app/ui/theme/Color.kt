package de.kopfgeld.app.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Zwei Farbwelten, siehe DESIGN.md 3 und 4.1.
 *
 *   Heft  = kuehles Papierweiss mit Karoraster, Fueller in Koenigsblau
 *   Tafel = Schiefergruen mit Kreideweiss
 *
 * Zwei Regeln, die leicht verletzt werden:
 *   - Marker ist nie Textfarbe, nur Flaeche hinter einer Zahl.
 *   - Richtig gilt nur fuer das Urteil "richtig" in Korrekturen.
 *   - Rot bedeutet immer: gesperrt, Fehler, Luecke, Box unterbrochen. Nie Deko.
 */

// Heft (hell)
val Papier = Color(0xFFF6F8F7)
val PapierTief = Color(0xFFECF0EF)
val Karo = Color(0xFFD5DEE6)
val Graphit = Color(0xFF23272E)
val Blei = Color(0xFF5E6670)
val Tinte = Color(0xFF1D3A9E)
val TinteHauch = Color(0xFFE3E8F7)
val Korrektur = Color(0xFFC8322B)
val KorrekturHauch = Color(0xFFF7E3E1)
val Marker = Color(0xFFF3E03B)
val Richtig = Color(0xFF2F6B4F)

// Tafel (dunkel)
val Tafel = Color(0xFF1F2B27)
val TafelHell = Color(0xFF2A3833)
val TafelLinie = Color(0xFF3A4A44)
val Kreide = Color(0xFFECEEE8)
val KreideMatt = Color(0xFFA9B3AD)
val TinteTafel = Color(0xFF9DB0FF)
val KorrekturTafel = Color(0xFFFF7A70)
val RichtigTafel = Color(0xFF8FD1AE)
