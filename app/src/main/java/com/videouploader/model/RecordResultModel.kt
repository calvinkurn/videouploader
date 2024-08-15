package com.videouploader.model

import android.net.Uri

data class RecordResultModel(
    val durationString: String,
    val rawDuration: Long,
    val uri: Uri,
    val error: Throwable?,
    val size: Double
)