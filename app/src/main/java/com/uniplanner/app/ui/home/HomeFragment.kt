// ============================================================
// FILE: HomeFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/home/
// SCOPO: Schermata principale con 4 bottoni per navigare verso
//        Lezioni, Esami, Scadenze e Ripristina dati.
// LEZIONE DI RIFERIMENTO: L12 (Fragments), L11 (Intent), L16 (SharedPreferences)
// ============================================================

package com.uniplanner.app.ui.home

import android.app.AlertDialog                // per mostrare il dialogo di conferma ripristino
import android.os.Bundle
import android.view.View
import android.widget.Button                  // widget bottone
import android.widget.TextView               // widget testo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope      // scope coroutine per il Fragment
import com.uniplanner.app.MainActivity
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase    // accesso al database Room
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment
import kotlinx.coroutines.Dispatchers         // thread di esecuzione
import kotlinx.coroutines.launch              // avvia una coroutine
import kotlinx.coroutines.withContext         // cambia thread dentro la coroutine

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // legge le SharedPreferences per mostrare il nome dello studente
        val prefs = requireContext().getSharedPreferences("uniplanner_prefs", 0)
        val nome = prefs.getString("nome", "") ?: ""

        // mostra il saluto personalizzato con il nome salvato
        if (nome.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tvSaluto).text = "Ciao, $nome! 👋"
        }

        // bottone Lezioni — apre la sezione lezioni
        view.findViewById<Button>(R.id.btnLezioni).setOnClickListener {
            (activity as MainActivity).mostraFragment(LezioniFragment())
        }

        // bottone Esami — apre la sezione esami
        view.findViewById<Button>(R.id.btnEsami).setOnClickListener {
            (activity as MainActivity).mostraFragment(EsamiFragment())
        }

        // bottone Scadenze — apre la sezione scadenze
        view.findViewById<Button>(R.id.btnScadenze).setOnClickListener {
            (activity as MainActivity).mostraFragment(ScadenzeFragment())
        }

        // bottone Ripristina — chiede conferma e cancella tutti i dati
        view.findViewById<Button>(R.id.btnRipristina).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Ripristina dati")
                .setMessage("Vuoi cancellare tutti i dati e ricominciare dal primo accesso?")
                .setPositiveButton("Sì") { _, _ ->
                    // cancella le SharedPreferences (nome, corso, semestre, ecc.)
                    prefs.edit().clear().apply()
                    // cancella tutto il database Room in background
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            // svuota tutte le tabelle del database
                            AppDatabase.getInstance(requireContext()).clearAllTables()
                        }
                        // riavvia l'app dopo la pulizia
                        requireActivity().recreate()
                    }
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }
}