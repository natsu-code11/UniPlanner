// ============================================================
// FILE: AppDatabase.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: È il punto di accesso unico al database dell'app.
//        Collega le 3 Entity e i 3 DAO in un unico database Room.
//        Usa il pattern Singleton: esiste UNA SOLA istanza in tutta l'app.
//        Versione 2: aggiunto campo "data" alla tabella lezioni.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Database, Singleton)
// ============================================================

package com.uniplanner.app.data

import android.content.Context              // serve per creare il database
import androidx.room.Database               // marca questa classe come database Room
import androidx.room.Room                   // classe che costruisce il database
import androidx.room.RoomDatabase           // classe base da cui estendere

@Database(
    entities = [Esame::class, Lezione::class, Scadenza::class],  // le 3 tabelle
    version = 2   // versione 2 — aumentata perché abbiamo aggiunto il campo "data" a Lezione
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun esameDao(): EsameDao          // accesso al DAO degli esami
    abstract fun lezioneDao(): LezioneDao      // accesso al DAO delle lezioni
    abstract fun scadenzaDao(): ScadenzaDao    // accesso al DAO delle scadenze

    companion object {

        @Volatile                              // garantisce che INSTANCE sia sempre aggiornata
        private var INSTANCE: AppDatabase? = null  // unica istanza del database

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,    // contesto dell'app
                    AppDatabase::class.java,       // classe del database
                    "uniplanner_database"          // nome del file del database
                )
                    .fallbackToDestructiveMigration()  // se cambia la versione, ricrea il DB
                    .build().also { INSTANCE = it }
            }
        }
    }
}