package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

public var deleteConfirmationRequiredTwo: MutableState<Boolean> = mutableStateOf(false)

object DeleteNoteDetailsScreenDestination : NavigationDestination {
    override val route = "deleted_note_details"
    override val titleRes = "Not Detay"
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeletedNoteDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: DeleteNoteDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = DeleteNoteDetailsScreenDestination.titleRes,
                        color = MaterialTheme.colorScheme.primary
                    )
                        },
                navigationIcon = {
                                 IconButton(
                                     onClick = { navigateBack() }
                                 ) {
                                     Icon(
                                         imageVector = Icons.Filled.ArrowBack,
                                         contentDescription = stringResource(R.string.back_button),
                                         tint = MaterialTheme.colorScheme.primary
                                     )
                                 }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                Toast.makeText(context, "${uiState.value.noteDetails.title} isimli notu geri yüklediniz.",Toast.LENGTH_LONG).show()
                                viewModel.moveToList()
                                navigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestoreFromTrash,
                            contentDescription = stringResource(R.string.upload_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 20.dp, end = 8.dp),
                onClick = { deleteConfirmationRequiredTwo.value = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = Color.Transparent
    ) {innerPadding ->
        DeletedNoteDetailsBody(
            deletedNoteDetailsUiState = uiState.value,
            onDelete = {
                coroutineScope.launch {
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun DeletedNoteDetailsBody(
    deletedNoteDetailsUiState: DeleteNoteDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeleteNoteDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Column (
        modifier = modifier.padding(16.dp)
    ) {
        NoteDetails(
            note = deletedNoteDetailsUiState.noteDetails.toNote(),
        )
        if (deleteConfirmationRequiredTwo.value) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteNote()
                    }
                    deleteConfirmationRequiredTwo.value = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequiredTwo.value = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun NoteDetails(
    note: Note,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 5.dp)
                .height(600.dp)
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {  },
        title = {
            Text(
                text = stringResource(R.string.alert_dialog_title_trash),
                color = MaterialTheme.colorScheme.primary
            )
                },
        text = {
            Text(
                text = stringResource(R.string.alert_dialog_message_trash),
                color = MaterialTheme.colorScheme.primary
            )
               },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDeleteCancel
            ) {
                Text(
                    text = stringResource(R.string.alert_dialog_dismiss_button),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDeleteConfirm
            ) {
                Text(
                    text = stringResource(R.string.alert_dialog_confirm_button),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
