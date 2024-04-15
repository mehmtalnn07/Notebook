package com.mehmetalan.notebook

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.notebook.ui.navigation.NotebookNavHost

@Composable
fun NotebookApp(navController: NavHostController = rememberNavController()) {
    NotebookNavHost(navController = navController)
}