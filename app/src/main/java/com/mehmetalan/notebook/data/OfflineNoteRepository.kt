package com.mehmetalan.notebook.data

import kotlinx.coroutines.flow.Flow

class OfflineNoteRepository(private val noteDao: NoteDao): NoteRepository {

    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun getNoteStream(id: Int): Flow<Note?> = noteDao.getNote(id)

    override suspend fun insertNote(note: Note) = noteDao.insert(note)

    override suspend fun deleteNote(note: Note) = noteDao.delete(note)

    override suspend fun updateNote(note: Note) = noteDao.update(note)

    override suspend fun moveToTrash(id: Int) = noteDao.moveToTrash(id)

    override suspend fun moveToList(id: Int) = noteDao.moveToList(id)

    override suspend fun moveToFavorite(id: Int) = noteDao.moveToFavorite(id)

    override suspend fun moveToNotFavorite(id: Int) = noteDao.moveToNotFavorite(id)

    override suspend fun moveToFavoriteMultiple(ids: List<Int>) = noteDao.moveToFavoriteMultiple(ids)

    override suspend fun deleteFromFavorites(ids: List<Int>) = noteDao.deleteFromFavorites(ids)

    override suspend fun moveToTrashMultiple(ids: List<Int>) = noteDao.moveToTrashMultiple(ids)

    override suspend fun moveToListMultiple(ids: List<Int>) = noteDao.moveToListMultiple(ids)

    override suspend fun deleteNotesMultiple(noteList: List<Note>) = noteDao.deleteNotesMultiple(noteList)


}
