package de.relock.app.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Farben, DESIGN.md 2.1. Ein einziges dunkles Theme in Version 1.
 *
 * Drei Regeln, die leicht verletzt werden:
 *   - Messing ist kostbar: nur Hauptaktionen, aktive Zustaende und Zahlen,
 *     die zaehlen. Nie flaechig, nie als Deko.
 *   - Signal heisst immer dasselbe: gesperrt, Fehler, ueberfaellig, Unterbrechung.
 *   - Richtig kommt nur in Korrekturen vor.
 */

/** Hintergrund. */
val Nacht = Color(0xFF0E1320)

/** Sheets, abgesetzte Bereiche. */
val Flaeche = Color(0xFF151C2B)

/** Eingabefelder, gedrueckte Zeilen. */
val FlaecheHoch = Color(0xFF1C2436)

/** Trennlinien, Rahmen. */
val Linie = Color(0xFF2A3348)

/** Haupttext. */
val Elfenbein = Color(0xFFEFE9DC)

/** Sekundaertext. */
val ElfenbeinMatt = Color(0xFFA7A193)

/** Hinweise, deaktiviert. */
val ElfenbeinLeise = Color(0xFF6E6A62)

/** Primaer: Buttons, aktive Zustaende. */
val Messing = Color(0xFFC9A35B)

/** Messing auf Messing, Hover- und Pressed-Text. */
val MessingHell = Color(0xFFE6CB8E)

/** Messing-Hauch fuer ausgewaehlte Flaechen. */
val MessingTief = Color(0xFF3A3020)

/** Gesperrt, Fehler, ueberfaellig. */
val Signal = Color(0xFFE2594C)
val SignalTief = Color(0xFF3A1D1B)

/** Nur in Korrekturen. */
val Richtig = Color(0xFF7CC49A)
val RichtigTief = Color(0xFF1B3327)
