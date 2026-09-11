package de.kopfgeld.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.kopfgeld.app.ui.nav.KopfgeldApp
import de.kopfgeld.app.ui.theme.KopfgeldTheme

/**
 * Einziger Einstiegspunkt. Phase 1 ist reine Oberflaeche: kein Service,
 * keine Berechtigung, kein Hintergrundbetrieb.
 *
 * Bewusst kein enableEdgeToEdge: Zielgeraet ist ein Galaxy S9 mit Android 9,
 * und das Fenster soll unter den Systemleisten bleiben, damit Compose sich
 * nicht selbst um Insets kuemmern muss.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KopfgeldTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KopfgeldApp()
                }
            }
        }
    }
}
