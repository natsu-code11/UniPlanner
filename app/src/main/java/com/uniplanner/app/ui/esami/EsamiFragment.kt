package com.uniplanner.app.ui.esami

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.MainActivity
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import com.uniplanner.app.data.Esame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EsamiFragment : Fragment(R.layout.fragment_esami) {

    private lateinit var adapter: EsamiAdapter
    private lateinit var tvMedia: TextView
    private lateinit var recyclerViewEsami: RecyclerView
    private lateinit var btnAggiungiEsame: Button
    private lateinit var btnTornaHome: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMedia = view.findViewById(R.id.tvMedia)
        recyclerViewEsami = view.findViewById(R.id.recyclerViewEsami)
        btnAggiungiEsame = view.findViewById(R.id.btnAggiungiEsame)
        btnTornaHome = view.findViewById(R.id.btnTornaHome)

        adapter = EsamiAdapter { esame ->
            val intent = Intent(requireContext(), AggiungiEsameActivity::class.java)

            intent.putExtra("ESAME_ID", esame.id)
            intent.putExtra("ESAME_NOME", esame.nome)
            intent.putExtra("ESAME_DATA", esame.data)
            intent.putExtra("ESAME_CFU", esame.cfu)
            intent.putExtra("ESAME_VOTO", esame.voto)
            intent.putExtra("ESAME_STATO", esame.stato)

            startActivity(intent)
        }

        recyclerViewEsami.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewEsami.adapter = adapter

        btnAggiungiEsame.setOnClickListener {
            val intent = Intent(requireContext(), AggiungiEsameActivity::class.java)
            startActivity(intent)
        }

        btnTornaHome.setOnClickListener {
            Toast.makeText(requireContext(), "Torno alla Home", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        caricaEsami()
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            caricaEsami()
        }
    }

    private fun caricaEsami() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())

            val prefs = requireContext().getSharedPreferences(
                "uniplanner_prefs",
                Context.MODE_PRIVATE
            )

            val corso = prefs.getString("corso", "") ?: ""
            val anno = prefs.getString("anno", "") ?: ""
            val semestre = prefs.getString("semestre", "") ?: ""

            val esami = withContext(Dispatchers.IO) {
                db.esameDao().getPerProfilo(corso, anno, semestre)
            }

            adapter.submitList(esami)

            val media = calcolaMediaPesata(esami)

            tvMedia.text = if (media > 0) {
                "Media: %.1f".format(media)
            } else {
                "Media: --"
            }
        }
    }

    private fun calcolaMediaPesata(esami: List<Esame>): Double {
        val superati = esami.filter {
            it.stato == "superato" && it.voto > 0
        }

        if (superati.isEmpty()) {
            return 0.0
        }

        val totalePunti = superati.sumOf {
            it.voto * it.cfu
        }

        val totaleCfu = superati.sumOf {
            it.cfu
        }

        return totalePunti.toDouble() / totaleCfu
    }
}