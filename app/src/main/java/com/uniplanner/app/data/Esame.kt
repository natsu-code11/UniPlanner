// FILE: Esame.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/


package com.uniplanner.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// diciamo a room che questa classe è una tabella del database
@Entity(tableName = "esami")
// data class è perfetta per contenere dati — kotlin genera automaticamente equals e toString
data class Esame(
    // l'id viene generato automaticamente in ordine crescente (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,   // nome dell'esame
    val data: String,   // data dell'appello
    val cfu: Int,       // crediti formativi
    val voto: Int,      // voto preso, 0 se non ancora sostenuto
    val stato: String,  // "da sostenere" oppure "superato"

    // questi tre campi hanno un valore di default vuoto
    // servono per collegare l'esame al corso dello studente
    val corso: String = "",
    val anno: String = "",
    val semestre: String = ""
)