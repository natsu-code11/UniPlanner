package com.uniplanner.app.ui.esami

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
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

        leggiDatiIntent()

        btnSalvaEsame.setOnClickListener {
            salvaEsame()
        }

        btnEliminaEsame.setOnClickListener {
            eliminaEsame()
        }
    }

    private fun leggiDatiIntent() {
        esameId = intent.getIntExtra("ESAME_ID", -1)

        if (esameId != -1) {
            tvTitoloFormEsame.text = "Modifica esame"

            val nome = intent.getStringExtra("ESAME_NOME") ?: ""
            val data = intent.getStringExtra("ESAME_DATA") ?: ""
            val cfu = intent.getIntExtra("ESAME_CFU", 0)
            val voto = intent.getIntExtra("ESAME_VOTO", 0)
            val stato = intent.getStringExtra("ESAME_STATO") ?: "da sostenere"

            etNomeEsame.setText(nome)
            etDataEsame.setText(data)
            etCfuEsame.setText(cfu.toString())
            etVotoEsame.setText(voto.toString())

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
    }

    private fun salvaEsame() {
        val nome = etNomeEsame.text.toString().trim()
        val data = etDataEsame.text.toString().trim()
        val cfuTesto = etCfuEsame.text.toString().trim()
        val votoTesto = etVotoEsame.text.toString().trim()

        if (nome.isEmpty()) {
            etNomeEsame.error = "Il nome è obbligatorio"
            return
        }

        if (data.isEmpty()) {
            etDataEsame.error = "La data è obbligatoria"
            return
        }

        val cfu = cfuTesto.toIntOrNull()
        if (cfu == null || cfu <= 0) {
            etCfuEsame.error = "Inserisci CFU validi"
            return
        }

        val voto = votoTesto.toIntOrNull() ?: 0

        if (voto < 0 || voto > 30) {
            etVotoEsame.error = "Il voto deve essere tra 0 e 30"
            return
        }

        val stato = if (rbSuperato.isChecked) {
            "superato"
        } else {
            "da sostenere"
        }

        val esame = Esame(
            id = if (esameId != -1) esameId else 0,
            nome = nome,
            data = data,
            cfu = cfu,
            voto = voto,
            stato = stato
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@AggiungiEsameActivity)

            withContext(Dispatchers.IO) {
                if (esameId != -1) {
                    db.esameDao().aggiorna(esame)
                } else {
                    db.esameDao().inserisci(esame)
                }
            }

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

            finish()
        }
    }
}