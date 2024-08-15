package com.videouploader.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class VideoUploaderViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow<VideoUploaderUiState>(VideoInitialUiState)
    val uiState get() = _uiState

    fun updateUiState(newState: VideoUploaderUiState) {
        uiState.tryEmit(newState)
    }
}