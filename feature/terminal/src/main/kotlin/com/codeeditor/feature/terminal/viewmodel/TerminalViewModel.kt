package com.codeeditor.feature.terminal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

data class TerminalUiState(
    val output: String = "Terminal ready. Type your commands.\n",
    val currentInput: String = "",
    val workingDirectory: String = "/sdcard",
    val isRunning: Boolean = false
)

@HiltViewModel
class TerminalViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    private var currentProcess: Process? = null

    fun updateInput(input: String) {
        _uiState.value = _uiState.value.copy(currentInput = input)
    }

    fun executeCommand() {
        val command = _uiState.value.currentInput.trim()
        if (command.isEmpty()) return

        viewModelScope.launch {
            try {
                appendOutput("$ $command\n")
                _uiState.value = _uiState.value.copy(
                    currentInput = "",
                    isRunning = true
                )

                val output = withContext(Dispatchers.IO) {
                    executeShellCommand(command)
                }

                appendOutput(output)
            } catch (e: Exception) {
                appendOutput("Error: ${e.message}\n")
            } finally {
                _uiState.value = _uiState.value.copy(isRunning = false)
            }
        }
    }

    private suspend fun executeShellCommand(command: String): String = withContext(Dispatchers.IO) {
        try {
            val processBuilder = ProcessBuilder()

            // Handle special commands
            when {
                command.startsWith("cd ") -> {
                    val newDir = command.substring(3).trim()
                    val targetDir = if (newDir.startsWith("/")) {
                        File(newDir)
                    } else {
                        File(_uiState.value.workingDirectory, newDir)
                    }

                    if (targetDir.exists() && targetDir.isDirectory) {
                        _uiState.value = _uiState.value.copy(
                            workingDirectory = targetDir.absolutePath
                        )
                        return@withContext "Changed directory to ${targetDir.absolutePath}\n"
                    } else {
                        return@withContext "cd: no such file or directory: $newDir\n"
                    }
                }
                command == "pwd" -> {
                    return@withContext "${_uiState.value.workingDirectory}\n"
                }
                command == "clear" -> {
                    _uiState.value = _uiState.value.copy(output = "")
                    return@withContext ""
                }
            }

            // Execute command using shell
            processBuilder.command("sh", "-c", command)
            processBuilder.directory(File(_uiState.value.workingDirectory))
            processBuilder.redirectErrorStream(true)

            currentProcess = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(currentProcess!!.inputStream))
            val output = StringBuilder()

            reader.useLines { lines ->
                lines.forEach { line ->
                    output.append(line).append("\n")
                }
            }

            currentProcess?.waitFor()
            currentProcess = null

            output.toString()
        } catch (e: Exception) {
            "Error executing command: ${e.message}\n"
        }
    }

    private fun appendOutput(text: String) {
        _uiState.value = _uiState.value.copy(
            output = _uiState.value.output + text
        )
    }

    fun clearTerminal() {
        _uiState.value = _uiState.value.copy(
            output = "Terminal cleared.\n"
        )
    }

    fun killSession() {
        currentProcess?.destroy()
        currentProcess = null
        _uiState.value = _uiState.value.copy(isRunning = false)
        appendOutput("\nSession killed.\n")
    }

    override fun onCleared() {
        super.onCleared()
        currentProcess?.destroy()
    }
}
