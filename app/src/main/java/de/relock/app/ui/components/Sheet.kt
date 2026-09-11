package de.relock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.relock.app.ui.theme.Elfenbein
import de.relock.app.ui.theme.Mass
import de.relock.app.ui.theme.Radius
import de.relock.app.ui.theme.RelockTheme

/**
 * Ein Sheet im Relock-Look: Flaeche als Grund, 28 dp oben rund,
 * ein feiner Griff statt des Material-Standards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelockSheet(
    beiSchliessen: () -> Unit,
    modifier: Modifier = Modifier,
    titel: String? = null,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    val zustand = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = beiSchliessen,
        sheetState = zustand,
        shape = Radius.Sheet,
        containerColor = RelockTheme.farben.flaeche,
        contentColor = Elfenbein,
        dragHandle = { Sheetgriff() },
        modifier = modifier,
    ) {
        if (titel != null) {
            Text(
                text = titel,
                style = MaterialTheme.typography.headlineMedium,
                color = Elfenbein,
                modifier = Modifier.padding(
                    start = Mass.Rand,
                    end = Mass.Rand,
                    bottom = Mass.Mittel,
                ),
            )
        }
        inhalt()
        Luft(Mass.Gross)
    }
}

@Composable
private fun Sheetgriff() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(36.dp)
                .height(3.dp)
                .background(RelockTheme.farben.linie, Radius.Chip),
        )
    }
}

/**
 * Der Inhalt eines Sheets ohne den Sheet-Rahmen. So kann der Screen-Katalog
 * dieselben Inhalte als ganzen Screen zeigen, ohne ein Sheet zu oeffnen.
 */
@Composable
fun SheetInhalt(
    titel: String,
    modifier: Modifier = Modifier,
    inhalt: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(RelockTheme.farben.flaeche)
            .padding(top = Mass.Gross),
    ) {
        Text(
            text = titel,
            style = MaterialTheme.typography.headlineMedium,
            color = Elfenbein,
            modifier = Modifier.padding(horizontal = Mass.Rand, vertical = Mass.Klein),
        )
        inhalt()
        Luft(Mass.Gross)
    }
}
