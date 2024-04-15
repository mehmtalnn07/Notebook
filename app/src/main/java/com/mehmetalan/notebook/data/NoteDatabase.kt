package com.mehmetalan.notebook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Database(entities = [Note::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, NoteDatabase::class.java, name = "note_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(it, 0, java.time.ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(java.time.ZoneOffset.UTC)
    }
}
