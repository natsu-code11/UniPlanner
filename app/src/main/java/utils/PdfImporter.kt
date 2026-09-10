// ============================================================
// FILE: PdfImporter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/utils/
// SCOPO: Legge un file PDF del calendario lezioni della Parthenope
//        e restituisce una lista di lezioni da salvare nel database.
//        Usa la libreria esterna PdfBox-Android.
// NOTA: Funzionalità aggiunta attingendo da risorse esterne per
//       migliorare l'esperienza utente — non nel programma del corso.
// ============================================================

package com.uniplanner.app.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.uniplanner.app.data.Lezione

object PdfImporter {

    // legge il PDF e restituisce la lista di lezioni trovate
    fun importaDaPdf(context: Context, uri: Uri): List<Lezione> {

        // inizializza PdfBox — obbligatorio su Android
        PDFBoxResourceLoader.init(context)

        val lezioni = mutableListOf<Lezione>()

        // apre il file PDF dall'URI selezionato
        val inputStream = context.contentResolver.openInputStream(uri) ?: return lezioni

        // legge e estrae il testo dal PDF
        val documento = PDDocument.load(inputStream)
        val estrattore = PDFTextStripper()
        val testo = estrattore.getText(documento)
        documento.close()
        inputStream.close()

        // divide il testo in righe pulite
        val righe = testo.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        // variabili temporanee per costruire ogni lezione
        var dataCorrente    = ""
        var giornoCorrente  = ""
        var materiaCorrente = ""
        var aulaCorrente    = ""

        var i = 0
        while (i < righe.size) {
            val riga = righe[i]

            when {
                // riconosce il giorno con la data — es. "Lunedì 21/09/2026"
                riga.startsWith("Lunedì") -> {
                    giornoCorrente = "Lunedì"
                    dataCorrente = estraiData(riga)
                }
                riga.startsWith("Martedì") -> {
                    giornoCorrente = "Martedì"
                    dataCorrente = estraiData(riga)
                }
                riga.startsWith("Mercoledì") -> {
                    giornoCorrente = "Mercoledì"
                    dataCorrente = estraiData(riga)
                }
                riga.startsWith("Giovedì") -> {
                    giornoCorrente = "Giovedì"
                    dataCorrente = estraiData(riga)
                }
                riga.startsWith("Venerdì") -> {
                    giornoCorrente = "Venerdì"
                    dataCorrente = estraiData(riga)
                }

                // riconosce l'aula fisica
                riga.startsWith("Aula") -> {
                    aulaCorrente = riga
                }

                // riconosce Teams come modalità online
                riga.startsWith("Codice Teams") -> {
                    aulaCorrente = "Teams"
                }

                // riconosce l'orario — es. "Dalle ore 9:30 alle ore 12:30"
                riga.startsWith("Dalle ore") -> {
                    val ora = estraiOrario(riga)
                    // quando troviamo l'orario abbiamo tutti i dati — salviamo la lezione
                    if (materiaCorrente.isNotEmpty() && giornoCorrente.isNotEmpty()) {
                        lezioni.add(
                            Lezione(
                                materia = materiaCorrente,
                                giorno  = giornoCorrente,
                                ora     = ora,
                                aula    = aulaCorrente,
                                data    = dataCorrente
                            )
                        )
                        // resetta materia e aula per la prossima
                        materiaCorrente = ""
                        aulaCorrente    = ""
                    }
                }

                // tutto il resto che non è intestazione è il nome della materia
                !riga.startsWith("Settimana") &&
                        !riga.startsWith("Dal ") &&
                        !riga.startsWith("SCUOLA") &&
                        !riga.startsWith("Corso") &&
                        !riga.startsWith("Lezioni") &&
                        !riga.startsWith("II Anno") &&
                        !riga.startsWith("prof.", ignoreCase = true) &&
                        !riga.startsWith("Aula") &&
                        !riga.startsWith("Codice") &&
                        !riga.startsWith("Dalle") &&
                        riga.length > 3 -> {
                    materiaCorrente = riga
                }
            }
            i++
        }

        return lezioni
    }

    // estrae la data da una riga — es. "Lunedì 21/09/2026" → "21/09/2026"
    private fun estraiData(riga: String): String {
        val regex = Regex("\\d{1,2}/\\d{2}/\\d{4}")
        return regex.find(riga)?.value ?: ""
    }

    // estrae l'orario — es. "Dalle ore 9:30 alle ore 12:30" → "9:30 - 12:30"
    private fun estraiOrario(riga: String): String {
        val regex = Regex("(\\d{1,2}:\\d{2})")
        val orari = regex.findAll(riga).map { it.value }.toList()
        return if (orari.size >= 2) "${orari[0]} - ${orari[1]}" else riga
    }
}