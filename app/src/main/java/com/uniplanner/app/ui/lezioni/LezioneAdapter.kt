// ============================================================
// FILE: LezioneAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: Adapter lezioni con conferma eliminazione e modifica.
//        Toccare una riga apre il form di modifica.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.lezioni

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.Lezione

class LezioneAdapter(
    private var lista: List<Lezione>,
    private val onElimina: (Lezione) -> Unit
) : RecyclerView.Adapter<LezioneAdapter.LezioneViewHolder>() {

    inner class LezioneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMateria  = itemView.findViewById<TextView>(R.id.tvMateria)
        val tvAula     = itemView.findViewById<TextView>(R.id.tvAula)
        val tvOra      = itemView.findViewById<TextView>(R.id.tvOra)
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataLezione)
        val btnElimina = itemView.findViewById<Button>(R.id.btnElimina)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LezioneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lezione, parent, false)
        return LezioneViewHolder(view)
    }

    override fun onBindViewHolder(holder: LezioneViewHolder, position: Int) {
        val lezione = lista[position]
        holder.tvMateria.text = lezione.materia
        holder.tvAula.text    = "Aula: ${lezione.aula}"
        holder.tvOra.text     = lezione.ora

        // mostra la data se presente
        if (lezione.data.isNotEmpty()) {
            holder.tvData.text       = lezione.data
            holder.tvData.visibility = View.VISIBLE
        } else {
            holder.tvData.visibility = View.GONE
        }

        // toccare la riga apre il form di modifica
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AggiungiLezioneActivity::class.java)
            intent.putExtra("LEZIONE_ID", lezione.id)  // passa l'id della lezione
            holder.itemView.context.startActivity(intent)
        }

        // chiede conferma prima di eliminare
        holder.btnElimina.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Elimina lezione")
                .setMessage("Vuoi eliminare ${lezione.materia}?")
                .setPositiveButton("Sì") { _, _ -> onElimina(lezione) }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    override fun getItemCount() = lista.size

    fun aggiorna(nuovaLista: List<Lezione>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}