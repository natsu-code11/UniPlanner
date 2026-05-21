// FILE: LezioneDao.kt
// POSIZIONE: app/src/main/java/com/uniplanner/app/data/
// SCOPO: Definisce le operazioni sul database per la tabella "lezioni".
//        Le lezioni vengono caricate dal JSON al primo avvio,
//        poi l'utente può aggiungerne o eliminarne manualmente.
// LEZIONE DI RIFERIMENTO: L15 (Room - @Dao, @Query, @Insert, @Delete)

package com.uniplanner.app.data

import androidx.room.Dao         // marca questa interfaccia come DAO
import androidx.room.Delete      // annotazione per eliminare un record
import androidx.room.Insert      // annotazione per inserire un record
import androidx.room.Query       // annotazione per query SQL personalizzate

@Dao
interface LezioneDao {

    @Insert                      // inserisce una lezione nella tabella
    suspend fun inserisci(lezione: Lezione)

    @Delete                      // elimina una lezione dalla tabella
    suspend fun elimina(lezione: Lezione)

    @Query("SELECT * FROM lezioni ORDER BY giorno ASC, ora ASC")  // tutte le lezioni ordinate per giorno e ora
    suspend fun getTutte(): List<Lezione>

    @Query("SELECT * FROM lezioni WHERE giorno = :giorno ORDER BY ora ASC")  // lezioni di un giorno specifico, es. "Lunedì"
    suspend fun getPerGiorno(giorno: String): List<Lezione>

    @Query("SELECT * FROM lezioni ORDER BY ora ASC LIMIT 1")  // prossima lezione (per la Home)
    suspend fun getProssima(): Lezione?

    @Query("SELECT COUNT(*) FROM lezioni")  // conta quante lezioni ci sono (utile per sapere se il JSON è già stato caricato)
    suspend fun contaTutte(): Int
}