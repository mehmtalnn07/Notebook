package com.mehmetalan.notebook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    var isSelect: Boolean = false,
    val createDate: LocalDateTime,
    var favorite: Boolean,
    var isDeleted: Boolean = false,
    val title: String,
    val content: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
