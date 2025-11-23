package com.codeeditor.feature.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditorUiState(
    val content: String = "",
    val currentFile: File? = null,
    val isDirty: Boolean = false,
    val language: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    fun openFile(file: File) {
        viewModelScope.launch {
            try {
                val content = file.readText()
                _uiState.value = _uiState.value.copy(
                    content = content,
                    currentFile = file,
                    isDirty = false,
                    language = detectLanguage(file.extension)
                )
                undoStack.clear()
                redoStack.clear()
                undoStack.add(content)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateContent(newContent: String) {
        if (newContent != _uiState.value.content) {
            undoStack.add(_uiState.value.content)
            redoStack.clear()

            _uiState.value = _uiState.value.copy(
                content = newContent,
                isDirty = true
            )
        }
    }

    fun saveFile() {
        viewModelScope.launch {
            _uiState.value.currentFile?.let { file ->
                try {
                    file.writeText(_uiState.value.content)
                    _uiState.value = _uiState.value.copy(isDirty = false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun undo() {
        if (undoStack.size > 1) {
            val current = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(current)
            _uiState.value = _uiState.value.copy(
                content = undoStack.last(),
                isDirty = true
            )
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val content = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(content)
            _uiState.value = _uiState.value.copy(
                content = content,
                isDirty = true
            )
        }
    }

    private fun detectLanguage(extension: String): String {
        return when (extension.lowercase()) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "py" -> "python"
            "js", "jsx" -> "javascript"
            "ts", "tsx" -> "typescript"
            "html", "htm" -> "html"
            "css" -> "css"
            "json" -> "json"
            "xml" -> "xml"
            "md" -> "markdown"
            "sh" -> "shell"
            "c" -> "c"
            "cpp", "cc", "cxx" -> "cpp"
            "h", "hpp" -> "cpp"
            "rs" -> "rust"
            "go" -> "go"
            else -> "plaintext"
        }
    }
}
