// ============================================================
// FILE: HomeFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/home/
// SCOPO: Home con riepilogo prossima scadenza urgente.
// LEZIONE DI RIFERIMENTO: L12 (Fragments), L15 (Room), L16 (SharedPreferences)
// ============================================================

package com.uniplanner.app.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.uniplanner.app.LoginActivity
import com.uniplanner.app.MainActivity
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("uniplanner_prefs", 0)
        val nome  = prefs.getString("nome", "") ?: ""

        if (nome.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tvSaluto).text = "Ciao, $nome! 👋"
        }

        // bottone Lezioni
        view.findViewById<Button>(R.id.btnLezioni).setOnClickListener {
            (activity as MainActivity).mostraFragment(LezioniFragment())
        }

        // bottone Esami
        view.findViewById<Button>(R.id.btnEsami).setOnClickListener {
            (activity as MainActivity).mostraFragment(EsamiFragment())
        }

        // bottone Scadenze
        view.findViewById<Button>(R.id.btnScadenze).setOnClickListener {
            (activity as MainActivity).mostraFragment(ScadenzeFragment())
        }

        // bottone Ripristina
        view.findViewById<Button>(R.id.btnRipristina).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Ripristina dati")
                .setMessage("Vuoi cancellare tutti i dati e ricominciare?")
                .setPositiveButton("Sì") { _, _ ->
                    prefs.edit().clear().apply()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.getInstance(requireContext()).clearAllTables()
                        }
                        requireActivity().recreate()
                    }
                }
                .setNegativeButton("Annulla", null)
                .show()
        }

        // bottone Logout
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().putBoolean("utente_autenticato", false).apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        // carica la prossima scadenza urgente
        caricaRiepilogo(view)
    }

    override fun onResume() {
        super.onResume()
        caricaRiepilogo(requireView())
    }

    // carica e mostra la prossima scadenza in Home
    private fun caricaRiepilogo(view: View) {
        lifecycleScope.launch {
            val prossima = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).scadenzaDao().getProssima()
            }

            val tvProssima = view.findViewById<TextView>(R.id.tvProssimaScadenza)
            if (prossima != null) {
                tvProssima.text       = "⏰ Prossima scadenza: ${prossima.titolo} entro ${prossima.data}"
                tvProssima.visibility = View.VISIBLE
            } else {
                tvProssima.visibility = View.GONE
            }
        }
    }
}