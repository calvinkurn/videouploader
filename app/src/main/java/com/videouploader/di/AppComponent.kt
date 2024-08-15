package com.videouploader.di

import com.videouploader.ui.VideoUploaderActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(application: VideoUploaderApplication)
    fun inject(activity: VideoUploaderActivity)
}