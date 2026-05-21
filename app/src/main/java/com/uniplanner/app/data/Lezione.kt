// FILE: Lezione.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "lezioni" nel database.
//        Le lezioni vengono caricate dal file JSON in assets/
//        al primo avvio dell'app e salvate qui.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)

package com.uniplanner.app.data  // cartella logica del file

import androidx.room.Entity      // trasforma questa classe in una tabella del database
import androidx.room.PrimaryKey  // definisce la chiave primaria

@Entity(tableName = "lezioni")  // la tabella nel database si chiamerà "lezioni"
data class Lezione(
    @PrimaryKey(autoGenerate = true)  // id generato automaticamente
    val id: Int = 0,            // id univoco della lezione
    val materia: String,        // nome della materia, es. "Matematica"
    val giorno: String,         // giorno della settimana, es. "Lunedì"
    val ora: String,            // orario, es. "09:00"
    val aula: String            // aula dove si tiene la lezione, es. "A1"
)