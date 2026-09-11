package androidx.compose.ui.text.googlefonts

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Stubs fuer androidx.compose.ui:ui-text-google-fonts.
 * Nur fuer die lokale Typpruefung.
 */
class GoogleFont(name: String, bestEffort: Boolean = true) {
    class Provider(
        providerAuthority: String,
        providerPackage: String,
        certificates: Int,
    )
}

/**
 * Stub mit der Signatur der echten Downloadable-Fonts-Funktion.
 * Der Rumpf ist egal - geprueft wird nur, ob die Aufrufe dazu passen.
 */
@Suppress("FunctionName", "UNUSED_PARAMETER")
fun Font(
    googleFont: GoogleFont,
    fontProvider: GoogleFont.Provider,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal,
): androidx.compose.ui.text.font.Font =
    throw UnsupportedOperationException("Stub fuer die Typpruefung")
