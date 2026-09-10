// ============================================================
// FILE: AggiungiEsameActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: Form per aggiungere O modificare un esame.
//        Se riceve un ID via Intent, carica l'esame esistente
//        e permette di modificarlo. Altrimenti aggiunge uno nuovo.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L11 (Intent), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.esami

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
import com.uniplanner.app.data.Esame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AggiungiEsameActivity : AppCompatActivity() {

    // data selezionata dall'utente tramite DatePicker
    private var dataSelezionata = ""

    // id dell'esame da modificare — -1 significa nuovo esame
    private var esameId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_esame)

        val tvTitolo             = findViewById<TextView>(R.id.tvTitoloForm)
        val etNome               = findViewById<EditText>(R.id.etNomeEsame)
        val btnSelezionaData     = findViewById<Button>(R.id.btnSelezionaDataEsame)
        val tvDataSelezionata    = findViewById<TextView>(R.id.tvDataSelezionataEsame)
        val etCfu                = findViewById<EditText>(R.id.etCfuEsame)
        val etVoto               = findViewById<EditText>(R.id.etVotoEsame)
        val spinnerStato         = findViewById<Spinner>(R.id.spinnerStatoEsame)
        val btnSalva             = findViewById<Button>(R.id.btnSalvaEsame)
        val btnAnnulla           = findViewById<Button>(R.id.btnAnnullaEsame)

        // popola lo spinner con gli stati possibili
        val stati = listOf("da sostenere", "superato")
        spinnerStato.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stati
        )

        // controlla se stiamo modificando un esame esistente
        esameId = intent.getIntExtra("ESAME_ID", -1)
        if (esameId != -1) {
            // modalità modifica — carica i dati dell'esame esistente
            tvTitolo.text = "Modifica esame"
            lifecycleScope.launch {
                val esame = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).esameDao().getById(esameId)
                }
                esame?.let {
                    etNome.setText(it.nome)
                    etCfu.setText(it.cfu.toString())
                    etVoto.setText(if (it.voto > 0) it.voto.toString() else "")
                    dataSelezionata = it.data
                    tvDataSelezionata.text = it.data
                    // seleziona lo stato nello spinner
                    val index = stati.indexOf(it.stato)
                    if (index >= 0) spinnerStato.setSelection(index)
                }
            }
        }

        // DatePicker per la data
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

        btnAnnulla.setOnClickListener {
            finish()
        }

        btnSalva.setOnClickListener {
            val nome  = etNome.text.toString().trim()
            val cfu   = etCfu.text.toString().trim()
            val voto  = etVoto.text.toString().trim()
            val stato = spinnerStato.selectedItem.toString()

            if (nome.isEmpty() || cfu.isEmpty()) {
                Toast.makeText(this, "Compila nome e CFU!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    if (esameId == -1) {
                        // inserisce nuovo esame
                        db.esameDao().inserisci(
                            Esame(
                                nome  = nome,
                                data  = dataSelezionata,
                                cfu   = cfu.toIntOrNull() ?: 0,
                                voto  = voto.toIntOrNull() ?: 0,
                                stato = stato
                            )
                        )
                    } else {
                        // aggiorna esame esistente
                        db.esameDao().aggiorna(
                            Esame(
                                id    = esameId,
                                nome  = nome,
                                data  = dataSelezionata,
                                cfu   = cfu.toIntOrNull() ?: 0,
                                voto  = voto.toIntOrNull() ?: 0,
                                stato = stato
                            )
                        )
                    }
                }
                Toast.makeText(
                    this@AggiungiEsameActivity,
                    if (esameId == -1) "Esame salvato!" else "Esame modificato!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}