// ============================================================
// FILE: EsamiAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: collega la lista degli esami alla recyclerView.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.esami

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.Esame

// l'adapter riceve la lista e una funzione da chiamare quando si elimina un esame
class EsamiAdapter(
    private var lista: List<Esame>,
    private val onElimina: (Esame) -> Unit
) : RecyclerView.Adapter<EsamiAdapter.EsameViewHolder>() {

    // il viewholder tiene i riferimenti ai widget di una singola riga
    // così non li cerchiamo ogni volta che la riga scorre sullo schermo
    inner class EsameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome     = itemView.findViewById<TextView>(R.id.tvNomeEsame)
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataEsame)
        val tvCfu      = itemView.findViewById<TextView>(R.id.tvCfuEsame)
        val tvVoto     = itemView.findViewById<TextView>(R.id.tvVotoEsame)
        val tvStato    = itemView.findViewById<TextView>(R.id.tvStatoEsame)
        val btnElimina = itemView.findViewById<Button>(R.id.btnEliminaEsame)
    }

    // crea una nuova riga "gonfiando" il layout xml — viene chiamato poche volte
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esame, parent, false)
        return EsameViewHolder(view)
    }

    // riempie ogni riga con i dati dell'esame corrispondente
    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = lista[position]
        holder.tvNome.text  = esame.nome
        holder.tvData.text  = "Data: ${esame.data}"
        holder.tvCfu.text   = "CFU: ${esame.cfu}"
        // se il voto è 0 mostriamo -- perché l'esame non è ancora stato sostenuto
        holder.tvVoto.text  = if (esame.voto > 0) "Voto: ${esame.voto}" else "Voto: --"
        holder.tvStato.text = esame.stato

        // coloriamo la riga di verde se superato, rosso se da sostenere
        if (esame.stato == "superato") {
            holder.tvStato.setTextColor(Color.parseColor("#2E7D32"))
            holder.itemView.setBackgroundColor(Color.parseColor("#F1F8E9"))
        } else {
            holder.tvStato.setTextColor(Color.parseColor("#C62828"))
            holder.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"))
        }

        // toccando la riga apriamo il form di modifica passando l'id dell'esame
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AggiungiEsameActivity::class.java)
            intent.putExtra("ESAME_ID", esame.id)
            holder.itemView.context.startActivity(intent)
        }

        // prima di eliminare chiediamo conferma per evitare eliminazioni accidentali
        holder.btnElimina.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Elimina esame")
                .setMessage("Vuoi eliminare ${esame.nome}?")
                .setPositiveButton("Sì") { _, _ -> onElimina(esame) }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    // dice alla recyclerview quante righe deve mostrare
    override fun getItemCount() = lista.size

    // aggiorniamo la lista e ridisegniamo tutto
    fun aggiorna(nuovaLista: List<Esame>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}