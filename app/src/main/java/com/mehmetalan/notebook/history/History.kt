package com.mehmetalan.notebook.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History(
    val query: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
