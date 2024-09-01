package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object NoteAddScreenDestination: NavigationDestination {
    override val route = "note_add"
    override val titleRes = "Not Ekle"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteAddScreen(
    navigateBack: () -> Unit,
    viewModel: NoteAddScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val noteUiState = viewModel.noteUiState
    val context = LocalContext.current
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = NoteAddScreenDestination.titleRes,
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
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 20.dp, end = 8.dp),
                onClick = {
                    coroutineScope.launch {
                        Toast.makeText(context, "${noteUiState.noteDetails.title} isimli not eklendi.",Toast.LENGTH_LONG).show()
                        viewModel.saveNote()
                        navigateBack()
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Save Button",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {contentPadding ->
        NoteAddBody(
            noteUiState = noteUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier
                .padding(contentPadding)
                //.verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun NoteAddBody(
    noteUiState: NoteUiState,
    onItemValueChange: (NoteDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        NoteInputForm(
            noteDetails = noteUiState.noteDetails,
            onValueChange = onItemValueChange,
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun NoteInputForm(
    noteDetails: NoteDetails,
    modifier: Modifier = Modifier,
    onValueChange: (NoteDetails) -> Unit = {},
    enabled: Boolean = true,
) {
    Column (
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = noteDetails.title,
            onValueChange = { onValueChange(noteDetails.copy(title = it)) },
            placeholder = {
                Text(
                    text = stringResource(R.string.title_text),
                    color = MaterialTheme.colorScheme.primary
                )
                          },
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
            placeholder = {
                Text(
                    text = stringResource(R.string.content_text),
                    color = MaterialTheme.colorScheme.primary
                )
                          },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            enabled = enabled,
            singleLine = false,
        )
    }
}