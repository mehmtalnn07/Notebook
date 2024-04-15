package com.mehmetalan.notebook.ui

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mehmetalan.notebook.NoteApplication
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.mehmetalan.notebook.ui.home.HomeViewModel
import com.mehmetalan.notebook.ui.notes.DeleteNoteDetailsScreenViewModel
import com.mehmetalan.notebook.ui.notes.FavoritesNoteDetailsViewModel
import com.mehmetalan.notebook.ui.notes.FavoritesScreenViewModel
import com.mehmetalan.notebook.ui.notes.NoteAddScreenViewModel
import com.mehmetalan.notebook.ui.notes.NoteDetailsViewModel
import com.mehmetalan.notebook.ui.notes.TrashScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                noteApplication().container.noteRepository)
        }
        initializer {
            TrashScreenViewModel(noteApplication().container.noteRepository)
        }
        initializer {
            FavoritesScreenViewModel(
                this.createSavedStateHandle(),
                noteApplication().container.noteRepository
            )
        }
        initializer {
            FavoritesNoteDetailsViewModel(
                this.createSavedStateHandle(),
                noteApplication().container.noteRepository
            )
        }
        initializer {
            NoteAddScreenViewModel(
                noteApplication().container.noteRepository
            )
        }
        initializer {
            NoteDetailsViewModel(
                this.createSavedStateHandle(),
                noteApplication().container.noteRepository
            )
        }
        initializer {
            DeleteNoteDetailsScreenViewModel(
                this.createSavedStateHandle(),
                noteApplication().container.noteRepository
            )
        }
    }
}

fun CreationExtras.noteApplication(): NoteApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as NoteApplication)