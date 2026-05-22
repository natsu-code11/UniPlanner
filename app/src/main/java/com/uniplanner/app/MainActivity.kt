// ============================================================
// FILE: MainActivity.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/
// SCOPO: Schermata principale dell'app. Gestisce la bottom
//        navigation bar e carica il Fragment corretto quando
//        l'utente tocca uno dei 4 tab in basso.
// LEZIONE DI RIFERIMENTO: L09 (Activity Lifecycle), L12 (Fragments)
// ============================================================

package com.uniplanner.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniplanner.app.R
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.home.HomeFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // collega il layout XML

        // mostra HomeFragment all'avvio dell'app
        if (savedInstanceState == null) {       // evita di ricaricare il Fragment se l'Activity viene ricreata (es. rotazione)
            mostraFragment(HomeFragment())
        }

        // collega la bottom navigation ai Fragment
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home     -> mostraFragment(HomeFragment())      // tab Home
                R.id.nav_lezioni  -> mostraFragment(LezioniFragment())   // tab Lezioni
                R.id.nav_esami    -> mostraFragment(EsamiFragment())     // tab Esami
                R.id.nav_scadenze -> mostraFragment(ScadenzeFragment())  // tab Scadenze
            }
            true  // restituisce true per indicare che il click è stato gestito
        }
    }

    // funzione che sostituisce il Fragment visibile nel contenitore
    private fun mostraFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()   // inizia una transazione
            .replace(R.id.fragmentContainer, fragment)  // sostituisce il Fragment nel contenitore
            .commit()                               // applica la transazione
    }
}