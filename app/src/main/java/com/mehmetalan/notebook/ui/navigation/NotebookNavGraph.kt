package com.mehmetalan.notebook.ui.navigation


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mehmetalan.notebook.R
import com.mehmetalan.notebook.ui.AppViewModelProvider
import com.mehmetalan.notebook.ui.home.HomeDestination
import com.mehmetalan.notebook.ui.home.HomeScreen
import com.mehmetalan.notebook.ui.home.HomeViewModel
import com.mehmetalan.notebook.ui.notes.AboutScreen
import com.mehmetalan.notebook.ui.notes.DeleteNoteDetailsScreenDestination
import com.mehmetalan.notebook.ui.notes.DeletedNoteDetailsScreen
import com.mehmetalan.notebook.ui.notes.FavoriteNoteDetailsScreen
import com.mehmetalan.notebook.ui.notes.FavoritesNoteDetailsScreenDestination
import com.mehmetalan.notebook.ui.notes.FavoritesScreen
import com.mehmetalan.notebook.ui.notes.FavoritesScreenDestination
import com.mehmetalan.notebook.ui.notes.FavoritesScreenViewModel
import com.mehmetalan.notebook.ui.notes.NoteAddScreen
import com.mehmetalan.notebook.ui.notes.NoteAddScreenDestination
import com.mehmetalan.notebook.ui.notes.NoteDetailsDestination
import com.mehmetalan.notebook.ui.notes.NoteDetailsScreen
import com.mehmetalan.notebook.ui.notes.TrashScreen
import com.mehmetalan.notebook.ui.notes.TrashScreenDestination
import com.mehmetalan.notebook.ui.notes.TrashScreenViewModel

import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NotebookNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val trashViewModel: TrashScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val trashUiState by trashViewModel.trashUiState.collectAsState()
    val trashList = trashUiState.noteList.filter { it.isDeleted }

    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val noteList = homeUiState.noteList.filter { !it.isDeleted }

    val favoriteViewModel: FavoritesScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val favoriteUiState by favoriteViewModel.favoriteUiState.collectAsState()
    val favoriteList = favoriteUiState.noteList.filter { it.favorite }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.note),
                        contentDescription = stringResource(R.string.designer_icon),
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                Divider()
                NavigationDrawerItem(
                    modifier = Modifier.padding(top = 10.dp),
                    label = { 
                        Text(
                            text = stringResource(R.string.navigation_drawer_all_notes),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ) 
                            },
                    selected = false,
                    onClick = { 
                        navController.navigate(HomeDestination.route)
                        coroutineScope.launch {
                            drawerState.close()
                        }
                              },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.navigation_drawer_all_notes),
                            tint = MaterialTheme.colorScheme.primary,
                        ) 
                    },
                    badge = {
                        Text(
                            text = noteList.size.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                )
                NavigationDrawerItem(
                    modifier = Modifier.padding(top = 10.dp),
                    label = { 
                        Text(
                            text = stringResource(R.string.navigation_drawer_trash),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ) 
                            },
                    selected = false,
                    onClick = {
                        navController.navigate(route = TrashScreenDestination.route)
                        coroutineScope.launch {
                            drawerState.close()
                        }
                              },
                    icon = { 
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.navigation_drawer_trash),
                            tint = MaterialTheme.colorScheme.primary
                        ) 
                           },
                    badge = { 
                        Text(
                            text = trashList.size.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ) 
                    }
                )
                NavigationDrawerItem(
                    modifier = Modifier.padding(top = 10.dp),
                    label = {
                            Text(
                                text = stringResource(R.string.navigation_drawer_favorites),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                    },
                    selected = false,
                    onClick = {
                        navController.navigate(FavoritesScreenDestination.route)
                        coroutineScope.launch {
                            drawerState.close()
                        }
                              },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.navigation_drawer_favorites),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    badge = {
                        Text(
                            text = favoriteList.size.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                )
                NavigationDrawerItem(
                    modifier = Modifier.padding(top = 10.dp),
                    label = {
                            Text(
                                text = stringResource(R.string.navigation_drawer_about),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                    },
                    selected = false,
                    onClick = {
                        navController.navigate(route = "about_screen")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                              },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.navigation_drawer_about),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = modifier
        ) {
            composable(route = HomeDestination.route) {
                HomeScreen(
                    openToDrawer = {
                                   coroutineScope.launch {
                                       drawerState.open()
                                   }
                    },
                    navigateToAddPage = { navController.navigate(NoteAddScreenDestination.route) },
                    navigateToDetailPage = {
                        navController.navigate("${NoteDetailsDestination.route}/${it}")
                    },
                )
            }
            composable(route = "about_screen") {
                AboutScreen(
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable(route = FavoritesScreenDestination.route) {
                FavoritesScreen(
                    navigateToDetailScreen = { navController.navigate("${FavoritesNoteDetailsScreenDestination.route}/${it}") },
                    onBackButtonPressed = { navController.popBackStack() }
                )
            }
            composable(route = TrashScreenDestination.route) {
                TrashScreen(
                    navigateToDetailPage = {
                        navController.navigate("${DeleteNoteDetailsScreenDestination.route}/${it}")
                    },
                    onBackButtonPressed = { navController.popBackStack() }
                )
            }
            composable(route = NoteAddScreenDestination.route) {
                NoteAddScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = FavoritesNoteDetailsScreenDestination.routeWithArgs,
                arguments = listOf(navArgument(FavoritesNoteDetailsScreenDestination.noteIdArg) {
                    type = NavType.IntType
                })
            ) {
                FavoriteNoteDetailsScreen(
                    onBackPressed = { navController.popBackStack() },
                )
            }
            composable(
                route = NoteDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(NoteDetailsDestination.noteIdArg) {
                    type = NavType.IntType
                })
            ) {
                NoteDetailsScreen(
                    navigateBack = { navController.popBackStack() },
                )
            }
            composable(
                route = DeleteNoteDetailsScreenDestination.routeWithArgs,
                arguments = listOf(navArgument(DeleteNoteDetailsScreenDestination.noteIdArg) {
                    type = NavType.IntType
                })
            ) {
                DeletedNoteDetailsScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}