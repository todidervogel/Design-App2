package de.kopfgeld.app.feature.katalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.ui.components.Abschnittstitel
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.MerkChip
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.nav.Katalog
import de.kopfgeld.app.ui.nav.Katalogeintrag
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass

/**
 * Screen-Katalog, DESIGN.md 6. Erreichbar ueber langen Druck auf das Datum
 * in Heute und ueber die Einstellungen.
 */
@Composable
fun KatalogScreen(
    beiZurueck: () -> Unit,
    beiEintrag: (Katalogeintrag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Screen-Katalog",
        unterzeile = "${Katalog.anzahlEintraege} Screens und Zustände",
        zurueck = beiZurueck,
        scrollbar = false,
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Hinweistext(
                "Phase 1 ist reine Oberfläche. Ohne Logik erreicht man viele Zustände " +
                    "nicht auf normalem Weg, deshalb stehen sie hier alle.",
                Modifier.padding(horizontal = Mass.Seitenrand, vertical = Mass.Klein),
            )
            Hinweistext(
                "„abgeleitet“ heißt: steht nicht im übermittelten Auftragstext, " +
                    "sondern folgt aus den Abläufen und dem Komponentenkatalog.",
                Modifier.padding(horizontal = Mass.Seitenrand),
            )

            Katalog.gruppen.forEach { gruppe ->
                Abschnittstitel(gruppe.name)
                gruppe.eintraege.forEach { eintrag ->
                    RasterZeile(beiKlick = { beiEintrag(eintrag) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = eintrag.titel,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = eintrag.route,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = stifte.blei,
                                )
                            }
                            if (eintrag.quelle.isNotBlank()) {
                                MerkChip("DESIGN.md ${eintrag.quelle}")
                            }
                            if (eintrag.abgeleitet) {
                                LuftBreit(6.dp)
                                MerkChip("abgeleitet", farbe = stifte.korrektur)
                            }
                        }
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}
