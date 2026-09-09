// ============================================================
// FILE: LezioneDao.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Operazioni sul database per le lezioni.
//        Aggiunto ordinamento per ora.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Dao, @Query)
// ============================================================

package com.uniplanner.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LezioneDao {

    @Insert
    suspend fun inserisci(lezione: Lezione)

    @Insert
    suspend fun inserisciTutte(lezioni: List<Lezione>)  // inserisce lista intera — utile per import PDF

    @Delete
    suspend fun elimina(lezione: Lezione)

    @Query("SELECT * FROM lezioni ORDER BY giorno ASC, ora ASC")
    suspend fun getTutte(): List<Lezione>

    // lezioni di un giorno specifico ordinate per ora crescente
    @Query("SELECT * FROM lezioni WHERE giorno = :giorno ORDER BY ora ASC")
    suspend fun getPerGiorno(giorno: String): List<Lezione>

    @Query("SELECT * FROM lezioni ORDER BY ora ASC LIMIT 1")
    suspend fun getProssima(): Lezione?

    @Query("SELECT COUNT(*) FROM lezioni")
    suspend fun contaTutte(): Int
}