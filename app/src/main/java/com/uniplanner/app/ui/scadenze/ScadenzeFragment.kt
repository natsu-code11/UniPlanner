// ============================================================
// FILE: ScadenzeFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Schermata scadenze con lista e messaggio lista vuota.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.scadenze

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

class ScadenzeFragment : Fragment(R.layout.fragment_scadenze) {

    private lateinit var adapter: ScadenzaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val rvScadenze = view.findViewById<RecyclerView>(R.id.rvScadenze)

        adapter = ScadenzaAdapter(emptyList()) { scadenza ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext()).scadenzaDao().elimina(scadenza)
                }
                caricaScadenze()
            }
        }

        rvScadenze.layoutManager = LinearLayoutManager(requireContext())
        rvScadenze.adapter = adapter

        view.findViewById<Button>(R.id.btnAggiungiScadenza).setOnClickListener {
            startActivity(Intent(requireContext(), AggiungiScadenzaActivity::class.java))
        }

        caricaScadenze()
    }

    override fun onResume() {
        super.onResume()
        caricaScadenze()
    }

    private fun caricaScadenze() {
        lifecycleScope.launch {
            val scadenze = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).scadenzaDao().getTutte()
            }
            adapter.aggiorna(scadenze)

            // mostra messaggio lista vuota o la lista
            val tvVuota = view?.findViewById<TextView>(R.id.tvListaVuotaScadenze)
            val rv      = view?.findViewById<RecyclerView>(R.id.rvScadenze)
            if (scadenze.isEmpty()) {
                tvVuota?.visibility = View.VISIBLE
                rv?.visibility      = View.GONE
            } else {
                tvVuota?.visibility = View.GONE
                rv?.visibility      = View.VISIBLE
            }
        }
    }
}