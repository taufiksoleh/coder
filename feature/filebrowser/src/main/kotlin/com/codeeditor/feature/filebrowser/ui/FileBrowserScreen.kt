package com.codeeditor.feature.filebrowser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codeeditor.feature.filebrowser.viewmodel.FileBrowserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    viewModel: FileBrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.currentPath)
                },
                actions = {
                    IconButton(onClick = { viewModel.createNewFolder() }) {
                        Icon(Icons.Default.CreateNewFolder, "New Folder")
                    }
                    IconButton(onClick = { viewModel.createNewFile() }) {
                        Icon(Icons.Default.NoteAdd, "New File")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.files) { file ->
                FileItem(
                    file = file,
                    onClick = { viewModel.onFileClick(file) }
                )
            }
        }
    }
}

@Composable
fun FileItem(
    file: File,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(file.name) },
        supportingContent = {
            if (file.isFile) {
                Text("${file.length() / 1024} KB")
            }
        },
        leadingContent = {
            Icon(
                imageVector = if (file.isDirectory) {
                    Icons.Default.Folder
                } else {
                    Icons.Default.Description
                },
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
