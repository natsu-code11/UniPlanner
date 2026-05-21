// FILE: ScadenzaDao.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce le operazioni sul database per la tabella "scadenze".
//        Permette di aggiungere, modificare, eliminare e leggere
//        le scadenze ordinate per data e filtrate per stato.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Dao, @Query, @Insert, @Update, @Delete)
package com.uniplanner.app.data

import androidx.room.Dao         // marca questa interfaccia come DAO
import androidx.room.Delete      // annotazione per eliminare un record
import androidx.room.Insert      // annotazione per inserire un record
import androidx.room.Query       // annotazione per query SQL personalizzate
import androidx.room.Update      // annotazione per aggiornare un record

@Dao
interface ScadenzaDao {

    @Insert                      // inserisce una nuova scadenza nella tabella
    suspend fun inserisci(scadenza: Scadenza)

    @Update                      // aggiorna una scadenza esistente (es. cambio priorità o segna come completata)
    suspend fun aggiorna(scadenza: Scadenza)

    @Delete                      // elimina una scadenza dalla tabella
    suspend fun elimina(scadenza: Scadenza)

    @Query("SELECT * FROM scadenze ORDER BY data ASC")  // tutte le scadenze ordinate dalla più vicina
    suspend fun getTutte(): List<Scadenza>

    @Query("SELECT * FROM scadenze WHERE completata = 0 ORDER BY data ASC")  // solo quelle non ancora completate
    suspend fun getDaCompletare(): List<Scadenza>

    @Query("SELECT * FROM scadenze WHERE completata = 0 ORDER BY data ASC LIMIT 1")  // prossima scadenza (per la Home)
    suspend fun getProssima(): Scadenza?

    @Query("SELECT * FROM scadenze WHERE id = :id")  // cerca una scadenza specifica per id
    suspend fun getById(id: Int): Scadenza?
}