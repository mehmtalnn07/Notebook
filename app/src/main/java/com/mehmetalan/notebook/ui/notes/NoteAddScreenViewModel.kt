package com.mehmetalan.notebook.ui.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.data.NoteRepository
import java.time.LocalDateTime

class NoteAddScreenViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {


    var noteUiState by mutableStateOf(NoteUiState())
        private set

    fun updateUiState(noteDetails: NoteDetails) {
        noteUiState = NoteUiState(noteDetails = noteDetails, isAddValid = validateInput(noteDetails))
    }

    suspend fun saveNote() {
        if (validateInput()) {
            noteRepository.insertNote(noteUiState.noteDetails.toNote())
        }
    }

    private fun validateInput(uiState: NoteDetails = noteUiState.noteDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

}

data class NoteUiState(
    val noteDetails: NoteDetails = NoteDetails(),
    val isAddValid: Boolean = false,
    var isFavorite: Boolean = false
)

data class NoteDetails(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    var isFavorite: Boolean = false
)

fun NoteDetails.toNote(): Note = Note(
    id = id,
    title = title,
    content = content,
    createDate = timestamp,
    favorite = isFavorite,

)

fun Note.toNoteUiState(isAddValid: Boolean = false, isFavorite: Boolean = false): NoteUiState = NoteUiState(
    noteDetails = this.toNoteDetails(),
    isAddValid = isAddValid,
    isFavorite = isFavorite
)

fun Note.toNoteDetails(): NoteDetails = NoteDetails(
    id = id,
    title = title,
    content = content,
    isFavorite = favorite
)