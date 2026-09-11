package de.relock.app.feature.einstellungen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.data.Updatezustand
import de.relock.app.ui.components.Fortschrittsstrich
import de.relock.app.ui.components.Hinweis
import de.relock.app.ui.components.Luft
import de.relock.app.ui.components.LuftBreit
import de.relock.app.ui.components.PrimaryButton
import de.relock.app.ui.components.QuietButton
import de.relock.app.ui.components.SecondaryButton
import de.relock.app.ui.components.SheetInhalt
import de.relock.app.ui.components.Statuspunkt
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Messing
import de.relock.app.ui.theme.RelockTheme
import de.relock.app.ui.theme.Richtig
import de.relock.app.ui.theme.Signal

// ========================================================= 6.24 Update-Dialog

/**
 * Update-Dialog, DESIGN.md 6.24.
 *
 * Der Inhalt eines Sheets. Fuenf Zustaende, alle hier drin, damit man den
 * ganzen Weg vom Angebot bis zur Android-Rueckfrage sehen kann.
 */
@Composable
fun ColumnScope.UpdateInhalt(
    zustand: Updatezustand,
    beiAktualisieren: () -> Unit,
    beiErneutVersuchen: () -> Unit,
    beiSchliessen: () -> Unit,
    beiSpaeter: () -> Unit,
) {
    val farben = RelockTheme.farben

    when (zustand) {
        // --- Es gibt nichts Neues ------------------------------------------
        Updatezustand.Aktuell -> SheetInhalt(titel = "Alles aktuell") {
            Zeileninhalt {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Statuspunkt(farbe = Richtig)
                    LuftBreit(10.dp)
                    Text(
                        text = "Relock ist auf dem neuesten Stand",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                }
            }
            Luft(Mass.Gross)
            Zeileninhalt {
                PrimaryButton(text = "Schließen", beiKlick = beiSchliessen)
            }
        }

        // --- Eine neue Version liegt bereit --------------------------------
        is Updatezustand.Verfuegbar -> SheetInhalt(
            titel = "Relock ${zustand.version} ist da",
        ) {
            Zeileninhalt {
                Column {
                    zustand.aenderungen.take(5).forEach { zeile ->
                        Row(
                            Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Messing,
                            )
                            LuftBreit(10.dp)
                            Text(
                                text = zeile,
                                style = MaterialTheme.typography.bodyMedium,
                                color = farben.matt,
                            )
                        }
                    }
                    Luft(Mass.Mittel)
                    Text(
                        text = zustand.groesse,
                        style = MaterialTheme.typography.labelSmall,
                        color = farben.leise,
                    )
                }
            }
            Luft(Mass.Gross)
            Zeileninhalt {
                Column {
                    PrimaryButton(text = "Jetzt aktualisieren", beiKlick = beiAktualisieren)
                    QuietButton(text = "Später", beiKlick = beiSpaeter)
                }
            }
        }

        // --- Der Download laeuft -------------------------------------------
        is Updatezustand.Laedt -> SheetInhalt(titel = "Update") {
            Zeileninhalt {
                Column {
                    Text(
                        text = "Wird geladen … ${zustand.prozent} %",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                    Luft(Mass.Mittel)
                    Fortschrittsstrich(anteil = zustand.prozent / 100f)
                }
            }
            Luft(Mass.Gross)
            Hinweis(text = "Du kannst das Sheet zumachen, es lädt weiter.")
            Luft(Mass.Klein)
            Zeileninhalt {
                QuietButton(text = "Schließen", beiKlick = beiSchliessen)
            }
        }

        // --- Android uebernimmt --------------------------------------------
        Updatezustand.BereitZumInstallieren -> SheetInhalt(titel = "Fast fertig") {
            Zeileninhalt {
                Text(
                    text = "Android fragt gleich nach, tippe auf Aktualisieren.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Elfenbein,
                )
            }
            Luft(Mass.Gross)
            Zeileninhalt {
                PrimaryButton(text = "Installieren", beiKlick = beiAktualisieren)
            }
        }

        // --- Es hat nicht geklappt -----------------------------------------
        Updatezustand.Fehlgeschlagen -> SheetInhalt(titel = "Update") {
            Zeileninhalt {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Statuspunkt(farbe = Signal)
                    LuftBreit(10.dp)
                    Text(
                        text = "Update konnte nicht geladen werden. Ist dein PC an?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Elfenbein,
                    )
                }
            }
            Luft(Mass.Gross)
            Zeileninhalt {
                Column {
                    SecondaryButton(text = "Erneut versuchen", beiKlick = beiErneutVersuchen)
                    QuietButton(text = "Schließen", beiKlick = beiSchliessen)
                }
            }
        }
    }
}

/** Alles im Sheet haelt denselben Seitenrand. */
@Composable
private fun Zeileninhalt(inhalt: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Mass.Rand),
    ) {
        inhalt()
    }
}
