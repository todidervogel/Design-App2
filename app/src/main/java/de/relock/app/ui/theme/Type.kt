package de.relock.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import de.relock.app.R

/*
 * Schrift, DESIGN.md 2.3.
 *
 *   Fraunces  Ueberschriften und grosse Zahlen (Karte, Timer, Countdown)
 *   Manrope   alles andere
 *
 * Geladen ueber Downloadable Google Fonts. Faellt der Anbieter aus (kein
 * Play Services, kein Netz), nimmt Compose die naechste Familie in der Liste,
 * am Ende die Systemschrift.
 */

private val anbieter = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val fraunces = GoogleFont("Fraunces")
private val manrope = GoogleFont("Manrope")

@OptIn(ExperimentalTextApi::class)
val Fraunces = FontFamily(
    Font(googleFont = fraunces, fontProvider = anbieter, weight = FontWeight.Normal),
    Font(googleFont = fraunces, fontProvider = anbieter, weight = FontWeight.Medium),
    Font(googleFont = fraunces, fontProvider = anbieter, weight = FontWeight.SemiBold),
)

@OptIn(ExperimentalTextApi::class)
val Manrope = FontFamily(
    Font(googleFont = manrope, fontProvider = anbieter, weight = FontWeight.Normal),
    Font(googleFont = manrope, fontProvider = anbieter, weight = FontWeight.Medium),
    Font(googleFont = manrope, fontProvider = anbieter, weight = FontWeight.SemiBold),
    Font(googleFont = manrope, fontProvider = anbieter, weight = FontWeight.Bold),
)

/**
 * Tabellenziffern und Versalziffern. Ohne das zappeln Timer und Guthaben
 * bei jedem Wechsel.
 */
private const val ZIFFERN = "tnum, lnum"

val RelockTypography = Typography(
    // Timer, Countdown
    displayLarge = TextStyle(
        fontFamily = Fraunces,
        fontWeight = FontWeight.Normal,
        fontSize = 96.sp,
        lineHeight = 100.sp,
        fontFeatureSettings = ZIFFERN,
    ),
    // Guthaben auf der Karte
    displayMedium = TextStyle(
        fontFamily = Fraunces,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        fontFeatureSettings = ZIFFERN,
    ),
    // Timer in der Box
    displaySmall = TextStyle(
        fontFamily = Fraunces,
        fontWeight = FontWeight.Normal,
        fontSize = 64.sp,
        lineHeight = 68.sp,
        fontFeatureSettings = ZIFFERN,
    ),
    // Screen-Titel
    headlineMedium = TextStyle(
        fontFamily = Fraunces,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontFeatureSettings = ZIFFERN,
    ),
    headlineSmall = TextStyle(
        fontFamily = Fraunces,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontFeatureSettings = ZIFFERN,
    ),
    // Abschnitte
    titleMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    // Buttons
    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

/**
 * Gravur auf der Karte: Name und Datum. Die einzige Stelle der App mit
 * Grossbuchstaben und weiter Laufweite - so steht es auf echten Karten.
 */
val Kartengravur = TextStyle(
    fontFamily = Manrope,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.14.em,
    fontFeatureSettings = ZIFFERN,
)

/** Die Wortmarke oben links auf der Karte. */
val Kartenwortmarke = TextStyle(
    fontFamily = Fraunces,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 22.sp,
)
