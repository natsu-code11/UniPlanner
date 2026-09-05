// ============================================================
// FILE: MainActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/
// SCOPO: Schermata principale dell'app. Controlla se è il primo
//        avvio e reindirizza alla configurazione se necessario.
//        Gestisce la navigazione tra i Fragment.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L12 (Fragments), L16 (SharedPreferences)
// ============================================================

package com.uniplanner.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.home.HomeFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // collega il layout XML

        // legge le SharedPreferences per capire lo stato dell'app
        val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)

        // controlla se è il primo avvio
        val primoAvvio = prefs.getBoolean("primo_avvio", true)

        if (primoAvvio) {
            // prima volta → vai alla schermata di configurazione
            startActivity(Intent(this, ConfigurazioneActivity::class.java))
            finish()  // chiude MainActivity così non si torna indietro
            return    // esce da onCreate senza eseguire il resto
        }

        // mostra il nome dello studente nella toolbar
        val nome = prefs.getString("nome", "") ?: ""
        if (nome.isNotEmpty()) {
            // aggiorna il testo del benvenuto con il nome salvato
            findViewById<TextView>(R.id.tvBenvenuto).text = "Ciao, $nome! 👋"
        }

        // mostra HomeFragment all'avvio solo se non c'è già un Fragment attivo
        if (savedInstanceState == null) {
            mostraFragment(HomeFragment())
        }
    }

    // funzione pubblica che sostituisce il Fragment visibile nel contenitore
    // è pubblica perché viene chiamata anche da HomeFragment
    fun mostraFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()  // inizia una transazione
            .replace(R.id.fragmentContainer, fragment)  // sostituisce il Fragment
            .addToBackStack(null)  // aggiunge al backstack → permette di tornare indietro con la freccia
            .commit()  // applica la transazione
    }
}