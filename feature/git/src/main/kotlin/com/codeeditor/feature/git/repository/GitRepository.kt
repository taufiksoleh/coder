package com.codeeditor.feature.git.repository

import com.codeeditor.feature.git.viewmodel.GitCommit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitRepository @Inject constructor() {

    private var git: Git? = null
    private var repository: Repository? = null

    suspend fun openRepository(path: File) = withContext(Dispatchers.IO) {
        try {
            val gitDir = File(path, ".git")
            repository = FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .findGitDir()
                .build()

            git = Git(repository)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to open repository: ${e.message}", e)
        }
    }

    suspend fun clone(url: String, directory: File) = withContext(Dispatchers.IO) {
        try {
            git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(directory)
                .call()
            repository = git?.repository
        } catch (e: Exception) {
            throw IllegalStateException("Failed to clone repository: ${e.message}", e)
        }
    }

    suspend fun init(directory: File) = withContext(Dispatchers.IO) {
        try {
            git = Git.init()
                .setDirectory(directory)
                .call()
            repository = git?.repository
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize repository: ${e.message}", e)
        }
    }

    suspend fun getStatus(): List<String> = withContext(Dispatchers.IO) {
        val status = git?.status()?.call() ?: return@withContext emptyList()

        val changedFiles = mutableListOf<String>()
        changedFiles.addAll(status.added)
        changedFiles.addAll(status.modified)
        changedFiles.addAll(status.removed)
        changedFiles.addAll(status.untracked)

        changedFiles
    }

    suspend fun getBranches(): List<String> = withContext(Dispatchers.IO) {
        git?.branchList()?.call()?.map { it.name.removePrefix("refs/heads/") } ?: emptyList()
    }

    suspend fun getCurrentBranch(): String = withContext(Dispatchers.IO) {
        repository?.branch ?: "unknown"
    }

    suspend fun getCommits(limit: Int = 20): List<GitCommit> = withContext(Dispatchers.IO) {
        val commits = mutableListOf<GitCommit>()

        try {
            git?.log()?.setMaxCount(limit)?.call()?.forEach { revCommit ->
                commits.add(
                    GitCommit(
                        hash = revCommit.name,
                        author = revCommit.authorIdent.name,
                        message = revCommit.shortMessage,
                        timestamp = revCommit.commitTime.toLong() * 1000
                    )
                )
            }
        } catch (e: Exception) {
            // Repository might be empty
        }

        commits
    }

    suspend fun add(filePattern: String = ".") = withContext(Dispatchers.IO) {
        git?.add()?.addFilepattern(filePattern)?.call()
    }

    suspend fun commit(message: String) = withContext(Dispatchers.IO) {
        add()
        git?.commit()?.setMessage(message)?.call()
    }

    suspend fun push() = withContext(Dispatchers.IO) {
        git?.push()?.call()
    }

    suspend fun pull() = withContext(Dispatchers.IO) {
        git?.pull()?.call()
    }

    suspend fun checkout(branch: String) = withContext(Dispatchers.IO) {
        git?.checkout()?.setName(branch)?.call()
    }

    fun close() {
        git?.close()
        repository?.close()
    }
}
