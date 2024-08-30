package com.mehmetalan.notebook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> =
        noteRepository.getAllNotesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    fun moveToFavoriteMultiple(noteIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.moveToFavoriteMultiple(noteIds)
        }
    }

    fun deleteFromFavorites(noteIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.deleteFromFavorites(noteIds)
        }
    }

    fun deleteNotes(noteIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.moveToTrashMultiple(noteIds)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(
    val noteList: List<Note> = listOf()
)
