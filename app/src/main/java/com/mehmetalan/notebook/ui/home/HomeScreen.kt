package com.mehmetalan.notebook.ui.home


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mehmetalan.notebook.data.Note
import com.mehmetalan.notebook.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.history.History
import com.mehmetalan.notebook.history.HistoryDatabase
import com.mehmetalan.notebook.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import java.util.Locale
import java.time.LocalDateTime

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = "Not Defteri"
    const val noteIdArg = "noteId"
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun HomeScreen(
    openToDrawer: () -> Unit,
    navigateToAddPage: () -> Unit,
    navigateToDetailPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold (
        topBar = {
                 CenterAlignedTopAppBar(
                     navigationIcon = {
                                      IconButton(
                                          onClick = openToDrawer
                                      ) {
                                          Icon(
                                              imageVector = Icons.Filled.Menu,
                                              contentDescription = stringResource(R.string.open_to_drawer),
                                              tint = MaterialTheme.colorScheme.primary
                                          )
                                      }
                     },
                     title = {
                         Text(
                             text = HomeDestination.titleRes,
                             color = MaterialTheme.colorScheme.primary
                         )
                     },
                 )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddPage,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    ) {innerPadding ->
        HomeBody(
            noteList = homeUiState.noteList,
            onItemClick = navigateToDetailPage,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBody(
    noteList: List<Note>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val activeNotes = noteList.filter { !it.isDeleted }
    val historyDao = remember { HistoryDatabase.getDatabase(context).historyDao() }
    var history by remember {
        mutableStateOf<List<History>>(emptyList())
    }
    LaunchedEffect(Unit) {
        history = historyDao.getAllSearchHistory()
    }
    @Composable
    fun performSearch() {
        val searchText = query.trim().toLowerCase(Locale.getDefault())
        // Arama sorgusuna göre notları filtrele
        val filteredNotes = if (searchText.isEmpty()) {
            null
        } else {
            activeNotes.filter { note ->
                note.title.toLowerCase(Locale.getDefault()).contains(searchText)
                        || note.content.toLowerCase(Locale.getDefault()).contains(searchText)
            }
        }
        if (filteredNotes != null) {
            NoteList(
                noteList = filteredNotes,
                onItemClick = { note ->
                    onItemClick(note.id)
                }
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!text.isNullOrEmpty()) {
                query = text[0]
            }
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SearchBar(
            modifier = Modifier.clip(shape = RoundedCornerShape(20.dp)),
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery },
            onSearch = { newQuery ->
                query = newQuery
                // Arama sorgusunu geçmişe ekle
                coroutineScope.launch {
                    historyDao.insertSearchHistory(History(query = newQuery))
                    history = historyDao.getAllSearchHistory()
                }
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                Row {
                    if (active) {
                        IconButton(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                intent.putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice Search")
                                try {
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Your device does not support speech recognition",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = stringResource(R.string.microphone_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (active) {
                        IconButton(
                            onClick = {
                                // Tüm arama geçmişini bellekten kaldır
                                query = ""
                                active = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        query = ""
                    }
                }
            }
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = if (history.isEmpty()) stringResource(R.string.empty_history) else stringResource(R.string.history_title),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                        )
                        TextButton(
                            onClick = {
                                // Tüm arama geçmişini sil
                                coroutineScope.launch {
                                    historyDao.deleteAllSearchHistory()
                                    history = emptyList() // Veritabanı güncellendiğinde liste güncellenir
                                }
                                query = ""
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.history_delete_text),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                val uniqueQueries = history.map { it.query }.toSet()
                if (query.isEmpty()) {
                    LazyColumn (
                        //modifier = Modifier.heightIn(min = 0.dp , max = 100.dp)
                    ) {
                        itemsIndexed(uniqueQueries.toList()) { index, newquery ->
                            Text(
                                text = if (query.isEmpty()) newquery else "",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable { query = newquery },
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                performSearch()
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        if (activeNotes.isEmpty()) {
            Text(
                text = stringResource(R.string.home_screen_empty_info),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            NoteList(
                noteList = activeNotes,
                onItemClick = { note ->
                    onItemClick(note.id)
                }
            )
        }
    }
}

@Composable
private fun NoteList(
    noteList: List<Note>,
    onItemClick: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sortingOption by remember { mutableStateOf(SortingOption.TITLE) }
    val activeNotes = noteList.filter { !it.isDeleted }
    var showMenu by remember { mutableStateOf(false) }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    var gender by remember {
        mutableStateOf("Sırala")
    }
    var isDescending by remember {
        mutableStateOf(false)
    }
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownMenu(
            offset = DpOffset(x = screenWidthDp - 10.dp, y = -50.dp),
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            DropdownMenuItem(
                text = {
                       Text(
                           text = stringResource(R.string.sort_drop_down_menu_item_title),
                           color = MaterialTheme.colorScheme.primary
                       )
                },
                onClick = {
                    sortingOption = SortingOption.TITLE
                    gender = "Başlık"
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = {
                       Text(
                           text = stringResource(R.string.sort_drop_down_menu_item_created_date),
                           color = MaterialTheme.colorScheme.primary
                       )
                },
                onClick = {
                    sortingOption = SortingOption.CREATE_DATE
                    gender = "Oluşturulma Tarihi"
                    showMenu = false
                }
            )
        }
        Text(
            text = gender,
            color = MaterialTheme.colorScheme.primary
        )
        if (gender == "Sırala") {
            null
        } else {
            IconButton(
                onClick = { isDescending = !isDescending }
            ) {
                Icon(
                    imageVector = if (isDescending) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(
            onClick = { showMenu = true }
        ) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    val sortedNotes = when {
        sortingOption == SortingOption.TITLE && isDescending ->
            activeNotes.sortedByDescending { it.title }
        sortingOption == SortingOption.CREATE_DATE && isDescending ->
            activeNotes.sortedBy { it.createDate }
        sortingOption == SortingOption.TITLE && !isDescending ->
            activeNotes.sortedBy { it.title }
        else ->
            activeNotes.sortedByDescending { it.createDate }
    }
    var isCheck by remember { mutableStateOf(false) }
    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        items(items = sortedNotes, key = { note -> note.id }) { note ->
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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
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
fun formatDate(date: LocalDateTime): String {
    val dayOfMonth = date.dayOfMonth
    val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
    val month = date.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
    return "$dayOfMonth $month $dayOfWeek"
}

enum class SortingOption {
    CREATE_DATE,
    TITLE
}


