package de.kopfgeld.app.feature.importieren

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kopfgeld.app.data.Aufgabenquelle
import de.kopfgeld.app.data.FakeData
import de.kopfgeld.app.data.Scan
import de.kopfgeld.app.data.Scanstand
import de.kopfgeld.app.data.Serverstand
import de.kopfgeld.app.ui.components.HeftScaffold
import de.kopfgeld.app.ui.components.Hinweistext
import de.kopfgeld.app.ui.components.KameraPlatzhalter
import de.kopfgeld.app.ui.components.Luft
import de.kopfgeld.app.ui.components.LuftBreit
import de.kopfgeld.app.ui.components.PrimaerButton
import de.kopfgeld.app.ui.components.RasterZeile
import de.kopfgeld.app.ui.components.ScanThumb
import de.kopfgeld.app.ui.components.ServerStatusChip
import de.kopfgeld.app.ui.components.TextAktion
import de.kopfgeld.app.ui.components.Trennlinie
import de.kopfgeld.app.ui.components.TypChip
import de.kopfgeld.app.ui.components.ZweitButton
import de.kopfgeld.app.ui.components.karoRaster
import de.kopfgeld.app.ui.components.seitenPlatzhalter
import de.kopfgeld.app.ui.theme.Kreide
import de.kopfgeld.app.ui.theme.KopfgeldTheme
import de.kopfgeld.app.ui.theme.Mass
import de.kopfgeld.app.ui.theme.Radius

/*
 * Import, DESIGN.md 2.3:
 *
 *   Scannen -> Warteschlange -> Transkription -> Pruefen -> Brain
 *
 * Ein eigener Wireframe fehlt im uebermittelten Auftragstext, der Ablauf ist
 * aber vollstaendig beschrieben.
 */

// --- Schritt 1: Scannen ----------------------------------------------------

data class ImportScannenUiState(
    val quelle: Aufgabenquelle = Aufgabenquelle.Buch,
    val bezeichnung: String = "Mathebuch",
    val scans: List<Scan> = FakeData.scansImport,
)

@Composable
fun ImportScannenScreen(
    state: ImportScannenUiState,
    beiQuelle: (Aufgabenquelle) -> Unit,
    beiBezeichnung: (String) -> Unit,
    beiAusloesen: () -> Unit,
    beiFertig: () -> Unit,
    beiAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .karoRaster(stifte.karo)
                .padding(Mass.Seitenrand),
        ) {
            Text(
                text = "Seiten importieren",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            Text(
                text = "Scanne Buchseiten, Arbeitsblätter oder alte Arbeiten. " +
                    "Die Zuordnung machst du danach.",
                style = MaterialTheme.typography.bodyMedium,
                color = stifte.blei,
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            KameraPlatzhalter()
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.7f)
                    .height(290.dp)
                    .border(2.dp, Kreide.copy(alpha = 0.8f), Radius.Chip),
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = Mass.Mittel),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Mass.Seitenrand),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.scans.forEach { scan -> ScanThumb(scan) }
            }

            Luft(Mass.Mittel)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Mass.Seitenrand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextAktion(text = "Abbrechen", beiKlick = beiAbbrechen)
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(68.dp)
                            .border(3.dp, stifte.tinte, CircleShape)
                            .clickable(onClick = beiAusloesen),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .background(MaterialTheme.colorScheme.background, CircleShape)
                                .border(1.dp, stifte.karo, CircleShape),
                        )
                    }
                }
                TextAktion(text = "Fertig (${state.scans.size})", beiKlick = beiFertig)
            }
        }
    }
}

// --- Schritt 2: Warteschlange ----------------------------------------------

data class ImportWarteschlangeUiState(
    val scans: List<Scan> = FakeData.scansImport,
    val server: Serverstand = Serverstand.Analysiert(2, 5, "Seiten"),
    val erkannteAufgaben: Int = 7,
) {
    val fertig: Int get() = scans.count { it.stand == Scanstand.Fertig }
    val bereitZumPruefen: Boolean get() = fertig > 0
}

@Composable
fun ImportWarteschlangeScreen(
    state: ImportWarteschlangeUiState,
    beiZurueck: () -> Unit,
    beiPruefen: () -> Unit,
    beiSpaeter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte

    HeftScaffold(
        titel = "Warteschlange",
        unterzeile = "${state.fertig} von ${state.scans.size} Seiten fertig",
        zurueck = beiZurueck,
        karo = true,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Column {
                PrimaerButton(
                    text = "${state.erkannteAufgaben} Aufgaben prüfen",
                    beiKlick = beiPruefen,
                    aktiv = state.bereitZumPruefen,
                )
                TextAktion(text = "Später prüfen", beiKlick = beiSpaeter)
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(Modifier.padding(horizontal = Mass.Seitenrand)) {
                ServerStatusChip(state.server)
            }
            Luft(Mass.Klein)
            Hinweistext(
                "Der Server liest Handschrift in Minuten, nicht in Sekunden. " +
                    "Du kannst die App zumachen, die Seiten bleiben in der Schlange.",
                Modifier.padding(horizontal = Mass.Seitenrand),
            )
            Luft(Mass.Mittel)

            state.scans.forEach { scan ->
                RasterZeile {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScanThumb(scan = scan, breite = 44.dp)
                        LuftBreit(14.dp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Seite ${scan.seitennummer}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = scan.herkunft,
                                style = MaterialTheme.typography.bodyMedium,
                                color = stifte.blei,
                            )
                        }
                        Text(
                            text = scan.stand.anzeige,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (scan.stand) {
                                Scanstand.Fertig -> stifte.richtig
                                Scanstand.Analysiert -> stifte.tinte
                                Scanstand.Wartet -> stifte.blei
                            },
                        )
                    }
                }
            }
            Luft(Mass.Gross)
        }
    }
}

// --- Schritt 3: Pruefen ----------------------------------------------------

/**
 * Eine erkannte Aufgabe, die noch geprueft werden muss.
 * Bewusst ein eigenes Modell: bis zum Uebernehmen ist das noch keine Aufgabe
 * im Brain, sondern ein Vorschlag der Transkription.
 */
data class ErkannteAufgabe(
    val id: String,
    val nummer: String,
    val text: String,
    val seite: Int,
    val fachId: String?,
    val themaIds: List<String>,
    val typ: String,
    val istLoesung: Boolean,
)

data class ImportPruefenUiState(
    val posten: List<ErkannteAufgabe> = beispielErkannt,
    val index: Int = 0,
) {
    val aktuelle: ErkannteAufgabe? get() = posten.getOrNull(index)
    val istLetzte: Boolean get() = index >= posten.lastIndex
}

private val beispielErkannt = listOf(
    ErkannteAufgabe(
        id = "e1", nummer = "3a",
        text = "Kürze die Brüche so weit wie möglich und gib jeweils an, mit welcher " +
            "Zahl du gekürzt hast.",
        seite = 84, fachId = "m", themaIds = listOf("m1"),
        typ = "Rechnen", istLoesung = false,
    ),
    ErkannteAufgabe(
        id = "e2", nummer = "3b",
        text = "Ordne die Brüche der Größe nach und begründe deine Reihenfolge.",
        seite = 84, fachId = "m", themaIds = emptyList(),
        typ = "Begründen", istLoesung = false,
    ),
    ErkannteAufgabe(
        id = "e3", nummer = "L 3a",
        text = "3/4; 2/3; 7/8 — jeweils durch den größten gemeinsamen Teiler gekürzt.",
        seite = 212, fachId = "m", themaIds = listOf("m1"),
        typ = "Lösung", istLoesung = true,
    ),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImportPruefenScreen(
    state: ImportPruefenUiState,
    beiZurueck: () -> Unit,
    beiText: (String) -> Unit,
    beiFach: (String) -> Unit,
    beiThemaUmschalten: (String) -> Unit,
    beiTyp: (String) -> Unit,
    beiLoesungUmschalten: () -> Unit,
    beiVerwerfen: () -> Unit,
    beiUebernehmen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stifte = KopfgeldTheme.stifte
    val posten = state.aktuelle

    HeftScaffold(
        titel = "Prüfen",
        unterzeile = if (posten == null) {
            null
        } else {
            "Aufgabe ${state.index + 1} von ${state.posten.size} · Seite ${posten.seite}"
        },
        zurueck = beiZurueck,
        karo = true,
        scrollbar = false,
        modifier = modifier,
        fussleiste = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ZweitButton(
                    text = "Verwerfen",
                    beiKlick = beiVerwerfen,
                    farbe = stifte.korrektur,
                    modifier = Modifier.weight(1f),
                )
                LuftBreit(12.dp)
                PrimaerButton(
                    text = if (state.istLetzte) "Fertig" else "Übernehmen",
                    beiKlick = beiUebernehmen,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    ) {
        if (posten == null) {
            Hinweistext(
                "Nichts mehr zu prüfen.",
                Modifier.padding(Mass.Seitenrand),
            )
            return@HeftScaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Mass.Seitenrand),
        ) {
            // Ausschnitt der gescannten Seite.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.background, Radius.Flaeche)
                    .border(1.dp, stifte.karo, Radius.Flaeche)
                    .seitenPlatzhalter(stifte.karo, stifte.blei),
            )

            Luft(Mass.Mittel)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nummer ${posten.nummer}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                TypChip(
                    text = if (posten.istLoesung) "Ist eine Lösung" else "Ist eine Aufgabe",
                    gewaehlt = posten.istLoesung,
                    beiKlick = beiLoesungUmschalten,
                )
            }

            Luft(Mass.Mittel)
            OutlinedTextField(
                value = posten.text,
                onValueChange = beiText,
                label = { Text("Erkannter Text") },
                shape = Radius.Eingabe,
                modifier = Modifier.fillMaxWidth(),
            )
            Luft(6.dp)
            Hinweistext("Erkennungsfehler kannst du hier direkt ausbessern.")

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
                        gewaehlt = fach.id == posten.fachId,
                        beiKlick = { beiFach(fach.id) },
                    )
                }
            }

            Luft(Mass.Gross)
            Text(
                text = "Thema",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Luft(Mass.Klein)
            if (posten.fachId == null) {
                Hinweistext("Wähle zuerst ein Fach.")
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FakeData.themenVon(posten.fachId).forEach { thema ->
                        TypChip(
                            text = thema.name,
                            gewaehlt = thema.id in posten.themaIds,
                            beiKlick = { beiThemaUmschalten(thema.id) },
                        )
                    }
                }
                if (posten.themaIds.isEmpty()) {
                    Luft(6.dp)
                    Text(
                        text = "Ohne Thema findet die Aufgabe später keinen Zyklus.",
                        style = MaterialTheme.typography.labelSmall,
                        color = stifte.korrektur,
                    )
                }
            }

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
                listOf(
                    "Rechnen", "Textaufgabe", "Begründen", "Umformen",
                    "Schreiben", "Nennen", "Zeichnen", "Lösung",
                ).forEach { typ ->
                    TypChip(
                        text = typ,
                        gewaehlt = typ == posten.typ,
                        beiKlick = { beiTyp(typ) },
                    )
                }
            }

            if (posten.istLoesung) {
                Luft(Mass.Mittel)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(stifte.tief, Radius.Flaeche)
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Lösungen bleiben im Brain, werden in Zyklen aber nie gezeigt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stifte.blei,
                    )
                }
            }

            Luft(Mass.Gross)
            Trennlinie()
            Luft(Mass.Gross)
        }
    }
}
