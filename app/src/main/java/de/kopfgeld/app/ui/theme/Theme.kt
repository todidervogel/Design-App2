package de.kopfgeld.app.ui.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*
 * Theme, DESIGN.md 3 und 4.
 *
 * Wichtig: Tafel ist kein Dark Mode, sondern ein Zustand. Box, laufende
 * Phasen und Pause sind IMMER im Tafel-Look, egal welches Systemtheme.
 * Dafuer gibt es TafelTheme. Das dunkle colorScheme existiert trotzdem,
 * fuer alle normalen Screens im Dark Mode.
 *
 * Keine Dynamic Colors.
 */

// --- Heft (hell) -----------------------------------------------------------

private val HeftSchema = lightColorScheme(
    primary = Tinte,
    onPrimary = Papier,
    primaryContainer = TinteHauch,
    onPrimaryContainer = Tinte,

    secondary = Blei,
    onSecondary = Papier,
    secondaryContainer = PapierTief,
    onSecondaryContainer = Graphit,

    tertiary = Blei,
    onTertiary = Papier,
    tertiaryContainer = PapierTief,
    onTertiaryContainer = Graphit,

    error = Korrektur,
    onError = Papier,
    errorContainer = KorrekturHauch,
    onErrorContainer = Korrektur,

    background = Papier,
    onBackground = Graphit,
    surface = Papier,
    onSurface = Graphit,
    surfaceVariant = PapierTief,
    onSurfaceVariant = Blei,

    // Kein Farbstich durch Tonal Elevation: Tint gleich Flaeche.
    surfaceTint = Papier,
    inverseSurface = Graphit,
    inverseOnSurface = Papier,
    inversePrimary = TinteHauch,

    outline = Karo,
    outlineVariant = Karo,
    scrim = Graphit,

    surfaceBright = Papier,
    surfaceDim = PapierTief,
    surfaceContainerLowest = Papier,
    surfaceContainerLow = Papier,
    surfaceContainer = PapierTief,
    surfaceContainerHigh = PapierTief,
    surfaceContainerHighest = PapierTief,
)

// --- Tafel (dunkel) --------------------------------------------------------

private val TafelSchema = darkColorScheme(
    primary = TinteTafel,
    onPrimary = Tafel,
    primaryContainer = TafelLinie,
    onPrimaryContainer = Kreide,

    secondary = KreideMatt,
    onSecondary = Tafel,
    secondaryContainer = TafelHell,
    onSecondaryContainer = Kreide,

    tertiary = KreideMatt,
    onTertiary = Tafel,
    tertiaryContainer = TafelHell,
    onTertiaryContainer = Kreide,

    error = KorrekturTafel,
    onError = Tafel,
    errorContainer = TafelHell,
    onErrorContainer = KorrekturTafel,

    background = Tafel,
    onBackground = Kreide,
    surface = Tafel,
    onSurface = Kreide,
    surfaceVariant = TafelHell,
    onSurfaceVariant = KreideMatt,

    surfaceTint = Tafel,
    inverseSurface = Kreide,
    inverseOnSurface = Tafel,
    inversePrimary = Tinte,

    outline = TafelLinie,
    outlineVariant = TafelLinie,
    scrim = Color.Black,

    surfaceBright = TafelHell,
    surfaceDim = Tafel,
    surfaceContainerLowest = Tafel,
    surfaceContainerLow = Tafel,
    surfaceContainer = TafelHell,
    surfaceContainerHigh = TafelHell,
    surfaceContainerHighest = TafelLinie,
)

// --- Zusatzfarben ----------------------------------------------------------

/**
 * Farben, fuer die Material 3 keinen Platz hat. Sie wechseln mit der
 * Farbwelt mit, damit Komponenten nicht selbst zwischen Heft und Tafel
 * unterscheiden muessen.
 */
data class Stiftkasten(
    /** Rasterlinien. */
    val karo: Color,
    /** Sekundaertext, Meta. */
    val blei: Color,
    /** Fueller-Koenigsblau. */
    val tinte: Color,
    /** Flaeche hinter Tinte. */
    val tinteHauch: Color,
    /** gesperrt, Fehler, Luecke, unterbrochen. Nie Deko. */
    val korrektur: Color,
    /** Bannerflaeche fuer Korrektur. */
    val korrekturHauch: Color,
    /** Nur Flaeche hinter Zahlen, nie Textfarbe. */
    val marker: Color,
    /** Nur fuer das Urteil "richtig" in Korrekturen. */
    val richtig: Color,
    /** Tiefere Ebene statt Schatten. */
    val tief: Color,
    /** true, wenn gerade die Tafel-Welt gilt. */
    val istTafel: Boolean,
)

private val HeftStifte = Stiftkasten(
    karo = Karo,
    blei = Blei,
    tinte = Tinte,
    tinteHauch = TinteHauch,
    korrektur = Korrektur,
    korrekturHauch = KorrekturHauch,
    marker = Marker,
    richtig = Richtig,
    tief = PapierTief,
    istTafel = false,
)

private val TafelStifte = Stiftkasten(
    karo = TafelLinie,
    blei = KreideMatt,
    tinte = TinteTafel,
    tinteHauch = TafelLinie,
    korrektur = KorrekturTafel,
    korrekturHauch = TafelHell,
    marker = Marker,
    richtig = RichtigTafel,
    tief = TafelHell,
    istTafel = true,
)

val LocalStiftkasten = staticCompositionLocalOf { HeftStifte }

/**
 * true, wenn das System Animationen abgeschaltet hat. DESIGN.md 4.4:
 * beide inszenierten Momente muessen das respektieren.
 */
val LocalReduzierteBewegung = compositionLocalOf { false }

// --- Themes ----------------------------------------------------------------

@Composable
fun KopfgeldTheme(
    dunkel: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val kontext = LocalContext.current
    val reduziert = remember(kontext) {
        runCatching {
            Settings.Global.getFloat(
                kontext.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        }.getOrDefault(false)
    }

    CompositionLocalProvider(
        LocalStiftkasten provides if (dunkel) TafelStifte else HeftStifte,
        LocalReduzierteBewegung provides reduziert,
    ) {
        MaterialTheme(
            colorScheme = if (dunkel) TafelSchema else HeftSchema,
            typography = KopfgeldTypography,
            shapes = KopfgeldShapes,
            content = content,
        )
    }
}

/**
 * Erzwingt die Tafel-Welt, unabhaengig vom Systemtheme. Fuer Box, laufende
 * Phasen, Countdown und Pause. Siehe DESIGN.md 3.
 */
@Composable
fun TafelTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalStiftkasten provides TafelStifte) {
        MaterialTheme(
            colorScheme = TafelSchema,
            typography = KopfgeldTypography,
            shapes = KopfgeldShapes,
            content = content,
        )
    }
}

/** Zugriff auf die Zusatzfarben: `KopfgeldTheme.stifte.marker`. */
object KopfgeldTheme {
    val stifte: Stiftkasten
        @Composable @ReadOnlyComposable get() = LocalStiftkasten.current

    val reduzierteBewegung: Boolean
        @Composable @ReadOnlyComposable get() = LocalReduzierteBewegung.current
}

/** Farbschemata, falls ein Screen sie direkt braucht (z. B. Screen-Katalog). */
val HeftColorScheme: ColorScheme get() = HeftSchema
val TafelColorScheme: ColorScheme get() = TafelSchema
