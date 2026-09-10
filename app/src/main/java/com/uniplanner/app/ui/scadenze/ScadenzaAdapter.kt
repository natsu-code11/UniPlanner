// ============================================================
// FILE: ScadenzaAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Adapter scadenze con conferma eliminazione e modifica.
//        Toccare una riga apre il form di modifica.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.scadenze

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
import com.uniplanner.app.data.Scadenza

class ScadenzaAdapter(
    private var lista: List<Scadenza>,
    private val onElimina: (Scadenza) -> Unit
) : RecyclerView.Adapter<ScadenzaAdapter.ScadenzaViewHolder>() {

    inner class ScadenzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitolo   = itemView.findViewById<TextView>(R.id.tvTitoloScadenza)
        val tvTipo     = itemView.findViewById<TextView>(R.id.tvTipoScadenza)
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataScadenza)
        val tvPriorita = itemView.findViewById<TextView>(R.id.tvPrioritaScadenza)
        val btnElimina = itemView.findViewById<Button>(R.id.btnEliminaScadenza)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScadenzaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scadenza, parent, false)
        return ScadenzaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScadenzaViewHolder, position: Int) {
        val scadenza = lista[position]
        holder.tvTitolo.text   = scadenza.titolo
        holder.tvTipo.text     = scadenza.tipo
        holder.tvData.text     = "Entro: ${scadenza.data}"
        holder.tvPriorita.text = "Priorità: ${scadenza.priorita}"

        // colora la riga in base alla priorità
        when (scadenza.priorita) {
            "alta" -> {
                holder.tvPriorita.setTextColor(Color.parseColor("#C62828"))
                holder.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"))
            }
            "media" -> {
                holder.tvPriorita.setTextColor(Color.parseColor("#E65100"))
                holder.itemView.setBackgroundColor(Color.parseColor("#FFF3E0"))
            }
            "bassa" -> {
                holder.tvPriorita.setTextColor(Color.parseColor("#2E7D32"))
                holder.itemView.setBackgroundColor(Color.parseColor("#F1F8E9"))
            }
        }

        // toccare la riga apre il form di modifica
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AggiungiScadenzaActivity::class.java)
            intent.putExtra("SCADENZA_ID", scadenza.id)
            holder.itemView.context.startActivity(intent)
        }

        // chiede conferma prima di eliminare
        holder.btnElimina.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Elimina scadenza")
                .setMessage("Vuoi eliminare ${scadenza.titolo}?")
                .setPositiveButton("Sì") { _, _ -> onElimina(scadenza) }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    override fun getItemCount() = lista.size

    fun aggiorna(nuovaLista: List<Scadenza>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}