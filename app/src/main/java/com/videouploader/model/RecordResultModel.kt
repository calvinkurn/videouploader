package com.videouploader.model

data class RecordResultModel(
    val durationString: String,
    val rawDuration: Long,
    val resultPath: String,
    val error: Throwable?,
    val size: Double
)