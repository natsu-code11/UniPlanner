// ============================================================
// FILE: Lezione.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "lezioni" nel database.
//        Ogni campo di questa classe diventa una colonna nella tabella.
//        Aggiunto campo "data" per la data specifica della lezione.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)
// ============================================================

package com.uniplanner.app.data

import androidx.room.Entity      // trasforma questa classe in una tabella del database
import androidx.room.PrimaryKey  // definisce la chiave primaria

@Entity(tableName = "lezioni")  // la tabella nel database si chiamerà "lezioni"
data class Lezione(
    @PrimaryKey(autoGenerate = true)  // id generato automaticamente: 1, 2, 3...
    val id: Int = 0,            // id univoco della lezione
    val materia: String,        // nome della materia, es. "Matematica II"
    val giorno: String,         // giorno della settimana, es. "Lunedì"
    val ora: String,            // orario, es. "9:30 - 12:30"
    val aula: String,           // aula dove si tiene la lezione, es. "Aula 6" o "Teams"
    val data: String = ""       // data specifica della lezione, es. "21/09/2026"
    // default vuoto per compatibilità con lezioni già inserite
)