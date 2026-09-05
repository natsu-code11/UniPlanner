// ============================================================
// FILE: ScadenzaAdapter.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/ui/scadenze/
// SCOPO: Adapter per la RecyclerView delle scadenze.
//        Mostra titolo, tipo, data e priorità di ogni scadenza.
// LEZIONE DI RIFERIMENTO: L13 (RecyclerView, Adapter, ViewHolder)
// ============================================================

package com.uniplanner.app.ui.scadenze

import android.view.LayoutInflater   // serve per gonfiare il layout XML di una riga
import android.view.View             // rappresenta un elemento visivo sullo schermo
import android.view.ViewGroup        // contenitore di View
import android.widget.Button         // widget bottone
import android.widget.TextView       // widget testo
import androidx.recyclerview.widget.RecyclerView  // classe base della lista scorrevole
import com.uniplanner.app.R          // riferimento alle risorse del progetto
import com.uniplanner.app.data.Scadenza  // il modello dati della scadenza

class ScadenzaAdapter(
    private var lista: List<Scadenza>,            // lista di scadenze da mostrare
    private val onElimina: (Scadenza) -> Unit     // funzione chiamata quando si preme X
) : RecyclerView.Adapter<ScadenzaAdapter.ScadenzaViewHolder>() {

    // ViewHolder: contiene i riferimenti ai widget di una singola riga
    inner class ScadenzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitolo   = itemView.findViewById<TextView>(R.id.tvTitoloScadenza)    // titolo
        val tvTipo     = itemView.findViewById<TextView>(R.id.tvTipoScadenza)      // tipo
        val tvData     = itemView.findViewById<TextView>(R.id.tvDataScadenza)      // data
        val tvPriorita = itemView.findViewById<TextView>(R.id.tvPrioritaScadenza)  // priorità
        val btnElimina = itemView.findViewById<Button>(R.id.btnEliminaScadenza)    // bottone elimina
    }

    // crea una nuova riga gonfiando il layout item_scadenza.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScadenzaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scadenza, parent, false)
        return ScadenzaViewHolder(view)
    }

    // riempie una riga con i dati della scadenza alla posizione corrente
    override fun onBindViewHolder(holder: ScadenzaViewHolder, position: Int) {
        val scadenza = lista[position]                          // prende la scadenza
        holder.tvTitolo.text   = scadenza.titolo               // mostra il titolo
        holder.tvTipo.text     = scadenza.tipo                 // mostra il tipo
        holder.tvData.text     = "Entro: ${scadenza.data}"    // mostra la data
        holder.tvPriorita.text = "Priorità: ${scadenza.priorita}"  // mostra la priorità

        // colora la priorità: rosso alta, arancione media, verde bassa
        when (scadenza.priorita) {
            "alta"  -> holder.tvPriorita.setTextColor(android.graphics.Color.parseColor("#C62828"))
            "media" -> holder.tvPriorita.setTextColor(android.graphics.Color.parseColor("#E65100"))
            "bassa" -> holder.tvPriorita.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        }

        // quando si preme X chiama la funzione di eliminazione
        holder.btnElimina.setOnClickListener {
            onElimina(scadenza)
        }
    }

    // restituisce il numero totale di scadenze nella lista
    override fun getItemCount() = lista.size

    // aggiorna la lista e ridisegna la RecyclerView
    fun aggiorna(nuovaLista: List<Scadenza>) {
        lista = nuovaLista       // sostituisce la lista vecchia
        notifyDataSetChanged()   // dice alla RecyclerView di ridisegnarsi
    }
}