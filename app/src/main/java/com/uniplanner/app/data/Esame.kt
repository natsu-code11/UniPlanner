// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce la struttura della tabella "esami" nel database locale.
//        Ogni campo di questa classe diventa una colonna nella tabella.
//        Room legge questa classe e crea automaticamente la tabella SQLite.
// LEZIONE DI RIFERIMENTO: L05 (data class), L15 (Room - @Entity)

package com.uniplanner.app.data  // dice a Kotlin in quale cartella logica si trova questo file

import androidx.room.Entity      // importa l'annotazione che trasforma questa classe in una tabella del database
import androidx.room.PrimaryKey  // importa l'annotazione per definire la chiave primaria (l'id univoco)

@Entity(tableName = "esami")    // questa classe diventa una tabella nel database chiamata "esami"
data class Esame(               // data class = classe pensata per contenere dati
    @PrimaryKey(autoGenerate = true)  // id generato automaticamente: 1, 2, 3...
    val id: Int = 0,            // id univoco dell'esame
    val nome: String,           // nome dell'esame, es. "Matematica"
    val data: String,           // data dell'appello, es. "15/06/2026"
    val cfu: Int,               // crediti formativi, es. 9
    val voto: Int,              // voto preso, es. 28 (0 se da sostenere)
    val stato: String           // "da sostenere" oppure "superato"
)