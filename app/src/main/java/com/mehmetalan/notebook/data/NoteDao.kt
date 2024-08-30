package com.mehmetalan.notebook.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNote(id: Int): Flow<Note>

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :id")
    suspend fun moveToTrash(id: Int)

    @Query("UPDATE notes SET isDeleted = 0 WHERE id = :id")
    suspend fun moveToList(id: Int)

    @Query("UPDATE notes SET favorite = 1 WHERE id = :id")
    suspend fun moveToFavorite(id: Int)

    @Query("UPDATE notes SET favorite = 0 WHERE id = :id")
    suspend fun moveToNotFavorite(id: Int)

    @Query("UPDATE notes SET favorite = 1 WHERE id IN (:ids)")
    suspend fun moveToFavoriteMultiple(ids: List<Int>)

    @Query("UPDATE notes SET favorite = 0 WHERE id IN (:ids)")
    suspend fun deleteFromFavorites(ids: List<Int>)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id IN (:ids)")
    suspend fun moveToTrashMultiple(ids: List<Int>)

    @Query("UPDATE notes SET isDeleted = 0 WHERE id IN (:ids)")
    suspend fun moveToListMultiple(ids: List<Int>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Delete
    suspend fun deleteNotesMultiple(noteList: List<Note>)

    @Update
    suspend fun update(note: Note)
}
