// FILE: Lezione.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/

package com.uniplanner.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// questa classe diventa una tabella nel database chiamata "lezioni"
@Entity(tableName = "lezioni")
data class Lezione(
    // id generato automaticamente, parte da 1
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val materia: String, // nome della materia, es. "matematica"
    val giorno: String,  // giorno della settimana, es. "lunedì"
    val ora: String,     // orario, es. "9:30 - 12:30"
    val aula: String,    // aula dove si tiene la lezione
    val data: String = "" // data specifica, aggiunta dopo — default vuoto per compatibilità
)