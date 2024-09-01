package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import com.mehmetalan.notebook.data.Note
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

public var deleteConfirmationRequired: MutableState<Boolean> = mutableStateOf(false)

object NoteDetailsDestination : NavigationDestination {
    override val route = "note_details"
    override val titleRes = "Not Detay"
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPad dingParameter", "StateFlowValueCalledInComposition")
@Composable
fun NoteDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isFavorite = uiState.value.isFavorite

    val context = LocalContext.current

    Scaffold (
        topBar = { 
                 CenterAlignedTopAppBar(
                     title = {
                         Text(
                             text = NoteDetailsDestination.titleRes,
                             color = MaterialTheme.colorScheme.primary,
                             fontWeight = FontWeight.ExtraBold
                         )
                             },
                     navigationIcon = {
                         IconButton(
                             onClick = navigateBack
                         ) {
                             Icon(
                                 imageVector = Icons.Default.ArrowBackIosNew,
                                 contentDescription = "Back Button",
                                 tint = MaterialTheme.colorScheme.primary
                             )
                         }
                     },
                     actions = {
                         ToggleButton(
                             colors = ToggleButtonDefaults.toggleButtonColors(
                                 checkedBackgroundColor = Color.Transparent,
                                 uncheckedBackgroundColor =  Color.Transparent
                             ),
                             checked = isFavorite,
                             onCheckedChange = {
                                 if (isFavorite) {
                                     coroutineScope.launch {
                                         Toast.makeText(context,"${uiState.value.noteDetails.title} isimli not favori listesinden çıkarıldı.", Toast.LENGTH_LONG).show()
                                         viewModel.moveToNotFavorite()
                                     }
                                 } else {
                                     coroutineScope.launch {
                                         Toast.makeText(context,"${uiState.value.noteDetails.title} isimli not favori listesine eklendi", Toast.LENGTH_LONG).show()
                                         viewModel.moveToFavorite()
                                     }
                                 }
                             }
                         ) {
                             Icon(
                                 imageVector = Icons.Filled.Star,
                                 contentDescription = "Favorite Icon",
                                 tint = if (isFavorite) colorResource(R.color.orange) else MaterialTheme.colorScheme.primary
                             )
                         }
                         IconButton(
                             onClick = {
                                 deleteConfirmationRequired.value = true
                             }
                         ) {
                             Icon(
                                 imageVector = Icons.Filled.Delete,
                                 contentDescription = "Delete Icon",
                                 tint = MaterialTheme.colorScheme.primary
                             )
                         }
                     },
                 )
        },
        floatingActionButton = {
           FloatingActionButton(
               modifier = Modifier.padding(bottom = 20.dp, end = 8.dp),
               onClick = {
                   coroutineScope.launch {
                       viewModel.updateNote()
                       navigateBack()
                   }
               }
           ) {
               Icon(
                   imageVector = Icons.Default.Save,
                   contentDescription = "Save Button",
                   tint = MaterialTheme.colorScheme.primary
               )
           }
        },
        containerColor = Color.Transparent
    ) {innerPadding ->
        NoteDetailsBody(
            noteUiState = viewModel.noteUiState,
            onItemValueChange = viewModel::updateUiState,
            noteDetailsUiState = uiState.value,
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            onDelete = {
                coroutineScope.launch {
                    navigateBack()
                }
            }
        )
    }
}

@Composable
private fun NoteDetailsBody(
    noteUiState: NoteUiState,
    onItemValueChange: (NoteDetails) -> Unit,
    noteDetailsUiState: NoteDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Column (
        modifier = modifier.padding(16.dp)
    ) {
        NoteDetails(
            note = noteDetailsUiState.noteDetails.toNote(),
            noteDetails = noteUiState.noteDetails,
            onValueChange = onItemValueChange,
        )

        if (deleteConfirmationRequired.value) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    coroutineScope.launch {
                        Toast.makeText(context,"${viewModel.uiState.value.noteDetails.title} isimli notu çöp kutusuna taşıdınız.", Toast.LENGTH_LONG).show()
                        viewModel.moveToTrash()
                    }
                    deleteConfirmationRequired.value = false
                    onDelete()
                },
                onDeleteCancel = {
                    Toast.makeText(context, "Silme işlemi iptal edildi.",Toast.LENGTH_LONG).show()
                    deleteConfirmationRequired.value = false
                                 },
                modifier = Modifier.padding(16.dp)
            )
        }
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
                text = stringResource(R.string.alert_dialog_title_detail),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
                },
        text = {
            Text(
                text = stringResource(R.string.alert_dialog_message_detail),
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

@Composable
fun NoteDetails(
    noteDetails: NoteDetails,
    onValueChange: (NoteDetails) -> Unit = {},
    note: Note,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        OutlinedTextField(
            value = noteDetails.title,
            onValueChange = { onValueChange(noteDetails.copy(title = it)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = noteDetails.content,
            onValueChange = { onValueChange(noteDetails.copy(content = it)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
    }
}