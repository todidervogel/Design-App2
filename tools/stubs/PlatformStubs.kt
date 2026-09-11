package androidx.compose.ui.platform

import android.content.Context
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Stub. Nur fuer die lokale Typpruefung.
 * Unter Android liefert LocalContext den echten Context.
 */
val LocalContext: ProvidableCompositionLocal<Context> =
    staticCompositionLocalOf { Context() }
