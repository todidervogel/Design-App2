package de.kopfgeld.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import de.kopfgeld.app.R

/*
 * Schrift, DESIGN.md 4.2. Drei Familien, klar getrennt:
 *
 *   Bricolage Grotesque  Ueberschriften und grosse Zahlen, immer mit "tnum"
 *   Atkinson Hyperlegible  Fliesstext, Buttons, Listen
 *   Caveat  ausschliesslich fuer die roten Korrekturstift-Notizen
 *
 * Geladen ueber Downloadable Google Fonts. Wenn der Provider fehlt
 * (kein Play Services, kein Netz), faellt Compose auf die jeweils zweite
 * Familie zurueck, damit die App nie ohne Schrift dasteht.
 */

private val anbieter = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val bricolage = GoogleFont("Bricolage Grotesque")
private val atkinson = GoogleFont("Atkinson Hyperlegible")
private val caveat = GoogleFont("Caveat")

@OptIn(ExperimentalTextApi::class)
val Bricolage = FontFamily(
    Font(googleFont = bricolage, fontProvider = anbieter, weight = FontWeight.Normal),
    Font(googleFont = bricolage, fontProvider = anbieter, weight = FontWeight.Medium),
    Font(googleFont = bricolage, fontProvider = anbieter, weight = FontWeight.SemiBold),
    Font(googleFont = bricolage, fontProvider = anbieter, weight = FontWeight.Bold),
)

@OptIn(ExperimentalTextApi::class)
val Atkinson = FontFamily(
    Font(googleFont = atkinson, fontProvider = anbieter, weight = FontWeight.Normal),
    Font(googleFont = atkinson, fontProvider = anbieter, weight = FontWeight.Bold),
)

/** Nur fuer CorrectionNote. Sonst nirgendwo. */
@OptIn(ExperimentalTextApi::class)
val CaveatFamilie = FontFamily(
    Font(googleFont = caveat, fontProvider = anbieter, weight = FontWeight.Normal),
    Font(googleFont = caveat, fontProvider = anbieter, weight = FontWeight.Bold),
)

/** Tabellenziffern: gleiche Breite, damit Timer nicht zappeln. */
private const val TNUM = "tnum"

val KopfgeldTypography = Typography(
    // Timer, Countdown
    displayLarge = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.SemiBold,
        fontSize = 88.sp,
        lineHeight = 92.sp,
        fontFeatureSettings = TNUM,
    ),
    // Guthaben, Note
    displayMedium = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        fontFeatureSettings = TNUM,
    ),
    displaySmall = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        fontFeatureSettings = TNUM,
    ),
    // Screen-Titel
    headlineMedium = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontFeatureSettings = TNUM,
    ),
    headlineSmall = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 30.sp,
        fontFeatureSettings = TNUM,
    ),
    // Abschnitte
    titleMedium = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
        fontFeatureSettings = TNUM,
    ),
    titleSmall = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontFeatureSettings = TNUM,
    ),
    bodyLarge = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 26.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    // Buttons
    labelLarge = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Atkinson,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
)
