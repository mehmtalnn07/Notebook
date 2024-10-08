package com.mehmetalan.notebook.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotesStream(): Flow<List<Note>>

    fun getNoteStream(id: Int): Flow<Note?>

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun moveToTrash(id: Int)

    suspend fun moveToList(id: Int)

    suspend fun moveToFavorite(id: Int)

    suspend fun moveToNotFavorite(id: Int)

    suspend fun moveToFavoriteMultiple(ids: List<Int>)

    suspend fun deleteFromFavorites(ids: List<Int>)

    suspend fun moveToTrashMultiple(ids: List<Int>)

    suspend fun moveToListMultiple(ids: List<Int>)

    suspend fun deleteNotesMultiple(noteList: List<Note>)

}
