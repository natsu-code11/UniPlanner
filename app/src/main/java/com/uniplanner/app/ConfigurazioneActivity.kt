// ============================================================
// FILE: ConfigurazioneActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/
// SCOPO: Schermata mostrata solo al primo avvio dell'app.
//        L'utente inserisce nome, corso, anno e semestre.
//        I dati vengono salvati in SharedPreferences.
//        Agli avvii successivi questa schermata viene saltata.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L10 (UI), L16 (SharedPreferences)
// ============================================================

package com.uniplanner.app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigurazioneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurazione)

        // collegamento ai widget del layout
        val etNome     = findViewById<EditText>(R.id.etNome)
        val spinnerCorso     = findViewById<Spinner>(R.id.spinnerCorso)
        val spinnerAnno      = findViewById<Spinner>(R.id.spinnerAnno)
        val spinnerSemestre  = findViewById<Spinner>(R.id.spinnerSemestre)
        val btnInizia        = findViewById<Button>(R.id.btnInizia)

        // popolamento Spinner corso di laurea
        val corsi = listOf(
            "Informatica",
            "Economia",
            "Ingegneria",
            "Scienze Nautiche",
            "Giurisprudenza",
            "Scienze Motorie",
            "Altro"
        )
        spinnerCorso.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, corsi)

        // popolamento Spinner anno di corso
        val anni = listOf("1° Anno", "2° Anno", "3° Anno", "4° Anno", "5° Anno")
        spinnerAnno.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, anni)

        // popolamento Spinner semestre
        val semestri = listOf("Primo Semestre", "Secondo Semestre")
        spinnerSemestre.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, semestri)

        // click sul bottone Inizia
        btnInizia.setOnClickListener {
            val nome = etNome.text.toString().trim()

            // controllo che il nome non sia vuoto
            if (nome.isEmpty()) {
                Toast.makeText(this, "Inserisci il tuo nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // salvataggio in SharedPreferences
            val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)
            prefs.edit()
                .putBoolean("primo_avvio", false)   // non mostrare più questa schermata
                .putString("nome", nome)             // nome dello studente
                .putString("corso", spinnerCorso.selectedItem.toString())
                .putString("anno", spinnerAnno.selectedItem.toString())
                .putString("semestre", spinnerSemestre.selectedItem.toString())
                .apply()

            // vai alla MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // chiude questa Activity così non si torna indietro
        }
    }
}