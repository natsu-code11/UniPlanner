package com.uniplanner.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Esame::class, Lezione::class, Scadenza::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun esameDao(): EsameDao
    abstract fun lezioneDao(): LezioneDao
    abstract fun scadenzaDao(): ScadenzaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uniplanner_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}