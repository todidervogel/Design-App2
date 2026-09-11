package de.kopfgeld.app.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.kopfgeld.app.feature.session.ZyklusSheet
import de.kopfgeld.app.ui.components.SymbolBrain
import de.kopfgeld.app.ui.components.SymbolFortschritt
import de.kopfgeld.app.ui.components.SymbolHeute
import de.kopfgeld.app.ui.components.SymbolLernen
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.theme.KopfgeldTheme

/**
 * Wurzel der App. Ein flacher NavHost, DESIGN.md 6.
 *
 * Die Bottom Navigation erscheint nur auf den vier Tab-Routen. Alle
 * Vollbild-Flows (Onboarding, Session, Import, Probearbeit, Sperrbildschirm)
 * verdraengen sie.
 */
@Composable
fun KopfgeldApp() {
    val navigation = rememberNavController()
    val zustand = rememberAppZustand()

    val eintrag by navigation.currentBackStackEntryAsState()
    val aktuelleRoute = eintrag?.destination?.route
    // Nicht `aktuelleRoute in Ziel.tabs`: das waere String? gegen List<String>.
    val zeigeLeiste = aktuelleRoute != null && aktuelleRoute in Ziel.tabs

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (zeigeLeiste) {
                UntereLeiste(
                    aktuelleRoute = aktuelleRoute,
                    beiWechsel = { route -> navigation.zuTab(route) },
                )
            }
        },
    ) { polster ->
        NavHost(
            navController = navigation,
            startDestination = Ziel.ONBOARDING,
            modifier = Modifier
                .fillMaxSize()
                .padding(polster),
        ) {
            tabZiele(navigation, zustand)
            vollbildZiele(navigation, zustand)
        }
    }

    // Das Zyklus-Sheet liegt ueber allem, damit es die Sessionplanung
    // nicht verlaesst (DESIGN.md 7.5: Bottom Sheet, voll ausklappbar).
    val sheet = zustand.zyklusSheet
    if (sheet != null) {
        ZyklusSheet(
            state = sheet,
            beiSchliessen = zustand::zyklusSheetSchliessen,
            beiFach = zustand::sheetFach,
            beiThemaUmschalten = zustand::sheetThemaUmschalten,
            beiTyp = zustand::sheetTyp,
            beiTypenMischen = zustand::sheetTypenMischen,
            beiLernphase = zustand::sheetLernphase,
            beiAufgabenphase = zustand::sheetAufgabenphase,
            beiAufgabenAutomatisch = zustand::sheetAufgabenAutomatisch,
            beiSelbstAuswaehlen = {
                zustand.zyklusSheetSchliessen()
                navigation.navigate(Ziel.AUFGABENAUSWAHL)
            },
            beiUebernehmen = zustand::sheetUebernehmen,
        )
    }
}

@Composable
private fun UntereLeiste(
    aktuelleRoute: String?,
    beiWechsel: (String) -> Unit,
) {
    Column {
        Trennlinie()
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface,
            // Keine tonale Elevation: Ebenen entstehen ueber Flaechen und
            // Rasterlinien, nicht ueber Schatten (DESIGN.md 4.3).
            tonalElevation = 0.dp,
        ) {
            LeistenEintrag(
                titel = "Heute",
                route = Ziel.HEUTE,
                aktuelleRoute = aktuelleRoute,
                beiWechsel = beiWechsel,
            ) { gewaehlt -> SymbolHeute(gewaehlt) }

            LeistenEintrag(
                titel = "Lernen",
                route = Ziel.LERNEN,
                aktuelleRoute = aktuelleRoute,
                beiWechsel = beiWechsel,
            ) { gewaehlt -> SymbolLernen(gewaehlt) }

            LeistenEintrag(
                titel = "Brain",
                route = Ziel.BRAIN,
                aktuelleRoute = aktuelleRoute,
                beiWechsel = beiWechsel,
            ) { gewaehlt -> SymbolBrain(gewaehlt) }

            LeistenEintrag(
                titel = "Fortschritt",
                route = Ziel.FORTSCHRITT,
                aktuelleRoute = aktuelleRoute,
                beiWechsel = beiWechsel,
            ) { gewaehlt -> SymbolFortschritt(gewaehlt) }
        }
    }
}

@Composable
private fun RowScope.LeistenEintrag(
    titel: String,
    route: String,
    aktuelleRoute: String?,
    beiWechsel: (String) -> Unit,
    symbol: @Composable (Boolean) -> Unit,
) {
    val stifte = KopfgeldTheme.stifte
    val gewaehlt = aktuelleRoute == route
    NavigationBarItem(
        selected = gewaehlt,
        onClick = { beiWechsel(route) },
        icon = { symbol(gewaehlt) },
        label = {
            Text(
                text = titel,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = stifte.tinte,
            selectedTextColor = stifte.tinte,
            unselectedIconColor = stifte.blei,
            unselectedTextColor = stifte.blei,
            indicatorColor = Color.Transparent,
        ),
    )
}

private fun NavHostController.zuTab(route: String) {
    navigate(route) {
        popUpTo(Ziel.HEUTE) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
