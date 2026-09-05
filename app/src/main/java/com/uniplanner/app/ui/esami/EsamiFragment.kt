// ============================================================
// FILE: EsamiFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: Schermata che mostra la lista degli esami.
//        Calcola e mostra la media voti in cima.
//        L'utente può aggiungere ed eliminare esami.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.esami

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

class EsamiFragment : Fragment(R.layout.fragment_esami) {

    // adapter per la RecyclerView degli esami
    private lateinit var adapter: EsamiAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bottone torna alla Home — torna al Fragment precedente
        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // collegamento alla RecyclerView
        val rvEsami = view.findViewById<RecyclerView>(R.id.rvEsami)

        // crea l'adapter — quando si preme X elimina l'esame
        adapter = EsamiAdapter(emptyList()) { esame ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // elimina l'esame dal database in background
                    AppDatabase.getInstance(requireContext()).esameDao().elimina(esame)
                }
                caricaEsami()  // ricarica la lista dopo l'eliminazione
            }
        }

        // imposta il layout verticale e collega l'adapter
        rvEsami.layoutManager = LinearLayoutManager(requireContext())
        rvEsami.adapter = adapter

        // bottone aggiungi esame — apre il form
        view.findViewById<Button>(R.id.btnAggiungiEsame).setOnClickListener {
            val intent = Intent(requireContext(), AggiungiEsameActivity::class.java)
            startActivity(intent)
        }

        // carica gli esami all'avvio
        caricaEsami()
    }

    // viene chiamato ogni volta che si torna a questo Fragment
    override fun onResume() {
        super.onResume()
        caricaEsami()
    }

    // legge gli esami dal database e aggiorna lista e media
    private fun caricaEsami() {
        lifecycleScope.launch {
            val esami = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).esameDao().getTutti()
            }
            adapter.aggiorna(esami)

            // calcola la media pesata sugli esami superati
            val soloSuperati = esami.filter { it.stato == "superato" && it.voto > 0 }
            if (soloSuperati.isEmpty()) {
                view?.findViewById<TextView>(R.id.tvMedia)?.text = "Media: --"
            } else {
                val sommaPesata = soloSuperati.sumOf { it.voto * it.cfu }
                val sommaCfu    = soloSuperati.sumOf { it.cfu }
                val media       = sommaPesata.toDouble() / sommaCfu
                view?.findViewById<TextView>(R.id.tvMedia)?.text = "Media: ${"%.2f".format(media)}"
            }
        }
    }
}