package com.videouploader.model

import com.cloudinary.android.callback.ErrorInfo

data class UploadResultModel (
    val playbackUrl: String = "",
    val error: ErrorInfo? = null
)