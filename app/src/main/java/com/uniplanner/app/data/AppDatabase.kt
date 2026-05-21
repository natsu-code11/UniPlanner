// FILE: AppDatabase.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: È il punto di accesso unico al database dell'app.
//        Collega le 3 Entity e i 3 DAO in un unico database Room.
//        Usa il pattern Singleton: esiste UNA SOLA istanza in tutta l'app.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Database, Singleton)
package com.uniplanner.app.data

import android.content.Context              // serve per creare il database (ha bisogno del contesto Android)
import androidx.room.Database               // marca questa classe come database Room
import androidx.room.Room                   // classe che costruisce il database
import androidx.room.RoomDatabase           // classe base da cui estendere

@Database(
    entities = [Esame::class, Lezione::class, Scadenza::class],  // le 3 tabelle del database
    version = 1                             // versione del database (se cambi la struttura, aumenta questo numero)
)
abstract class AppDatabase : RoomDatabase() {  // abstract perché Room genera l'implementazione automaticamente

    abstract fun esameDao(): EsameDao          // punto di accesso al DAO degli esami
    abstract fun lezioneDao(): LezioneDao      // punto di accesso al DAO delle lezioni
    abstract fun scadenzaDao(): ScadenzaDao    // punto di accesso al DAO delle scadenze

    companion object {                         // companion object = metodi/variabili "statici" condivisi da tutti

        @Volatile                              // @Volatile garantisce che INSTANCE sia sempre aggiornata in tutti i thread
        private var INSTANCE: AppDatabase? = null  // l'unica istanza del database, null finché non viene creata

        fun getInstance(context: Context): AppDatabase {  // chiamata da ogni Fragment/Activity per ottenere il database
            return INSTANCE ?: synchronized(this) {        // se INSTANCE esiste già la restituisce, altrimenti la crea
                Room.databaseBuilder(
                    context.applicationContext,            // usa il contesto dell'app (non dell'Activity) per evitare memory leak
                    AppDatabase::class.java,               // la classe del database
                    "uniplanner_database"                  // nome del file del database sul dispositivo
                ).build().also { INSTANCE = it }           // salva l'istanza creata in INSTANCE
            }
        }
    }
}