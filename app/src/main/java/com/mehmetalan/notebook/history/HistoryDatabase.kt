package com.mehmetalan.notebook.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: HistoryDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): HistoryDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}