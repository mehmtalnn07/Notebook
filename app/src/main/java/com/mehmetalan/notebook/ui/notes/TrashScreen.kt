package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import com.mehmetalan.notebook.ui.AppViewModelProvider
import com.mehmetalan.notebook.ui.home.formatDate

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
                .fillMaxSize()
        )
    }
}

@Composable
private fun TrashBody(
    noteList: List<Note>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeNotes = noteList.filter { it.isDeleted }
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
            NoteList(noteList = activeNotes, onItemClick = { note -> onItemClick(note.id) })
        }
    }
}

@Composable
private fun NoteList(
    noteList: List<Note>,
    onItemClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeNotes = noteList.filter { it.isDeleted }
    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        items(items = activeNotes, key = { note -> note.id }) { note ->
            NoteItem(
                note = note,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onItemClick(note) }
            )
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
) {

    val iconTint = if (note.favorite) colorResource(R.color.orange) else Color.Transparent
    Column {
        OutlinedCard (
            modifier = modifier.padding(start = 10.dp, end = 10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 3.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.favorite_button),
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
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}