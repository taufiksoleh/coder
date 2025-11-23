package com.codeeditor.feature.git.di

import com.codeeditor.feature.git.repository.GitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitModule {

    @Provides
    @Singleton
    fun provideGitRepository(): GitRepository {
        return GitRepository()
    }
}
