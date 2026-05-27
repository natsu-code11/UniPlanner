// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "esami" nel database locale.
//        Ogni campo di questa classe diventa una colonna nella tabella.
//        Room legge questa classe e crea automaticamente la tabella SQLite.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)

package com.uniplanner.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "esami")
data class Esame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,
    val data: String,
    val cfu: Int,
    val voto: Int,
    val stato: String,

    val corso: String = "",
    val anno: String = "",
    val semestre: String = ""
)