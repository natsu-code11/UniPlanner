// ============================================================
// FILE: AggiungiLezioneActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Schermata con il form per aggiungere una nuova lezione.
//        L'utente inserisce materia, giorno, ora e aula.
//        I dati vengono salvati nel database Room.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L11 (Intent), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.lezioni

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
import com.uniplanner.app.data.Lezione
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AggiungiLezioneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_lezione)  // collega il layout

        // collegamento ai widget del layout
        val etMateria     = findViewById<EditText>(R.id.etMateria)
        val spinnerGiorno = findViewById<Spinner>(R.id.spinnerGiorno)
        val etOra         = findViewById<EditText>(R.id.etOra)
        val etAula        = findViewById<EditText>(R.id.etAula)
        val btnSalva      = findViewById<Button>(R.id.btnSalvaLezione)
        val btnAnnulla    = findViewById<Button>(R.id.btnAnnullaLezione)

        // popola lo spinner con i giorni della settimana
        val giorni = listOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì")
        spinnerGiorno.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            giorni
        )

        // bottone annulla — torna alla schermata precedente
        btnAnnulla.setOnClickListener {
            finish()  // chiude questa Activity
        }

        // bottone salva — salva la lezione nel database
        btnSalva.setOnClickListener {
            val materia = etMateria.text.toString().trim()  // legge il testo inserito
            val giorno  = spinnerGiorno.selectedItem.toString()
            val ora     = etOra.text.toString().trim()
            val aula    = etAula.text.toString().trim()

            // controlla che i campi non siano vuoti
            if (materia.isEmpty() || ora.isEmpty() || aula.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // esce senza salvare
            }

            // crea l'oggetto Lezione con i dati inseriti
            val nuovaLezione = Lezione(
                materia = materia,
                giorno  = giorno,
                ora     = ora,
                aula    = aula
            )

            // salva nel database in background
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // operazione lenta → eseguita in background
                    AppDatabase.getInstance(applicationContext)
                        .lezioneDao()
                        .inserisci(nuovaLezione)
                }
                // torna alla schermata precedente dopo il salvataggio
                Toast.makeText(
                    this@AggiungiLezioneActivity,
                    "Lezione salvata!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()  // chiude questa Activity
            }
        }
    }
}