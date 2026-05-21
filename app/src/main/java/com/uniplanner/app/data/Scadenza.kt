// FILE: Scadenza.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "scadenze" nel database.
//        Contiene progetti, pagamenti, consegne e attività di studio
//        con priorità e stato di completamento.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)

package com.uniplanner.app.data  // cartella logica del file

import androidx.room.Entity      // trasforma questa classe in una tabella del database
import androidx.room.PrimaryKey  // definisce la chiave primaria

@Entity(tableName = "scadenze") // la tabella nel database si chiamerà "scadenze"
data class Scadenza(
    @PrimaryKey(autoGenerate = true)  // id generato automaticamente
    val id: Int = 0,            // id univoco della scadenza
    val titolo: String,         // titolo della scadenza, es. "Pagamento tasse"
    val tipo: String,           // tipo: "progetto", "pagamento", "studio", "consegna"
    val data: String,           // data entro cui completarla, es. "30/06/2026"
    val priorita: String,       // livello di urgenza: "alta", "media", "bassa"
    val completata: Boolean = false  // false = ancora da fare, true = completata
)