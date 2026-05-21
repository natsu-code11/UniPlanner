// FILE: EsameDao.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce le operazioni sul database per la tabella "esami".
//        Room genera automaticamente il codice SQL da queste funzioni.
//        "suspend" significa che vanno chiamate dentro una coroutine.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Dao, @Query, @Insert, @Update, @Delete)

package com.uniplanner.app.data

import androidx.room.Dao         // marca questa interfaccia come DAO (Data Access Object)
import androidx.room.Delete      // annotazione per eliminare un record
import androidx.room.Insert      // annotazione per inserire un record
import androidx.room.Query       // annotazione per query SQL personalizzate
import androidx.room.Update      // annotazione per aggiornare un record

@Dao
interface EsameDao {

    @Insert                      // inserisce un nuovo esame nella tabella
    suspend fun inserisci(esame: Esame)

    @Update                      // aggiorna un esame esistente (usa l'id per trovarlo)
    suspend fun aggiorna(esame: Esame)

    @Delete                      // elimina un esame dalla tabella
    suspend fun elimina(esame: Esame)

    @Query("SELECT * FROM esami ORDER BY data ASC")  // prende tutti gli esami ordinati per data
    suspend fun getTutti(): List<Esame>

    @Query("SELECT * FROM esami WHERE stato = 'superato'")  // solo gli esami superati (per calcolare la media)
    suspend fun getSuperati(): List<Esame>

    @Query("SELECT * FROM esami WHERE id = :id")  // cerca un esame specifico per id
    suspend fun getById(id: Int): Esame?

    @Query("SELECT * FROM esami WHERE stato = 'da sostenere' ORDER BY data ASC LIMIT 1")  // prossimo esame da sostenere (per la Home)
    suspend fun getProssimo(): Esame?
}