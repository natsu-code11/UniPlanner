// ============================================================
// FILE: ScadenzeFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Schermata che mostra la lista delle scadenze ordinate
//        per data. L'utente può aggiungere ed eliminare scadenze.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.scadenze

import android.content.Intent              // per aprire AggiungiScadenzaActivity
import android.os.Bundle
import android.view.View
import android.widget.Button               // widget bottone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope   // scope coroutine per il Fragment
import androidx.recyclerview.widget.LinearLayoutManager  // layout verticale lista
import androidx.recyclerview.widget.RecyclerView         // lista scorrevole
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase               // accesso al database
import kotlinx.coroutines.Dispatchers                    // thread di esecuzione
import kotlinx.coroutines.launch                         // avvia una coroutine
import kotlinx.coroutines.withContext                    // cambia thread

class ScadenzeFragment : Fragment(R.layout.fragment_scadenze) {

    // adapter per la RecyclerView delle scadenze
    private lateinit var adapter: ScadenzaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bottone torna alla Home
        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // collegamento alla RecyclerView
        val rvScadenze = view.findViewById<RecyclerView>(R.id.rvScadenze)

        // crea l'adapter — quando si preme X elimina la scadenza
        adapter = ScadenzaAdapter(emptyList()) { scadenza ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // elimina la scadenza dal database in background
                    AppDatabase.getInstance(requireContext()).scadenzaDao().elimina(scadenza)
                }
                caricaScadenze()  // ricarica la lista dopo l'eliminazione
            }
        }

        // imposta il layout verticale e collega l'adapter
        rvScadenze.layoutManager = LinearLayoutManager(requireContext())
        rvScadenze.adapter = adapter

        // bottone aggiungi scadenza — apre il form
        view.findViewById<Button>(R.id.btnAggiungiScadenza).setOnClickListener {
            val intent = Intent(requireContext(), AggiungiScadenzaActivity::class.java)
            startActivity(intent)
        }

        // carica le scadenze all'avvio
        caricaScadenze()
    }

    // viene chiamato ogni volta che si torna a questo Fragment
    override fun onResume() {
        super.onResume()
        caricaScadenze()  // ricarica i dati freschi dal database
    }

    // legge le scadenze dal database e aggiorna la lista
    private fun caricaScadenze() {
        lifecycleScope.launch {
            val scadenze = withContext(Dispatchers.IO) {
                // legge tutte le scadenze ordinate per data in background
                AppDatabase.getInstance(requireContext()).scadenzaDao().getTutte()
            }
            // aggiorna l'adapter con i nuovi dati
            adapter.aggiorna(scadenze)
        }
    }
}