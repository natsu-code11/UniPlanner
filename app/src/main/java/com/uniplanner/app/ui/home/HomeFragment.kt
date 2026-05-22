// ============================================================
// FILE: HomeFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/home/
// SCOPO: Schermata principale dell'app. Mostra un riepilogo rapido
//        della prossima lezione, del prossimo esame e della
//        prossima scadenza. Viene mostrata all'avvio dell'app.
// LEZIONE DI RIFERIMENTO: L12 (Fragments), L15 (Room @Query)
// ============================================================

package com.uniplanner.app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {  // collega questo Fragment al suo layout XML

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // il codice verrà completato nella Fase 2 (tutti insieme)
        // per ora il Fragment è vuoto ma funzionante
    }
}