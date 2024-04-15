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

class FavoritesNoteDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
): ViewModel() {
    private val noteId: Int = checkNotNull(savedStateHandle[FavoritesNoteDetailsScreenDestination.noteIdArg])

    var noteUiState by mutableStateOf(NoteUiState())
        private set

    val uiState: StateFlow<FavoriteNoteDetailsUiState> =
        noteRepository.getNoteStream(noteId)
            .filterNotNull()
            .map {
                FavoriteNoteDetailsUiState(noteDetails = it.toNoteDetails(), isFavorite = it.favorite)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FavoriteNoteDetailsUiState()
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

    suspend fun moveToNotFavoriteTwo() {
        noteRepository.moveToNotFavorite(uiState.value.noteDetails.id)
        uiState.value.isFavorite = false
        noteUiState.isFavorite = false
    }

    suspend fun moveToFavorite() {
        noteRepository.moveToFavorite(uiState.value.noteDetails.id)
        uiState.value.isFavorite = true
        noteUiState.isFavorite = true
    }

    fun updateUiState(noteDetails: NoteDetails) {
        noteUiState =
            NoteUiState(noteDetails = noteDetails, isAddValid = validateInput(noteDetails))
    }

    private fun validateInput(uiState: NoteDetails = noteUiState.noteDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

    suspend fun moveToNotFavorite() {
        noteRepository.moveToNotFavorite(uiState.value.noteDetails.id)
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class FavoriteNoteDetailsUiState(
    val noteDetails: NoteDetails = NoteDetails(),
    var isFavorite: Boolean = false
)