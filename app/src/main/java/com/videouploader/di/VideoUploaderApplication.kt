package com.videouploader.di

import android.app.Application
import dagger.Component

@Component
interface ApplicationComponent

class VideoUploaderApplication: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().repositoryModule(RepositoryModule(this)).build()
        appComponent.inject(this)
    }
}