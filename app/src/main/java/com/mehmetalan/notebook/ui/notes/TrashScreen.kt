package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import com.mehmetalan.notebook.ui.AppViewModelProvider
import com.mehmetalan.notebook.ui.home.formatDate
import kotlinx.coroutines.launch

public var deleteConfirmationRequiredThree: MutableState<Boolean> = mutableStateOf(false)

object TrashScreenDestination : NavigationDestination {
    override val route = "trash_screen"
    override val titleRes = "Çöp Kutusu"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrashScreen(
    navigateToDetailPage: (Int) -> Unit,
    onBackButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrashScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val trashUiState by viewModel.trashUiState.collectAsState()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = TrashScreenDestination.titleRes,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackButtonPressed
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
    ) {innerPadding ->
        TrashBody(
            onItemClick = navigateToDetailPage,
            noteList = trashUiState.noteList,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            trashViewModel = viewModel
        )
    }
}

@Composable
private fun TrashBody(
    noteList: List<Note>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    trashViewModel: TrashScreenViewModel
) {
    val activeNotes = noteList.filter { it.isDeleted }
    val selectedNotes = noteList.filter { it.isSelect }
    val coroutineScope = rememberCoroutineScope()
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        if (activeNotes.isEmpty()) {
            Text(
                text = stringResource(R.string.trash_screen_empty_info),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            NoteList(
                noteList = activeNotes,
                onItemClick = { note ->
                    onItemClick(note.id)
                },
                onDeleteNotes = { notesToDelete ->
                    deleteConfirmationRequiredThree.value = true
                },
                onRecoveryNotes = { notesToRecovery ->
                    val notesToRecoveryIds = notesToRecovery.map { it.id }
                    trashViewModel.recoveryNotesMultiple(notesIds = notesToRecoveryIds)
                }
            )
        }

        if (deleteConfirmationRequiredThree.value) {
            DeleteConfirmationDialog(
                onDeleteConfirm = { notesToDelete ->
                    coroutineScope.launch {
                        trashViewModel.deleteNotesMultiple(noteList = notesToDelete)
                        deleteConfirmationRequiredThree.value = false
                    }
                },
                onDeleteCancel = { deleteConfirmationRequiredThree.value = false },
                noteList = selectedNotes
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteList(
    noteList: List<Note>,
    onItemClick: (Note) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteNotes: (List<Note>) -> Unit,
    onRecoveryNotes: (List<Note>) -> Unit
) {
    val activeNotes = noteList.filter { it.isDeleted }
    var selectedNotes by remember { mutableStateOf(setOf<Note>()) }
    var showCheckboxes by remember { mutableStateOf(false) }
    var allSelected by remember { mutableStateOf(false) }
    var showMenuSelection by remember { mutableStateOf(false) }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    fun toggleSelectAllNotes() {
        if (allSelected) {
            selectedNotes = emptySet()
        } else {
            selectedNotes = activeNotes.toSet()
        }
        allSelected = !allSelected
    }

    fun deleteSelectedNotes() {
        onDeleteNotes(selectedNotes.toList())
        selectedNotes = emptySet()
        allSelected = false
        showCheckboxes = false
    }

    fun recoverySelectedNotes() {
        onRecoveryNotes(selectedNotes.toList())
        selectedNotes = emptySet()
        allSelected = false
        showCheckboxes = false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        if (showCheckboxes) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 15.dp)
                ) {
                    Text(
                        text = selectedNotes.size.toString(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 23.sp
                    )
                    IconButton(
                        onClick = { showCheckboxes = false }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = ""
                        )
                    }
                }
                DropdownMenu(
                    offset = DpOffset(x = screenWidthDp - 10.dp, y = -50.dp),
                    expanded = showMenuSelection,
                    onDismissRequest = { showMenuSelection = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Kalıcı Olarak Sil",
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            deleteSelectedNotes()
                            showMenuSelection = false
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = ""
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Geri Yükle",
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            recoverySelectedNotes()
                            showMenuSelection = false
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Restore,
                                contentDescription = ""
                            )
                        }
                    )
                }
                IconButton(
                    onClick = {
                        showMenuSelection = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        items(items = activeNotes, key = { note -> note.id }) { note ->
            val isSelected = selectedNotes.contains(note)
            NoteItem(
                note = note,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .combinedClickable(
                        onLongClick = {
                            note.isSelect = true
                            showCheckboxes = true
                            selectedNotes = selectedNotes + note
                            allSelected = selectedNotes.size == activeNotes.size
                        },
                        onClick = {
                            if (showCheckboxes) {
                                selectedNotes = if (isSelected) {
                                    selectedNotes - note
                                } else {
                                    selectedNotes + note
                                }
                                note.isSelect = true
                                allSelected = selectedNotes.size == activeNotes.size
                            } else {
                                onItemClick(note)
                            }
                        }
                    ),
                isSelected = isSelected,
                onSelectionChange = { isChecked ->
                    if (isChecked) {
                        selectedNotes = selectedNotes + note
                    } else {
                        selectedNotes = selectedNotes - note
                    }
                    allSelected = selectedNotes.size == activeNotes.size
                },
                showCheckbox = showCheckboxes
            )
        }
    }
}


@Composable
private fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {},
    showCheckbox: Boolean = false
) {
    val iconTint = if (note.favorite) colorResource(R.color.orange) else Color.Transparent
    Column {
        OutlinedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            if (showCheckbox) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        onSelectionChange(it)
                        note.isSelect = true
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 3.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .size(15.dp)
                        .alpha(1f)
                )
            }
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = note.title.capitalize(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .widthIn(min = 0.dp, max = 250.dp),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier
                        .widthIn(min = 0.dp, max = 250.dp)
                        .padding(start = 3.dp, end = 3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${formatDate(date = note.createDate)}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: (List<Note>) -> Unit,
    onDeleteCancel: () -> Unit,
    noteList: List<Note>
) {
    AlertDialog(
        onDismissRequest = { onDeleteCancel() },
        title = {
            Text(text = "Notları Sil")
        },
        text = {
            Text("Seçili notları kalıcı olarak silmek istediğinize emin misiniz?")
        },
        confirmButton = {
            TextButton(onClick = { onDeleteConfirm(noteList) }) {
                Text("Sil")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDeleteCancel() }) {
                Text("İptal")
            }
        }
    )
}
