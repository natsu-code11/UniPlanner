// FILE: LezioneDao.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/

package com.uniplanner.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LezioneDao {

    @Insert
    suspend fun inserisci(lezione: Lezione)

    @Insert
    suspend fun inserisciTutte(lezioni: List<Lezione>)

    @Update
    suspend fun aggiorna(lezione: Lezione)  // aggiorna una lezione esistente

    @Delete
    suspend fun elimina(lezione: Lezione)

    @Query("SELECT * FROM lezioni ORDER BY giorno ASC, ora ASC")
    suspend fun getTutte(): List<Lezione>

    @Query("SELECT * FROM lezioni WHERE giorno = :giorno ORDER BY ora ASC")
    suspend fun getPerGiorno(giorno: String): List<Lezione>

    @Query("SELECT * FROM lezioni WHERE id = :id")
    suspend fun getById(id: Int): Lezione?  // cerca una lezione per id — usato per la modifica

    @Query("SELECT * FROM lezioni ORDER BY ora ASC LIMIT 1")
    suspend fun getProssima(): Lezione?

    @Query("SELECT COUNT(*) FROM lezioni")
    suspend fun contaTutte(): Int
}