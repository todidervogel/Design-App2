package de.relock.app.ui.theme

import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*
 * Theme, DESIGN.md 2.
 *
 * Version 1 hat genau ein Theme: dunkel. Kein Hell-Dunkel-Umschalten, keine
 * Dynamic Colors. Die Privatbank hat abends geoeffnet.
 */

private val RelockSchema = darkColorScheme(
    primary = Messing,
    onPrimary = Nacht,
    primaryContainer = MessingTief,
    onPrimaryContainer = MessingHell,

    secondary = ElfenbeinMatt,
    onSecondary = Nacht,
    secondaryContainer = FlaecheHoch,
    onSecondaryContainer = Elfenbein,

    // Bewusst nicht Richtig: das gehoert nur in Korrekturen und soll nicht
    // versehentlich ueber eine Material-Vorgabe irgendwo auftauchen.
    tertiary = ElfenbeinMatt,
    onTertiary = Nacht,
    tertiaryContainer = FlaecheHoch,
    onTertiaryContainer = Elfenbein,

    error = Signal,
    onError = Nacht,
    errorContainer = SignalTief,
    onErrorContainer = Signal,

    background = Nacht,
    onBackground = Elfenbein,
    surface = Nacht,
    onSurface = Elfenbein,
    surfaceVariant = FlaecheHoch,
    onSurfaceVariant = ElfenbeinMatt,

    // Tint gleich Flaeche: so faerbt Material die Flaechen nicht selbst ein.
    // Tiefe entsteht bei Relock nur ueber Nacht, Flaeche, FlaecheHoch.
    surfaceTint = Nacht,
    inverseSurface = Elfenbein,
    inverseOnSurface = Nacht,
    inversePrimary = MessingTief,

    outline = Linie,
    outlineVariant = Linie,
    scrim = Color.Black,

    surfaceBright = FlaecheHoch,
    surfaceDim = Nacht,
    surfaceContainerLowest = Nacht,
    surfaceContainerLow = Nacht,
    surfaceContainer = Flaeche,
    surfaceContainerHigh = Flaeche,
    surfaceContainerHighest = FlaecheHoch,
)

/**
 * Farben, fuer die Material 3 keinen Platz hat. Sie liegen hier statt als
 * lose Konstanten, damit spaetere Themes sie mitwechseln koennen.
 */
data class Relockfarben(
    /** Sheets, abgesetzte Bereiche. */
    val flaeche: Color,
    /** Eingabefelder, gedrueckte Zeilen. */
    val flaecheHoch: Color,
    /** Trennlinien, Rahmen. */
    val linie: Color,
    /** Sekundaertext. */
    val matt: Color,
    /** Hinweise, deaktiviert. */
    val leise: Color,
    /** Messing auf Messing. */
    val messingHell: Color,
    /** Messing-Hauch fuer ausgewaehlte Flaechen. */
    val messingTief: Color,
    /** Flaeche hinter Signal. */
    val signalTief: Color,
    /** Nur in Korrekturen. */
    val richtig: Color,
    val richtigTief: Color,
)

private val Standardfarben = Relockfarben(
    flaeche = Flaeche,
    flaecheHoch = FlaecheHoch,
    linie = Linie,
    matt = ElfenbeinMatt,
    leise = ElfenbeinLeise,
    messingHell = MessingHell,
    messingTief = MessingTief,
    signalTief = SignalTief,
    richtig = Richtig,
    richtigTief = RichtigTief,
)

val LocalRelockfarben = compositionLocalOf { Standardfarben }

/**
 * true, wenn das System Animationen abgeschaltet hat. DESIGN.md 2.5:
 * alle drei inszenierten Momente muessen das respektieren.
 */
val LocalReduzierteBewegung = compositionLocalOf { false }

@Composable
fun RelockTheme(content: @Composable () -> Unit) {
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
        LocalRelockfarben provides Standardfarben,
        LocalReduzierteBewegung provides reduziert,
    ) {
        MaterialTheme(
            colorScheme = RelockSchema,
            typography = RelockTypography,
            shapes = RelockShapes,
            content = content,
        )
    }
}

/** Zugriff auf die Zusatzfarben: `RelockTheme.farben.matt`. */
object RelockTheme {
    val farben: Relockfarben
        @Composable @ReadOnlyComposable get() = LocalRelockfarben.current

    val reduzierteBewegung: Boolean
        @Composable @ReadOnlyComposable get() = LocalReduzierteBewegung.current
}
