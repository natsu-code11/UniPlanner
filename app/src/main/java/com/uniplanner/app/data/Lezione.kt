// ============================================================
// FILE: Lezione.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "lezioni".
//        Aggiunto campo "data" per la data specifica della lezione.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)
// ============================================================

package com.uniplanner.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lezioni")
data class Lezione(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String,
    val giorno: String,
    val ora: String,
    val aula: String,
    val data: String = ""
)