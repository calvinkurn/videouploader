package com.videouploader.ui

import com.videouploader.model.RecordResultModel

interface VideoUploaderUiState

object VideoInitialUiState: VideoUploaderUiState
object RouteToCamera: VideoUploaderUiState

data class VideoRecordFinish(val result: RecordResultModel): VideoUploaderUiState