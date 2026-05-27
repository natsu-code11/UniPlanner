package com.uniplanner.app.ui.esami

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
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

class AggiungiEsameActivity : AppCompatActivity() {

    private lateinit var tvTitoloFormEsame: TextView
    private lateinit var etNomeEsame: EditText
    private lateinit var etDataEsame: EditText
    private lateinit var etCfuEsame: EditText
    private lateinit var etVotoEsame: EditText
    private lateinit var rbDaSostenere: RadioButton
    private lateinit var rbSuperato: RadioButton
    private lateinit var btnSalvaEsame: Button
    private lateinit var btnEliminaEsame: Button

    private var esameId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_esame)

        tvTitoloFormEsame = findViewById(R.id.tvTitoloFormEsame)
        etNomeEsame = findViewById(R.id.etNomeEsame)
        etDataEsame = findViewById(R.id.etDataEsame)
        etCfuEsame = findViewById(R.id.etCfuEsame)
        etVotoEsame = findViewById(R.id.etVotoEsame)
        rbDaSostenere = findViewById(R.id.rbDaSostenere)
        rbSuperato = findViewById(R.id.rbSuperato)
        btnSalvaEsame = findViewById(R.id.btnSalvaEsame)
        btnEliminaEsame = findViewById(R.id.btnEliminaEsame)

        esameId = intent.getIntExtra("ESAME_ID", -1)

        if (esameId != -1) {
            tvTitoloFormEsame.text = "Modifica esame"

            etNomeEsame.setText(intent.getStringExtra("ESAME_NOME") ?: "")
            etDataEsame.setText(intent.getStringExtra("ESAME_DATA") ?: "")
            etCfuEsame.setText(intent.getIntExtra("ESAME_CFU", 0).toString())
            etVotoEsame.setText(intent.getIntExtra("ESAME_VOTO", 0).toString())

            val stato = intent.getStringExtra("ESAME_STATO") ?: "da sostenere"
            if (stato == "superato") {
                rbSuperato.isChecked = true
            } else {
                rbDaSostenere.isChecked = true
            }

            btnEliminaEsame.visibility = View.VISIBLE
        } else {
            tvTitoloFormEsame.text = "Aggiungi esame"
            btnEliminaEsame.visibility = View.GONE
        }

        btnSalvaEsame.setOnClickListener {
            Toast.makeText(this, "Click su Salva", Toast.LENGTH_SHORT).show()
            salvaEsame()
        }

        btnEliminaEsame.setOnClickListener {
            eliminaEsame()
        }
    }

    private fun salvaEsame() {
        val nome = etNomeEsame.text.toString().trim()
        val data = etDataEsame.text.toString().trim()
        val cfuString = etCfuEsame.text.toString().trim()
        val votoString = etVotoEsame.text.toString().trim()

        if (nome.isEmpty()) {
            etNomeEsame.error = "Inserisci il nome"
            return
        }

        if (data.isEmpty()) {
            etDataEsame.error = "Inserisci la data"
            return
        }

        val cfu = cfuString.toIntOrNull()
        if (cfu == null || cfu <= 0) {
            etCfuEsame.error = "CFU non validi"
            return
        }

        val voto = votoString.toIntOrNull() ?: 0

        val stato = if (rbSuperato.isChecked) {
            "superato"
        } else {
            "da sostenere"
        }

        val esame = Esame(
            id = if (esameId == -1) 0 else esameId,
            nome = nome,
            data = data,
            cfu = cfu,
            voto = voto,
            stato = stato
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@AggiungiEsameActivity)

            withContext(Dispatchers.IO) {
                if (esameId == -1) {
                    db.esameDao().inserisci(esame)
                } else {
                    db.esameDao().aggiorna(esame)
                }
            }

            Toast.makeText(this@AggiungiEsameActivity, "Esame salvato", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun eliminaEsame() {
        if (esameId == -1) {
            finish()
            return
        }

        val nome = etNomeEsame.text.toString().trim()
        val data = etDataEsame.text.toString().trim()
        val cfu = etCfuEsame.text.toString().trim().toIntOrNull() ?: 0
        val voto = etVotoEsame.text.toString().trim().toIntOrNull() ?: 0

        val stato = if (rbSuperato.isChecked) {
            "superato"
        } else {
            "da sostenere"
        }

        val esame = Esame(
            id = esameId,
            nome = nome,
            data = data,
            cfu = cfu,
            voto = voto,
            stato = stato
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@AggiungiEsameActivity)

            withContext(Dispatchers.IO) {
                db.esameDao().elimina(esame)
            }

            Toast.makeText(this@AggiungiEsameActivity, "Esame eliminato", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}