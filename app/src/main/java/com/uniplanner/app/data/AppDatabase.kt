// FILE: AppDatabase.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// LEZIONE DI RIFERIMENTO: L15 (Room - @Database)

package com.uniplanner.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// qui elenchiamo le tre tabelle del database e la versione
// ogni volta che modifichiamo la struttura dobbiamo aumentare version
@Database(
    entities = [Esame::class, Lezione::class, Scadenza::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    // questi metodi ci danno accesso alle operazioni di ogni tabella
    abstract fun esameDao(): EsameDao
    abstract fun lezioneDao(): LezioneDao
    abstract fun scadenzaDao(): ScadenzaDao

    companion object {

        // @Volatile serve per evitare che due thread creino due database diversi
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // unico modo per ottenere il database in tutta l'app
        fun getInstance(context: Context): AppDatabase {
            // se esiste già lo restituiamo, altrimenti lo creiamo
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uniplanner_database"
                )
                    // se cambia la versione del database, lo ricrea da zero
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
        }
    }
}