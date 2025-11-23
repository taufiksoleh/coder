package com.codeeditor.feature.filebrowser.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class FileBrowserUiState(
    val currentPath: String = "",
    val files: List<File> = emptyList(),
    val selectedFile: File? = null
)

@HiltViewModel
class FileBrowserViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState: StateFlow<FileBrowserUiState> = _uiState.asStateFlow()

    init {
        loadFiles(Environment.getExternalStorageDirectory())
    }

    private fun loadFiles(directory: File) {
        viewModelScope.launch {
            val files = directory.listFiles()?.sortedWith(
                compareBy<File> { !it.isDirectory }
                    .thenBy { it.name.lowercase() }
            ) ?: emptyList()

            _uiState.value = _uiState.value.copy(
                currentPath = directory.absolutePath,
                files = files
            )
        }
    }

    fun onFileClick(file: File) {
        if (file.isDirectory) {
            loadFiles(file)
        } else {
            _uiState.value = _uiState.value.copy(selectedFile = file)
            // TODO: Open file in editor
        }
    }

    fun createNewFile() {
        // TODO: Show dialog to create new file
    }

    fun createNewFolder() {
        // TODO: Show dialog to create new folder
    }

    fun navigateUp() {
        val currentDir = File(_uiState.value.currentPath)
        currentDir.parentFile?.let { parent ->
            loadFiles(parent)
        }
    }
}
