// ============================================================
// FILE: EsamiAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/esami/
// SCOPO: Adapter esami con conferma eliminazione e colori stato.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.esami

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.Esame

class EsamiAdapter(
    private var lista: List<Esame>,
    private val onElimina: (Esame) -> Unit
) : RecyclerView.Adapter<EsamiAdapter.EsameViewHolder>() {

    inner class EsameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome     = itemView.findViewById<TextView>(R.id.tvNomeEsame)
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataEsame)
        val tvCfu      = itemView.findViewById<TextView>(R.id.tvCfuEsame)
        val tvVoto     = itemView.findViewById<TextView>(R.id.tvVotoEsame)
        val tvStato    = itemView.findViewById<TextView>(R.id.tvStatoEsame)
        val btnElimina = itemView.findViewById<Button>(R.id.btnEliminaEsame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esame, parent, false)
        return EsameViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = lista[position]
        holder.tvNome.text  = esame.nome
        holder.tvData.text  = "Data: ${esame.data}"
        holder.tvCfu.text   = "CFU: ${esame.cfu}"
        holder.tvVoto.text  = if (esame.voto > 0) "Voto: ${esame.voto}" else "Voto: --"
        holder.tvStato.text = esame.stato

        // colora la riga e lo stato in base al risultato
        if (esame.stato == "superato") {
            holder.tvStato.setTextColor(Color.parseColor("#2E7D32"))  // verde
            holder.itemView.setBackgroundColor(Color.parseColor("#F1F8E9"))  // verde chiaro
        } else {
            holder.tvStato.setTextColor(Color.parseColor("#C62828"))  // rosso
            holder.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"))  // rosso chiaro
        }

        // chiede conferma prima di eliminare
        holder.btnElimina.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Elimina esame")
                .setMessage("Vuoi eliminare ${esame.nome}?")
                .setPositiveButton("Sì") { _, _ -> onElimina(esame) }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    override fun getItemCount() = lista.size

    fun aggiorna(nuovaLista: List<Esame>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}