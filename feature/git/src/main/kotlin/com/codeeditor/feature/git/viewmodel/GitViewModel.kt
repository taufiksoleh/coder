package com.codeeditor.feature.git.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeeditor.feature.git.repository.GitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GitCommit(
    val hash: String,
    val author: String,
    val message: String,
    val timestamp: Long
)

data class GitUiState(
    val currentBranch: String = "main",
    val branches: List<String> = emptyList(),
    val changedFiles: List<String> = emptyList(),
    val commits: List<GitCommit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GitViewModel @Inject constructor(
    private val gitRepository: GitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitUiState())
    val uiState: StateFlow<GitUiState> = _uiState.asStateFlow()

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val branches = gitRepository.getBranches()
                val currentBranch = gitRepository.getCurrentBranch()
                val changedFiles = gitRepository.getStatus()
                val commits = gitRepository.getCommits(20)

                _uiState.value = _uiState.value.copy(
                    branches = branches,
                    currentBranch = currentBranch,
                    changedFiles = changedFiles,
                    commits = commits,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun commit() {
        viewModelScope.launch {
            try {
                gitRepository.commit("Commit from Android IDE")
                refreshStatus()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun push() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                gitRepository.push()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun pull() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                gitRepository.pull()
                refreshStatus()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun checkout(branch: String) {
        viewModelScope.launch {
            try {
                gitRepository.checkout(branch)
                refreshStatus()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
