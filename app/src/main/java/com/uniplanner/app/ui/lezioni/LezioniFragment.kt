// ============================================================
// FILE: LezioniFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Schermata che mostra le lezioni divise per giorno.
//        L'utente seleziona il giorno con i bottoni in cima
//        e vede la lista delle lezioni. Può aggiungere o eliminare.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.lezioni

import android.content.Intent              // serve per aprire AggiungiLezioneActivity
import android.os.Bundle
import android.view.View
import android.widget.Button               // widget bottone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope   // scope per le coroutines nel Fragment
import androidx.recyclerview.widget.LinearLayoutManager  // layout verticale per la lista
import androidx.recyclerview.widget.RecyclerView         // lista scorrevole
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase               // accesso al database
import kotlinx.coroutines.Dispatchers                    // thread di esecuzione
import kotlinx.coroutines.launch                         // avvia una coroutine
import kotlinx.coroutines.withContext                    // cambia thread dentro la coroutine

class LezioniFragment : Fragment(R.layout.fragment_lezioni) {

    // tiene traccia del giorno selezionato — di default lunedì
    private var giornoSelezionato = "Lunedì"

    // adapter per la RecyclerView — inizializzato dopo la creazione della view
    private lateinit var adapter: LezioneAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bottone torna alla Home — svuota il backstack e torna alla Home
        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // collegamento alla RecyclerView nel layout
        val rvLezioni = view.findViewById<RecyclerView>(R.id.rvLezioni)

        // crea l'adapter — quando si preme X su una lezione la elimina dal database
        adapter = LezioneAdapter(emptyList()) { lezione ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // elimina la lezione dal database in background
                    AppDatabase.getInstance(requireContext()).lezioneDao().elimina(lezione)
                }
                caricaLezioni()  // ricarica la lista dopo l'eliminazione
            }
        }

        // imposta il layout verticale e collega l'adapter alla RecyclerView
        rvLezioni.layoutManager = LinearLayoutManager(requireContext())
        rvLezioni.adapter = adapter

        // bottone Lunedì — carica le lezioni del lunedì
        view.findViewById<Button>(R.id.btnLun).setOnClickListener {
            giornoSelezionato = "Lunedì"
            caricaLezioni()
        }
        // bottone Martedì
        view.findViewById<Button>(R.id.btnMar).setOnClickListener {
            giornoSelezionato = "Martedì"
            caricaLezioni()
        }
        // bottone Mercoledì
        view.findViewById<Button>(R.id.btnMer).setOnClickListener {
            giornoSelezionato = "Mercoledì"
            caricaLezioni()
        }
        // bottone Giovedì
        view.findViewById<Button>(R.id.btnGio).setOnClickListener {
            giornoSelezionato = "Giovedì"
            caricaLezioni()
        }
        // bottone Venerdì
        view.findViewById<Button>(R.id.btnVen).setOnClickListener {
            giornoSelezionato = "Venerdì"
            caricaLezioni()
        }

        // bottone aggiungi lezione — apre il form
        view.findViewById<Button>(R.id.btnAggiungiLezione).setOnClickListener {
            val intent = Intent(requireContext(), AggiungiLezioneActivity::class.java)
            startActivity(intent)
        }

        // carica le lezioni di lunedì all'avvio
        caricaLezioni()
    }

    // viene chiamato ogni volta che si torna a questo Fragment
    override fun onResume() {
        super.onResume()
        caricaLezioni()  // ricarica i dati freschi dal database
    }

    // legge le lezioni del giorno selezionato dal database e aggiorna la lista
    private fun caricaLezioni() {
        lifecycleScope.launch {
            val lezioni = withContext(Dispatchers.IO) {
                // legge dal database in background solo le lezioni del giorno selezionato
                AppDatabase.getInstance(requireContext())
                    .lezioneDao()
                    .getPerGiorno(giornoSelezionato)
            }
            // aggiorna l'adapter con i nuovi dati sul thread principale
            adapter.aggiorna(lezioni)
        }
    }
}