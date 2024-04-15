package com.mehmetalan.notebook.ui.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalan.notebook.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeleteNoteDetailsScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
): ViewModel() {

    private val noteId: Int = checkNotNull(savedStateHandle[DeleteNoteDetailsScreenDestination.noteIdArg])

    var noteUiState by mutableStateOf(NoteUiState())
        private set

    val uiState: StateFlow<DeleteNoteDetailsUiState> =
        noteRepository.getNoteStream(noteId)
            .filterNotNull()
            .map {
                DeleteNoteDetailsUiState(noteDetails = it.toNoteDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeleteNoteDetailsUiState()
            )

    init {
        viewModelScope.launch {
            noteUiState = noteRepository.getNoteStream(noteId)
                .filterNotNull()
                .first()
                .toNoteUiState(true)
        }
    }

    suspend fun updateNote() {
        if (validateInput(noteUiState.noteDetails)) {
            noteRepository.updateNote(noteUiState.noteDetails.toNote())
        }
    }

    private fun validateInput(uiState: NoteDetails = noteUiState.noteDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

    suspend fun deleteNote() {
        noteRepository.deleteNote(uiState.value.noteDetails.toNote())
    }

    suspend fun moveToList() {
        noteRepository.moveToList(uiState.value.noteDetails.id)
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class DeleteNoteDetailsUiState(
    val noteDetails: NoteDetails = NoteDetails()
)