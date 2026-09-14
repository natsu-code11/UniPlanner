// ============================================================
// FILE: LezioneAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/lezioni/
// SCOPO: collega la lista delle lezioni alla recyclerview.
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

// l'adapter riceve la lista e una funzione da chiamare quando si elimina una lezione
class LezioneAdapter(
    private var lista: List<Lezione>,
    private val onElimina: (Lezione) -> Unit
) : RecyclerView.Adapter<LezioneAdapter.LezioneViewHolder>() {

    // teniamo i riferimenti ai widget della riga per non cercarli ogni volta
    inner class LezioneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMateria  = itemView.findViewById<TextView>(R.id.tvMateria)
        val tvAula     = itemView.findViewById<TextView>(R.id.tvAula)
        val tvOra      = itemView.findViewById<TextView>(R.id.tvOra)
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataLezione)
        val btnElimina = itemView.findViewById<Button>(R.id.btnElimina)
    }

    // crea una nuova riga partendo dal layout xml item_lezione
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LezioneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lezione, parent, false)
        return LezioneViewHolder(view)
    }

    // riempie ogni riga con i dati della lezione corrispondente
    override fun onBindViewHolder(holder: LezioneViewHolder, position: Int) {
        val lezione = lista[position]
        holder.tvMateria.text = lezione.materia
        holder.tvAula.text    = "Aula: ${lezione.aula}"
        holder.tvOra.text     = lezione.ora

        // la data è opzionale — la mostriamo solo se è stata inserita
        if (lezione.data.isNotEmpty()) {
            holder.tvData.text       = lezione.data
            holder.tvData.visibility = View.VISIBLE
        } else {
            holder.tvData.visibility = View.GONE
        }

        // toccando la riga apriamo il form di modifica con l'id della lezione
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AggiungiLezioneActivity::class.java)
            intent.putExtra("LEZIONE_ID", lezione.id)
            holder.itemView.context.startActivity(intent)
        }

        // prima di eliminare chiediamo conferma per evitare errori accidentali
        holder.btnElimina.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Elimina lezione")
                .setMessage("Vuoi eliminare ${lezione.materia}?")
                .setPositiveButton("Sì") { _, _ -> onElimina(lezione) }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    // numero totale di righe nella lista
    override fun getItemCount() = lista.size

    // sostituiamo la lista e ridisegniamo la recyclerview
    fun aggiorna(nuovaLista: List<Lezione>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}