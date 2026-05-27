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

        val etNome = findViewById<EditText>(R.id.etNome)
        val spinnerCorso = findViewById<Spinner>(R.id.spinnerCorso)
        val spinnerAnno = findViewById<Spinner>(R.id.spinnerAnno)
        val spinnerSemestre = findViewById<Spinner>(R.id.spinnerSemestre)
        val btnInizia = findViewById<Button>(R.id.btnInizia)

        etNome.setTextColor(getColor(android.R.color.black))
        etNome.setHintTextColor(getColor(android.R.color.darker_gray))
        etNome.setBackgroundColor(getColor(android.R.color.white))

        // Primo elemento vuoto: così la tendina parte senza valore selezionato
        val corsi = listOf(
            "",
            "Informatica",
            "Economia",
            "Ingegneria",
            "Scienze Nautiche",
            "Giurisprudenza",
            "Scienze Motorie",
            "Altro"
        )

        val adapterCorso = ArrayAdapter(
            this,
            R.layout.item_spinner,
            corsi
        )
        adapterCorso.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinnerCorso.adapter = adapterCorso

        val anni = listOf(
            "",
            "1° Anno",
            "2° Anno",
            "3° Anno",
            "4° Anno",
            "5° Anno"
        )

        val adapterAnno = ArrayAdapter(
            this,
            R.layout.item_spinner,
            anni
        )
        adapterAnno.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinnerAnno.adapter = adapterAnno

        val semestri = listOf(
            "",
            "Primo Semestre",
            "Secondo Semestre"
        )

        val adapterSemestre = ArrayAdapter(
            this,
            R.layout.item_spinner,
            semestri
        )
        adapterSemestre.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinnerSemestre.adapter = adapterSemestre

        btnInizia.setOnClickListener {
            val nome = etNome.text.toString().trim()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Inserisci il tuo nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerCorso.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleziona il corso di laurea", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerAnno.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleziona l'anno di corso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerSemestre.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleziona il semestre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)
            prefs.edit()
                .putBoolean("primo_avvio", false)
                .putString("nome", nome)
                .putString("corso", spinnerCorso.selectedItem.toString())
                .putString("anno", spinnerAnno.selectedItem.toString())
                .putString("semestre", spinnerSemestre.selectedItem.toString())
                .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}