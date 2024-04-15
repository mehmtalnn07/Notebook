package com.mehmetalan.notebook.ui.notes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mehmetalan.notebook.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutScreen(
    onBackPressed: () -> Unit,
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about_screen_top_bar_text),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.note),
                    contentDescription = stringResource(R.string.app_icon)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.designer_name),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Image(
                        painter = painterResource(R.drawable.cupcake),
                        contentDescription = stringResource(R.string.designer_icon),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.version_number),
                    fontWeight = FontWeight.Thin,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}