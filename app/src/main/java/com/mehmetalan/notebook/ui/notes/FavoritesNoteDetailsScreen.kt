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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object FavoritesNoteDetailsScreenDestination : NavigationDestination {
    override val route = "favorites_note_detail"
    override val titleRes = "Not Detay"
    const val noteIdArg = "noteId"
    val routeWithArgs = "${route}/{$noteIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteNoteDetailsScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesNoteDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
                        text = FavoritesNoteDetailsScreenDestination.titleRes,
                        color = MaterialTheme.colorScheme.primary
                    )
                        },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
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
                                    viewModel.moveToNotFavoriteTwo()
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
                            contentDescription = "Favorite Button",
                            tint = if (isFavorite) colorResource(R.color.orange) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 20.dp, end = 8.dp),
                onClick = {
                    coroutineScope.launch {
                        viewModel.updateNote()
                        onBackPressed()
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
    ) {innerPadding ->
        FavoriteNoteDetailsBody(
            noteDetailsUiState = viewModel.noteUiState,
            onItemValueChange = viewModel::updateUiState,
            favoritesNoteDetailsUiState = uiState.value,
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun FavoriteNoteDetailsBody(
    favoritesNoteDetailsUiState: FavoriteNoteDetailsUiState,
    onItemValueChange: (NoteDetails) -> Unit,
    noteDetailsUiState: NoteUiState,
    modifier: Modifier = Modifier,
) {
    val note = favoritesNoteDetailsUiState.noteDetails.toNote()
    Column (
        modifier = modifier.padding(16.dp)
    ) {
        NoteDetailsTwo(
            note = favoritesNoteDetailsUiState.noteDetails.toNote(),
            noteDetails = noteDetailsUiState.noteDetails,
            onValueChange = onItemValueChange,
        )
    }
}

@Composable
fun NoteDetailsTwo(
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
            singleLine = true,
            textStyle = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            )
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