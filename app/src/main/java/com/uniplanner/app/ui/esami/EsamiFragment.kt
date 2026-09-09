// ============================================================
// FILE: EsamiFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: Schermata esami con contatore e media voti.
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

    private lateinit var adapter: EsamiAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val rvEsami = view.findViewById<RecyclerView>(R.id.rvEsami)

        adapter = EsamiAdapter(emptyList()) { esame ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext()).esameDao().elimina(esame)
                }
                caricaEsami()
            }
        }

        rvEsami.layoutManager = LinearLayoutManager(requireContext())
        rvEsami.adapter = adapter

        view.findViewById<Button>(R.id.btnAggiungiEsame).setOnClickListener {
            startActivity(Intent(requireContext(), AggiungiEsameActivity::class.java))
        }

        caricaEsami()
    }

    override fun onResume() {
        super.onResume()
        caricaEsami()
    }

    private fun caricaEsami() {
        lifecycleScope.launch {
            val esami = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).esameDao().getTutti()
            }
            adapter.aggiorna(esami)

            // mostra messaggio lista vuota o la lista
            val tvVuota = view?.findViewById<TextView>(R.id.tvListaVuotaEsami)
            val rv      = view?.findViewById<RecyclerView>(R.id.rvEsami)
            if (esami.isEmpty()) {
                tvVuota?.visibility = View.VISIBLE
                rv?.visibility      = View.GONE
            } else {
                tvVuota?.visibility = View.GONE
                rv?.visibility      = View.VISIBLE
            }

            // contatore esami superati su totale
            val superati = esami.count { it.stato == "superato" }
            val totale   = esami.size
            view?.findViewById<TextView>(R.id.tvContatore)?.text =
                "Superati: $superati su $totale"

            // calcola media pesata
            val soloSuperati = esami.filter { it.stato == "superato" && it.voto > 0 }
            if (soloSuperati.isEmpty()) {
                view?.findViewById<TextView>(R.id.tvMedia)?.text = "Media: --"
            } else {
                val sommaPesata = soloSuperati.sumOf { it.voto * it.cfu }
                val sommaCfu    = soloSuperati.sumOf { it.cfu }
                val media       = sommaPesata.toDouble() / sommaCfu
                view?.findViewById<TextView>(R.id.tvMedia)?.text =
                    "Media: ${"%.2f".format(media)}"
            }
        }
    }
}