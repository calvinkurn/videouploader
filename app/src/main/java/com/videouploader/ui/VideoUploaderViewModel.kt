package com.videouploader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.callback.ErrorInfo
import com.videouploader.byteToMega
import com.videouploader.model.UploadResultModel
import com.videouploader.repository.UploaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

class VideoUploaderViewModel @Inject constructor(
    private val uploaderRepository: UploaderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<VideoUploaderUiState>(VideoInitialUiState)
    val uiState get() = _uiState

    private val _uploadProgress = MutableStateFlow(0.0)
    val uploadProgress get() = _uploadProgress

    private val _uploadResult = MutableStateFlow(UploadResultModel())
    val uploadResult get() = _uploadResult

    fun updateUiState(newState: VideoUploaderUiState) {
        uiState.tryEmit(newState)
    }

    fun uploadVideo(
        filePath: String
    ) {
        clearUploadData()
        viewModelScope.launch(Dispatchers.IO) {
            uploaderRepository.uploadVideo(
                filePath,
                onFinish = {
                    val resultPlaybackUrl = it?.get("url").toString()
                    _uploadResult.update { currentState ->
                        currentState.copy(playbackUrl = resultPlaybackUrl, error = null)
                    }
                },
                onError = {
                    _uploadResult.update { currentState ->
                        currentState.copy(playbackUrl = "", error = it)
                    }
                },
                onProgress = { byte, _ ->
                    _uploadProgress.tryEmit(byte.byteToMega())
                })
        }
    }

    fun clearUploadData() {
        _uploadResult.update {
            UploadResultModel()
        }
        _uploadProgress.tryEmit(0.0)
    }
}