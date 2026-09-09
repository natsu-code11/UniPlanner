// ============================================================
// FILE: LezioniFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Schermata che mostra le lezioni divise per giorno.
//        Mostra messaggio quando lista è vuota.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.lezioni

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LezioniFragment : Fragment(R.layout.fragment_lezioni) {

    // tiene traccia del giorno selezionato — di default lunedì
    private var giornoSelezionato = "Lunedì"

    // adapter per la RecyclerView
    private lateinit var adapter: LezioneAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bottone torna alla Home
        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // collegamento alla RecyclerView
        val rvLezioni = view.findViewById<RecyclerView>(R.id.rvLezioni)

        // crea l'adapter con funzione di eliminazione
        adapter = LezioneAdapter(emptyList()) { lezione ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext()).lezioneDao().elimina(lezione)
                }
                caricaLezioni()
            }
        }

        rvLezioni.layoutManager = LinearLayoutManager(requireContext())
        rvLezioni.adapter = adapter

        // bottoni giorni settimana
        view.findViewById<Button>(R.id.btnLun).setOnClickListener {
            giornoSelezionato = "Lunedì"
            caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnMar).setOnClickListener {
            giornoSelezionato = "Martedì"
            caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnMer).setOnClickListener {
            giornoSelezionato = "Mercoledì"
            caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnGio).setOnClickListener {
            giornoSelezionato = "Giovedì"
            caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnVen).setOnClickListener {
            giornoSelezionato = "Venerdì"
            caricaLezioni()
        }

        // bottone aggiungi lezione
        view.findViewById<Button>(R.id.btnAggiungiLezione).setOnClickListener {
            startActivity(Intent(requireContext(), AggiungiLezioneActivity::class.java))
        }

        caricaLezioni()
    }

    override fun onResume() {
        super.onResume()
        caricaLezioni()
    }

    private fun caricaLezioni() {
        lifecycleScope.launch {
            val lezioni = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext())
                    .lezioneDao()
                    .getPerGiorno(giornoSelezionato)
            }
            adapter.aggiorna(lezioni)

            // mostra messaggio lista vuota o la lista
            val tvVuota = view?.findViewById<TextView>(R.id.tvListaVuotaLezioni)
            val rv      = view?.findViewById<RecyclerView>(R.id.rvLezioni)
            if (lezioni.isEmpty()) {
                tvVuota?.visibility = View.VISIBLE  // mostra messaggio
                rv?.visibility      = View.GONE     // nasconde lista
            } else {
                tvVuota?.visibility = View.GONE     // nasconde messaggio
                rv?.visibility      = View.VISIBLE  // mostra lista
            }
        }
    }
}