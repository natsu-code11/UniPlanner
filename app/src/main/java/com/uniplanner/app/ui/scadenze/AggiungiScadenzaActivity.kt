// ============================================================
// FILE: AggiungiScadenzaActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Form per aggiungere una nuova scadenza.
//        Campi: titolo, tipo, data, priorità.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L11 (Intent), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.scadenze

import android.os.Bundle
import android.widget.ArrayAdapter    // crea la lista di opzioni per lo Spinner
import android.widget.Button          // widget bottone
import android.widget.EditText        // widget campo testo modificabile
import android.widget.Spinner         // widget menu a tendina
import android.widget.Toast           // messaggio popup temporaneo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import com.uniplanner.app.data.Scadenza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AggiungiScadenzaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_scadenza)  // collega il layout

        // collegamento ai widget del layout
        val etTitolo        = findViewById<EditText>(R.id.etTitoloScadenza)
        val spinnerTipo     = findViewById<Spinner>(R.id.spinnerTipoScadenza)
        val etData          = findViewById<EditText>(R.id.etDataScadenza)
        val spinnerPriorita = findViewById<Spinner>(R.id.spinnerPrioritaScadenza)
        val btnSalva        = findViewById<Button>(R.id.btnSalvaScadenza)
        val btnAnnulla      = findViewById<Button>(R.id.btnAnnullaScadenza)

        // popola lo spinner con i tipi di scadenza
        val tipi = listOf("progetto", "pagamento", "studio", "consegna")
        spinnerTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipi
        )

        // popola lo spinner con le priorità
        val priorita = listOf("alta", "media", "bassa")
        spinnerPriorita.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorita
        )

        // bottone annulla — torna alla schermata precedente
        btnAnnulla.setOnClickListener {
            finish()  // chiude questa Activity
        }

        // bottone salva — salva la scadenza nel database
        btnSalva.setOnClickListener {
            val titolo   = etTitolo.text.toString().trim()
            val tipo     = spinnerTipo.selectedItem.toString()
            val data     = etData.text.toString().trim()
            val priorita = spinnerPriorita.selectedItem.toString()

            // controlla che i campi obbligatori non siano vuoti
            if (titolo.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Compila titolo e data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // crea l'oggetto Scadenza con i dati inseriti
            val nuovaScadenza = Scadenza(
                titolo     = titolo,
                tipo       = tipo,
                data       = data,
                priorita   = priorita,
                completata = false   // di default non completata
            )

            // salva nel database in background
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // operazione lenta → eseguita in background
                    AppDatabase.getInstance(applicationContext)
                        .scadenzaDao()
                        .inserisci(nuovaScadenza)
                }
                Toast.makeText(
                    this@AggiungiScadenzaActivity,
                    "Scadenza salvata!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()  // chiude questa Activity
            }
        }
    }
}