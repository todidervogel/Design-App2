package de.kopfgeld.app.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Zyklus
import de.kopfgeld.app.data.Zyklustyp
import de.kopfgeld.app.data.formatiereDauer
import de.kopfgeld.app.ui.components.DurationSlider
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.SubjectBadge
import de.kopfgeld.app.ui.components.TopicStatus
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.TypChip
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/**
 * Zyklus bearbeiten, DESIGN.md 7.5. Bottom Sheet, voll ausklappbar.
 */
data class ZyklusSheetUiState(
    val zyklus: Zyklus,
) {
    val summeMinuten: Int get() = zyklus.gesamtMinuten
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZyklusSheet(
    state: ZyklusSheetUiState,
    beiSchliessen: () -> Unit,
    beiFach: (String) -> Unit,
    beiThemaUmschalten: (String) -> Unit,
    beiTyp: (Zyklustyp) -> Unit,
    beiTypenMischen: (Boolean) -> Unit,
    beiLernphase: (Int) -> Unit,
    beiAufgabenphase: (Int) -> Unit,
    beiAufgabenAutomatisch: (Boolean) -> Unit,
    beiSelbstAuswaehlen: () -> Unit,
    beiUebernehmen: () -> Unit,
) {
    val sheetzustand = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = beiSchliessen,
        sheetState = sheetzustand,
        shape = Radius.Sheet,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        ZyklusSheetInhalt(
            state = state,
            beiFach = beiFach,
            beiThemaUmschalten = beiThemaUmschalten,
            beiTyp = beiTyp,
            beiTypenMischen = beiTypenMischen,
            beiLernphase = beiLernphase,
            beiAufgabenphase = beiAufgabenphase,
            beiAufgabenAutomatisch = beiAufgabenAutomatisch,
            beiSelbstAuswaehlen = beiSelbstAuswaehlen,
            beiUebernehmen = beiUebernehmen,
        )
    }
}

/**
 * Der Inhalt ohne Sheet-Rahmen. So kann der Screen-Katalog ihn auch als
 * ganzen Screen zeigen, ohne dass ein Sheet geoeffnet werden muss.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZyklusSheetInhalt(
    state: ZyklusSheetUiState,
    beiFach: (String) -> Unit,
    beiThemaUmschalten: (String) -> Unit,
    beiTyp: (Zyklustyp) -> Unit,
    beiTypenMischen: (Boolean) -> Unit,
    beiLernphase: (Int) -> Unit,
    beiAufgabenphase: (Int) -> Unit,
    beiAufgabenAutomatisch: (Boolean) -> Unit,
    beiSelbstAuswaehlen: () -> Unit,
    beiUebernehmen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val zyklus = state.zyklus
    val themen = FakeData.themenVon(zyklus.fachId)

    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier
                .weight(1f, fill = false)
                .heightIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Mass.Seitenrand),
        ) {
            Text(
                text = "Zyklus bearbeiten",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // --- Fach
            Luft(Mass.Gross)
            Text(
                text = "Fach",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FakeData.faecher.filter { it.aktiv }.forEach { fach ->
                    TypChip(
                        text = fach.name,
                        gewaehlt = fach.id == zyklus.fachId,
                        beiKlick = { beiFach(fach.id) },
                    )
                }
            }

            // --- Themen
            Luft(Mass.Gross)
            Text(
                text = "Themen",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            themen.forEach { thema ->
                val gewaehlt = thema.id in zyklus.themaIds
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { beiThemaUmschalten(thema.id) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TopicStatus(thema.stand)
                    LuftBreit(12.dp)
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = thema.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (gewaehlt) {
                                stifte.tinte
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                        Text(
                            text = "${thema.anzahlAufgaben} Aufgaben, " +
                                "${thema.anzahlKarten} Karten",
                            style = MaterialTheme.typography.labelSmall,
                            color = stifte.blei,
                        )
                    }
                    Box(
                        Modifier
                            .background(
                                if (gewaehlt) stifte.tinteHauch else stifte.tief,
                                Radius.Chip,
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = if (gewaehlt) "gewählt" else "wählen",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (gewaehlt) stifte.tinte else stifte.blei,
                        )
                    }
                }
                Trennlinie()
            }

            // --- Typ
            Luft(Mass.Gross)
            Text(
                text = "Typ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Zyklustyp.entries.forEach { typ ->
                    TypChip(
                        text = typ.anzeige,
                        gewaehlt = typ == zyklus.typ,
                        beiKlick = { beiTyp(typ) },
                    )
                }
            }
            Luft(Mass.Klein)
            Hinweistext(zyklus.typ.erklaerung)

            if (zyklus.typ == Zyklustyp.Aufgaben) {
                Luft(Mass.Klein)
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Aufgabentypen mischen",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = zyklus.typenMischen,
                        onCheckedChange = beiTypenMischen,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.background,
                            checkedTrackColor = stifte.tinte,
                            uncheckedThumbColor = stifte.blei,
                            uncheckedTrackColor = stifte.tief,
                            uncheckedBorderColor = stifte.karo,
                        ),
                    )
                }
            }

            // --- Dauer
            Luft(Mass.Gross)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Dauer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatiereDauer(state.summeMinuten),
                    style = MaterialTheme.typography.titleMedium,
                    color = stifte.tinte,
                )
            }
            Luft(Mass.Mittel)
            DurationSlider(
                beschriftung = "Lernphase",
                minuten = zyklus.lernphaseMinuten,
                beiAenderung = beiLernphase,
            )
            Luft(Mass.Klein)
            DurationSlider(
                beschriftung = "Aufgabenphase",
                minuten = zyklus.aufgabenphaseMinuten,
                beiAenderung = beiAufgabenphase,
                aktiv = zyklus.typ.hatBoxAufgabenphase,
            )
            if (!zyklus.typ.hatBoxAufgabenphase) {
                Hinweistext("Karteikarten laufen in der App, ohne Aufgabenphase in der Box.")
            }

            // --- Aufgaben
            if (zyklus.typ.hatBoxAufgabenphase) {
                Luft(Mass.Gross)
                Text(
                    text = "Aufgaben",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Luft(Mass.Klein)
                val automatischAnzahl = FakeData
                    .aufgabenZuThemen(zyklus.themaIds)
                    .size
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TypChip(
                        text = "Automatisch aus dem Brain ($automatischAnzahl)",
                        gewaehlt = zyklus.aufgabenAutomatisch,
                        beiKlick = { beiAufgabenAutomatisch(true) },
                    )
                }
                Luft(8.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TypChip(
                        text = if (zyklus.aufgabenIds.isEmpty()) {
                            "Selbst auswählen"
                        } else {
                            "Selbst ausgewählt (${zyklus.aufgabenIds.size})"
                        },
                        gewaehlt = !zyklus.aufgabenAutomatisch,
                        beiKlick = {
                            beiAufgabenAutomatisch(false)
                            beiSelbstAuswaehlen()
                        },
                    )
                }
            }

            Luft(Mass.Gross)
        }

        Trennlinie()
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Mass.Seitenrand),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubjectBadge(FakeData.fach(zyklus.fachId))
            LuftBreit(12.dp)
            PrimaerButton(
                text = "Übernehmen",
                beiKlick = beiUebernehmen,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
