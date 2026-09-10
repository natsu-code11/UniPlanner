// ============================================================
// FILE: AggiungiScadenzaActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Form per aggiungere O modificare una scadenza.
//        Se riceve un ID via Intent carica la scadenza esistente.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L11 (Intent), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.scadenze

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
import com.uniplanner.app.data.Scadenza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AggiungiScadenzaActivity : AppCompatActivity() {

    private var dataSelezionata = ""
    private var scadenzaId = -1  // -1 significa nuova scadenza

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_scadenza)

        val tvTitolo          = findViewById<TextView>(R.id.tvTitoloFormScadenza)
        val etTitolo          = findViewById<EditText>(R.id.etTitoloScadenza)
        val spinnerTipo       = findViewById<Spinner>(R.id.spinnerTipoScadenza)
        val btnSelezionaData  = findViewById<Button>(R.id.btnSelezionaDataScadenza)
        val tvDataSelezionata = findViewById<TextView>(R.id.tvDataSelezionataScadenza)
        val spinnerPriorita   = findViewById<Spinner>(R.id.spinnerPrioritaScadenza)
        val btnSalva          = findViewById<Button>(R.id.btnSalvaScadenza)
        val btnAnnulla        = findViewById<Button>(R.id.btnAnnullaScadenza)

        val tipi = listOf("progetto", "pagamento", "studio", "consegna")
        spinnerTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipi
        )

        val priorita = listOf("alta", "media", "bassa")
        spinnerPriorita.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorita
        )

        // controlla se stiamo modificando una scadenza esistente
        scadenzaId = intent.getIntExtra("SCADENZA_ID", -1)
        if (scadenzaId != -1) {
            tvTitolo.text = "Modifica scadenza"
            lifecycleScope.launch {
                val scadenza = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).scadenzaDao().getById(scadenzaId)
                }
                scadenza?.let {
                    etTitolo.setText(it.titolo)
                    dataSelezionata = it.data
                    tvDataSelezionata.text = it.data
                    val indexTipo = tipi.indexOf(it.tipo)
                    if (indexTipo >= 0) spinnerTipo.setSelection(indexTipo)
                    val indexPriorita = priorita.indexOf(it.priorita)
                    if (indexPriorita >= 0) spinnerPriorita.setSelection(indexPriorita)
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
            val titolo   = etTitolo.text.toString().trim()
            val tipo     = spinnerTipo.selectedItem.toString()
            val priorita = spinnerPriorita.selectedItem.toString()

            if (titolo.isEmpty()) {
                Toast.makeText(this, "Inserisci il titolo!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dataSelezionata.isEmpty()) {
                Toast.makeText(this, "Seleziona una data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    if (scadenzaId == -1) {
                        db.scadenzaDao().inserisci(
                            Scadenza(
                                titolo     = titolo,
                                tipo       = tipo,
                                data       = dataSelezionata,
                                priorita   = priorita,
                                completata = false
                            )
                        )
                    } else {
                        db.scadenzaDao().aggiorna(
                            Scadenza(
                                id         = scadenzaId,
                                titolo     = titolo,
                                tipo       = tipo,
                                data       = dataSelezionata,
                                priorita   = priorita,
                                completata = false
                            )
                        )
                    }
                }
                Toast.makeText(
                    this@AggiungiScadenzaActivity,
                    if (scadenzaId == -1) "Scadenza salvata!" else "Scadenza modificata!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}