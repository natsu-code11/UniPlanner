// ============================================================
// FILE: LezioniFragment.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Schermata lezioni con aggiunta manuale e importazione PDF.
// LEZIONE DI RIFERIMENTO: L12 (Fragment), L13 (RecyclerView), L15 (Room)
// ============================================================

package com.uniplanner.app.ui.lezioni

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.AppDatabase
import com.uniplanner.app.utils.PdfImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LezioniFragment : Fragment(R.layout.fragment_lezioni) {

    private var giornoSelezionato = "Lunedì"
    private lateinit var adapter: LezioneAdapter

    // launcher per aprire il selettore file PDF
    private val selettorePdf = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            // importa le lezioni dal PDF selezionato
            lifecycleScope.launch {
                val lezioni = withContext(Dispatchers.IO) {
                    PdfImporter.importaDaPdf(requireContext(), uri)
                }

                if (lezioni.isEmpty()) {
                    // nessuna lezione trovata nel PDF
                    AlertDialog.Builder(requireContext())
                        .setTitle("Nessuna lezione trovata")
                        .setMessage("Non ho trovato lezioni nel PDF. Assicurati di aver selezionato il calendario della Parthenope.")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    // mostra quante lezioni ha trovato e chiede conferma
                    AlertDialog.Builder(requireContext())
                        .setTitle("Lezioni trovate")
                        .setMessage("Ho trovato ${lezioni.size} lezioni nel PDF. Vuoi importarle tutte?")
                        .setPositiveButton("Sì, importa") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    // salva tutte le lezioni nel database
                                    AppDatabase.getInstance(requireContext())
                                        .lezioneDao()
                                        .inserisciTutte(lezioni)
                                }
                                caricaLezioni()  // ricarica la lista
                            }
                        }
                        .setNegativeButton("Annulla", null)
                        .show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnTornaHome).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val rvLezioni = view.findViewById<RecyclerView>(R.id.rvLezioni)

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

        // bottoni giorni
        view.findViewById<Button>(R.id.btnLun).setOnClickListener {
            giornoSelezionato = "Lunedì"; caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnMar).setOnClickListener {
            giornoSelezionato = "Martedì"; caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnMer).setOnClickListener {
            giornoSelezionato = "Mercoledì"; caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnGio).setOnClickListener {
            giornoSelezionato = "Giovedì"; caricaLezioni()
        }
        view.findViewById<Button>(R.id.btnVen).setOnClickListener {
            giornoSelezionato = "Venerdì"; caricaLezioni()
        }

        // bottone aggiungi lezione manualmente
        view.findViewById<Button>(R.id.btnAggiungiLezione).setOnClickListener {
            startActivity(Intent(requireContext(), AggiungiLezioneActivity::class.java))
        }

        // bottone importa da PDF — apre il selettore file
        view.findViewById<Button>(R.id.btnImportaPdf).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"  // mostra solo file PDF
            }
            selettorePdf.launch(intent)
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

            val tvVuota = view?.findViewById<TextView>(R.id.tvListaVuotaLezioni)
            val rv      = view?.findViewById<RecyclerView>(R.id.rvLezioni)
            if (lezioni.isEmpty()) {
                tvVuota?.visibility = View.VISIBLE
                rv?.visibility      = View.GONE
            } else {
                tvVuota?.visibility = View.GONE
                rv?.visibility      = View.VISIBLE
            }
        }
    }
}