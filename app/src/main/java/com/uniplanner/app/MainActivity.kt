// ============================================================
// FILE: MainActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/
// SCOPO: Schermata principale dell'app. Controlla se è il primo
//        avvio e reindirizza alla configurazione se necessario.
//        Gestisce la bottom navigation bar.
// LEZIONE DI RIFERIMENTO: L09 (Activity), L12 (Fragments), L16 (SharedPreferences)
// ============================================================

package com.uniplanner.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.home.HomeFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // controlla se è il primo avvio
        val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)
        val primoAvvio = prefs.getBoolean("primo_avvio", true)

        if (primoAvvio) {
            // prima volta → vai alla configurazione
            startActivity(Intent(this, ConfigurazioneActivity::class.java))
            finish()
            return
        }

        // mostra il nome dello studente nella toolbar
        val nome = prefs.getString("nome", "") ?: ""
        if (nome.isNotEmpty()) {
            findViewById<TextView>(R.id.tvBenvenuto).text = "Ciao, $nome! 👋"
        }

        // mostra HomeFragment all'avvio
        if (savedInstanceState == null) {
            mostraFragment(HomeFragment())
        }

        // collega la bottom navigation ai Fragment
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home     -> mostraFragment(HomeFragment())
                R.id.nav_lezioni  -> mostraFragment(LezioniFragment())
                R.id.nav_esami    -> mostraFragment(EsamiFragment())
                R.id.nav_scadenze -> mostraFragment(ScadenzeFragment())
            }
            true
        }
    }

    private fun mostraFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}