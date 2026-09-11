package de.relock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.relock.app.ui.theme.RelockTheme

/**
 * Einziger Einstiegspunkt. Version 1 ist reine Oberflaeche: kein Service,
 * keine Berechtigung, kein Hintergrundbetrieb.
 *
 * Bewusst kein enableEdgeToEdge: das Fenster bleibt unter den Systemleisten,
 * damit Compose sich nicht um Insets kuemmern muss.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RelockTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RelockApp()
                }
            }
        }
    }
}
