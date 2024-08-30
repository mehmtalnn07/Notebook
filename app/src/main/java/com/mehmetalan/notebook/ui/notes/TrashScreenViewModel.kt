package com.mehmetalan.notebook.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrashScreenViewModel(
    private val noteRepository: NoteRepository
): ViewModel() {
    val trashUiState: StateFlow<TrashUiState> =
        noteRepository.getAllNotesStream().map { TrashUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TrashUiState()
            )
    fun deleteNotesMultiple(noteList: List<Note>) {
        viewModelScope.launch {
            noteRepository.deleteNotesMultiple(noteList)
        }
    }
    fun recoveryNotesMultiple(notesIds: List<Int>) {
        viewModelScope.launch {
            noteRepository.moveToListMultiple(notesIds)
        }
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TrashUiState(
    val noteList: List<Note> = listOf()
)