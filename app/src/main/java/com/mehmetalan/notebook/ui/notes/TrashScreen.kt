package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.MoreVert
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
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


object TrashScreenDestination : NavigationDestination {
    override val route = "trash_screen"
    override val titleRes = "Çöp Kutusu"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrashScreen(
    navigateToDetailPage: (Int) -> Unit,
    onBackButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrashScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val trashUiState by viewModel.trashUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var topBarChanged by remember { mutableStateOf(false) }
    var allSelected by remember { mutableStateOf(false) }
    var selectedNotes by remember { mutableStateOf(setOf<Note>()) }
    val activeNotes = trashUiState.noteList.filter { it.isDeleted }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    var showDropDownMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var deleteDialog by remember { mutableStateOf(false) }


    fun toggleSelectAllNotes() {
        if (allSelected) {
            selectedNotes = emptySet()
        } else {
            selectedNotes = activeNotes.toSet()
        }
        allSelected = !allSelected
    }

    Scaffold (
        topBar = {
            if (topBarChanged) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.navigation_drawer_trash),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = allSelected,
                                onCheckedChange = { toggleSelectAllNotes() }
                            )
                            Text(
                                text = selectedNotes.size.toString(),
                                fontWeight = FontWeight.ExtraBold
                            )
                            IconButton(
                                onClick = {
                                    topBarChanged = false
                                    selectedNotes = emptySet()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Close Button"
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDropDownMenu = true }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "MoreVert Button"
                            )
                        }
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = TrashScreenDestination.titleRes,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackButtonPressed
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back Button",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        }
    ) {innerPadding ->
        if (activeNotes.isEmpty()) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = "Delete Button",
                    modifier = Modifier
                        .size(200.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(
                    modifier = Modifier
                        .size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.trash_screen_empty_info),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DropdownMenu(
                    offset = DpOffset(x = screenWidthDp - 0.dp, y = 50.dp),
                    expanded = showDropDownMenu,
                    onDismissRequest = { showDropDownMenu = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.delete_from_trash),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            showDropDownMenu = false
                            deleteDialog = true
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete Button",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.recovery),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            val notesToDeleteIds = selectedNotes.map { it.id }
                            viewModel.recoveryNotesMultiple(notesToDeleteIds)
                            topBarChanged = false
                            showDropDownMenu = false
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Restore,
                                contentDescription = "Recovery Button",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                }
                if (deleteDialog) {
                    DeleteConfirmationDialog(
                        onDeleteConfirm = {
                            viewModel.deleteNotesMultiple(noteList = selectedNotes.toList())
                            selectedNotes = emptySet()
                            topBarChanged = false
                            deleteDialog = false
                            scope.launch {
                                val result = snackBarHostState
                                    .showSnackbar(
                                        message = "Not Silindi",
                                    )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                    }
                                    SnackbarResult.Dismissed -> {
                                    }
                                }
                            }
                        },
                        onDeleteCancel = {
                            deleteDialog = false
                        },
                        noteList = selectedNotes.toList()
                    )
                }
                LazyVerticalGrid (
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
                                        selectedNotes = selectedNotes + note
                                        allSelected = selectedNotes.size == activeNotes.size
                                        topBarChanged = true
                                    },
                                    onClick = {
                                        if (topBarChanged) {
                                            selectedNotes = if (isSelected) {
                                                note.isSelect = false
                                                selectedNotes - note
                                            } else {
                                                note.isSelect = true
                                                selectedNotes + note

                                            }
                                            note.isSelect = true
                                            allSelected = selectedNotes.size == activeNotes.size
                                        } else {
                                            navigateToDetailPage(note.id)
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
                            showCheckbox = topBarChanged
                        )
                    }
                }
            }
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
        OutlinedCard (
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .border(
                    width = if (isSelected) 5.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            if (showCheckbox) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Row (
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
            Column (
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
                Row (
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
            Text(
                text = stringResource(id = R.string.delete_from_trash),
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.trash_dialog_message),
                color = MaterialTheme.colorScheme.primary
            )
        },
        confirmButton = {
            TextButton(onClick = { onDeleteConfirm(noteList) }) {
                Text(
                    text = stringResource(id = R.string.alert_dialog_confirm_button),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDeleteCancel() }) {
                Text(
                    text = stringResource(id = R.string.alert_dialog_dismiss_button),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
