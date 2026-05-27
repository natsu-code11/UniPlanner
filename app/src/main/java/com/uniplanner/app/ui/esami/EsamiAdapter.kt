package com.uniplanner.app.ui.esami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uniplanner.app.R
import com.uniplanner.app.data.Esame

class EsamiAdapter(
    private val onClick: (Esame) -> Unit
) : ListAdapter<Esame, EsamiAdapter.ViewHolder>(EsameDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeEsame: TextView = itemView.findViewById(R.id.tvNomeEsame)
        val tvDataEsame: TextView = itemView.findViewById(R.id.tvDataEsame)
        val tvCfuVoto: TextView = itemView.findViewById(R.id.tvCfuVoto)
        val tvStatoEsame: TextView = itemView.findViewById(R.id.tvStatoEsame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esame, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val esame = getItem(position)

        holder.tvNomeEsame.text = esame.nome
        holder.tvDataEsame.text = "Data: ${esame.data}"

        val votoTesto = if (esame.voto > 0) {
            esame.voto.toString()
        } else {
            "--"
        }

        holder.tvCfuVoto.text = "CFU: ${esame.cfu} | Voto: $votoTesto"
        holder.tvStatoEsame.text = "Stato: ${esame.stato}"

        holder.itemView.setOnClickListener {
            onClick(esame)
        }
    }
}

class EsameDiffCallback : DiffUtil.ItemCallback<Esame>() {

    override fun areItemsTheSame(oldItem: Esame, newItem: Esame): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Esame, newItem: Esame): Boolean {
        return oldItem == newItem
    }
}