package com.videouploader.di

import android.content.Context
import com.videouploader.repository.UploaderRepository
import com.videouploader.repository.UploaderRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context.applicationContext
    }

    @Provides
    @Singleton
    fun provideUploaderRepository(): UploaderRepository {
        return UploaderRepositoryImpl(context)
    }
}