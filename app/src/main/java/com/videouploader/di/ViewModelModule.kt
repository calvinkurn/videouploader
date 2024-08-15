package com.videouploader.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.videouploader.di.viewmodel.ViewModelFactory
import com.videouploader.di.viewmodel.ViewModelKey
import com.videouploader.ui.VideoUploaderViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(VideoUploaderViewModel::class)
    abstract fun bindStockMonitorViewModel(viewModel: VideoUploaderViewModel): ViewModel
}