// ============================================================
// FILE: AggiungiLezioneActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Form per aggiungere O modificare una lezione.
//        Se riceve un ID via Intent carica la lezione esistente.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L11 (Intent), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.lezioni

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import com.uniplanner.app.data.Lezione
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AggiungiLezioneActivity : AppCompatActivity() {

    private var dataSelezionata = ""
    private var lezioneId = -1  // -1 significa nuova lezione

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_lezione)

        val tvTitolo          = findViewById<TextView>(R.id.tvTitoloFormLezione)
        val etMateria         = findViewById<EditText>(R.id.etMateria)
        val spinnerGiorno     = findViewById<Spinner>(R.id.spinnerGiorno)
        val btnSelezionaData  = findViewById<Button>(R.id.btnSelezionaData)
        val tvDataSelezionata = findViewById<TextView>(R.id.tvDataSelezionata)
        val etOra             = findViewById<EditText>(R.id.etOra)
        val etAula            = findViewById<EditText>(R.id.etAula)
        val btnSalva          = findViewById<Button>(R.id.btnSalvaLezione)
        val btnAnnulla        = findViewById<Button>(R.id.btnAnnullaLezione)

        val giorni = listOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì")
        spinnerGiorno.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            giorni
        )

        // controlla se stiamo modificando una lezione esistente
        lezioneId = intent.getIntExtra("LEZIONE_ID", -1)
        if (lezioneId != -1) {
            tvTitolo.text = "Modifica lezione"
            lifecycleScope.launch {
                val lezione = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).lezioneDao().getById(lezioneId)
                }
                lezione?.let {
                    etMateria.setText(it.materia)
                    etOra.setText(it.ora)
                    etAula.setText(it.aula)
                    dataSelezionata = it.data
                    tvDataSelezionata.text = if (it.data.isNotEmpty()) it.data else "Nessuna data selezionata"
                    val index = giorni.indexOf(it.giorno)
                    if (index >= 0) spinnerGiorno.setSelection(index)
                }
            }
        }

        // DatePicker
        btnSelezionaData.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, anno, mese, giorno ->
                    dataSelezionata = "$giorno/${mese + 1}/$anno"
                    tvDataSelezionata.text = dataSelezionata
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnAnnulla.setOnClickListener { finish() }

        btnSalva.setOnClickListener {
            val materia = etMateria.text.toString().trim()
            val giorno  = spinnerGiorno.selectedItem.toString()
            val ora     = etOra.text.toString().trim()
            val aula    = etAula.text.toString().trim()

            if (materia.isEmpty() || ora.isEmpty() || aula.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    if (lezioneId == -1) {
                        db.lezioneDao().inserisci(
                            Lezione(
                                materia = materia,
                                giorno  = giorno,
                                ora     = ora,
                                aula    = aula,
                                data    = dataSelezionata
                            )
                        )
                    } else {
                        db.lezioneDao().aggiorna(
                            Lezione(
                                id      = lezioneId,
                                materia = materia,
                                giorno  = giorno,
                                ora     = ora,
                                aula    = aula,
                                data    = dataSelezionata
                            )
                        )
                    }
                }
                Toast.makeText(
                    this@AggiungiLezioneActivity,
                    if (lezioneId == -1) "Lezione salvata!" else "Lezione modificata!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}