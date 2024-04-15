package com.mehmetalan.notebook.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    suspend fun getAllSearchHistory(): List<History>

    @Insert
    suspend fun insertSearchHistory(history: History)

    @Query("DELETE FROM history")
    suspend fun deleteAllSearchHistory()
}