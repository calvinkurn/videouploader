package com.videouploader.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import javax.inject.Inject

interface UploaderRepository {
    suspend fun uploadVideo(
        filePath: String,
        onFinish: (resultData: MutableMap<Any?, Any?>?) -> Unit,
        onError: (error: ErrorInfo?) -> Unit,
        onProgress: (bytes: Long, totalBytes: Long) -> Unit,
    )
}

class UploaderRepositoryImpl @Inject constructor(
    context: Context
) : UploaderRepository {

    init {
        val config = mutableMapOf(
            "cloud_name" to CLOUD_NAME,
            "secure" to true,
            "api_secret" to "undefined",
            "api_key" to API_KEY
        )
        MediaManager.init(context, config)
    }

    override suspend fun uploadVideo(
        filePath: String, onFinish: (resultData: MutableMap<Any?, Any?>?) -> Unit,
        onError: (error: ErrorInfo?) -> Unit,
        onProgress: (bytes: Long, totalBytes: Long) -> Unit,
    ) {
        val fileUri = Uri.parse(filePath)

        MediaManager.get().upload(fileUri)
            .unsigned(PRESETS_KEY)
            .option("resource_type", "video")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    onProgress(1, 0)
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    onProgress(bytes, totalBytes)
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    onFinish(resultData)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    onError(error)
                }
            })
            .dispatch()
    }

    companion object {
        /**
         * All API key should not be shared to public
         * due to circumstances, i put the key here to make it easier to check
         */
        private const val CLOUD_NAME = "dxowcfja7"
        private const val API_KEY = "382418376615791"
        private const val PRESETS_KEY = "aerq9wtw"
    }
}