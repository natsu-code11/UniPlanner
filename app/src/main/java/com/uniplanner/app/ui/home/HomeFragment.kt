package com.uniplanner.app.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.uniplanner.app.MainActivity
import com.uniplanner.app.R
import com.uniplanner.app.ui.esami.EsamiFragment
import com.uniplanner.app.ui.lezioni.LezioniFragment
import com.uniplanner.app.ui.scadenze.ScadenzeFragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mostra il nome salvato
        val prefs = requireContext().getSharedPreferences("uniplanner_prefs", 0)
        val nome = prefs.getString("nome", "") ?: ""
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
                    requireActivity().recreate()
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }
}