// ============================================================
// FILE: EsamiAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: Adapter per la RecyclerView degli esami.
//        Mostra nome, data, CFU, voto e stato di ogni esame.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.esami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.Esame

class EsamiAdapter(
    private var lista: List<Esame>,          // lista di esami da mostrare
    private val onElimina: (Esame) -> Unit   // funzione chiamata quando si preme X
) : RecyclerView.Adapter<EsamiAdapter.EsameViewHolder>() {

    // ViewHolder: contiene i riferimenti ai widget di una singola riga
    inner class EsameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome     = itemView.findViewById<TextView>(R.id.tvNomeEsame)    // nome esame
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataEsame)    // data appello
        val tvCfu      = itemView.findViewById<TextView>(R.id.tvCfuEsame)     // CFU
        val tvVoto     = itemView.findViewById<TextView>(R.id.tvVotoEsame)    // voto
        val tvStato    = itemView.findViewById<TextView>(R.id.tvStatoEsame)   // stato
        val btnElimina = itemView.findViewById<Button>(R.id.btnEliminaEsame)  // bottone elimina
    }

    // crea una nuova riga gonfiando il layout item_esame.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esame, parent, false)
        return EsameViewHolder(view)
    }

    // riempie una riga con i dati dell'esame alla posizione corrente
    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = lista[position]
        holder.tvNome.text  = esame.nome
        holder.tvData.text  = "Data: ${esame.data}"
        holder.tvCfu.text   = "CFU: ${esame.cfu}"
        holder.tvVoto.text  = if (esame.voto > 0) "Voto: ${esame.voto}" else "Voto: --"
        holder.tvStato.text = esame.stato

        // colora lo stato in verde se superato, rosso se da sostenere
        if (esame.stato == "superato") {
            holder.tvStato.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        } else {
            holder.tvStato.setTextColor(android.graphics.Color.parseColor("#C62828"))
        }

        // quando si preme X chiama la funzione di eliminazione
        holder.btnElimina.setOnClickListener {
            onElimina(esame)
        }
    }

    // restituisce il numero totale di esami nella lista
    override fun getItemCount() = lista.size

    // aggiorna la lista e ridisegna la RecyclerView
    fun aggiorna(nuovaLista: List<Esame>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}