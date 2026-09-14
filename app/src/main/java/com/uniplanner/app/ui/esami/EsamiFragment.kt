// ============================================================
// FILE: EsamiFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: schermata che mostra gli esami con media e contatore.
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

// colleghiamo il fragment al suo layout xml
class EsamiFragment : Fragment(R.layout.fragment_esami) {

    // lo dichiariamo qui perché ci serve in più punti del fragment
    private lateinit var adapter: EsamiAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // premendo il bottone torniamo al fragment precedente
        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val rvEsami = view.findViewById<RecyclerView>(R.id.rvEsami)

        // creiamo l'adapter — quando si elimina un esame lo cancelliamo dal database
        // e ricarichiamo la lista per aggiornare quello che vede l'utente
        adapter = EsamiAdapter(emptyList()) { esame ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext()).esameDao().elimina(esame)
                }
                caricaEsami()
            }
        }

        // diciamo alla recyclerview come disporre le righe (una sotto l'altra)
        rvEsami.layoutManager = LinearLayoutManager(requireContext())
        rvEsami.adapter = adapter

        view.findViewById<Button>(R.id.btnAggiungiEsame).setOnClickListener {
            startActivity(Intent(requireContext(), AggiungiEsameActivity::class.java))
        }

        caricaEsami()
    }

    // ogni volta che torniamo su questa schermata ricarichiamo i dati
    // così le modifiche fatte nel form di aggiunta si vedono subito
    override fun onResume() {
        super.onResume()
        caricaEsami()
    }

    private fun caricaEsami() {
        // le operazioni sul database le facciamo in background con le coroutines
        lifecycleScope.launch {
            val esami = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).esameDao().getTutti()
            }
            adapter.aggiorna(esami)

            // se non ci sono esami mostriamo un messaggio, altrimenti la lista
            val tvVuota = view?.findViewById<TextView>(R.id.tvListaVuotaEsami)
            val rv      = view?.findViewById<RecyclerView>(R.id.rvEsami)
            if (esami.isEmpty()) {
                tvVuota?.visibility = View.VISIBLE
                rv?.visibility      = View.GONE
            } else {
                tvVuota?.visibility = View.GONE
                rv?.visibility      = View.VISIBLE
            }

            // contiamo quanti esami sono stati superati e mostriamo il totale
            val superati = esami.count { it.stato == "superato" }
            val totale   = esami.size
            view?.findViewById<TextView>(R.id.tvContatore)?.text =
                "Superati: $superati su $totale"

            // calcoliamo la media pesata: somma(voto x cfu) / somma(cfu)
            // prendiamo solo gli esami superati con un voto valido
            val soloSuperati = esami.filter { it.stato == "superato" && it.voto > 0 }
            if (soloSuperati.isEmpty()) {
                view?.findViewById<TextView>(R.id.tvMedia)?.text = "Media: --"
            } else {
                val sommaPesata = soloSuperati.sumOf { it.voto * it.cfu }
                val sommaCfu    = soloSuperati.sumOf { it.cfu }
                val media       = sommaPesata.toDouble() / sommaCfu
                // formattiamo la media con due decimali
                view?.findViewById<TextView>(R.id.tvMedia)?.text =
                    "Media: ${"%.2f".format(media)}"
            }
        }
    }
}