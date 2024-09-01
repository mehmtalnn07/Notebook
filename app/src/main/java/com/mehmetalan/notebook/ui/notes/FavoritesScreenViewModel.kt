package com.mehmetalan.notebook.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesScreenViewModel(
    noteRepository1: SavedStateHandle,
    private val noteRepository: NoteRepository
): ViewModel() {
    val favoriteUiState: StateFlow<FavoriteNoteUiState> =
        noteRepository.getAllNotesStream().map {
            FavoriteNoteUiState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FavoriteNoteUiState()
            )

    fun deleteNotes(noteIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.moveToTrashMultiple(noteIds)
        }
    }

    fun deleteFromFavorites(noteIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.deleteFromFavorites(noteIds)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class FavoriteNoteUiState(
    val noteList: List<Note> = listOf()
)