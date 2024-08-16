package com.videouploader.ui.view

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.videouploader.FILE_EXT
import com.videouploader.LIMIT_CACHE_FILE
import com.videouploader.byteToMega
import com.videouploader.getUniqueStringTime
import com.videouploader.model.RecordResultModel
import com.videouploader.nanoToSec
import com.videouploader.toTimerFormat
import java.io.File

@Composable
fun VideoRecorderScreen(
    onRecordFinish: (result: RecordResultModel) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoCapture = remember {
        VideoCapture.withOutput(
            Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
        )
    }

    val recording = remember { mutableStateOf<Recording?>(null) }
    val timer = remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(key1 = Unit) {
        context.cacheDir.listFiles()?.let {
            if (it.size >= LIMIT_CACHE_FILE) {
                it.forEach { fileRef -> fileRef.delete() }
            }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, videoCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (recording.value == null) {
                val name = getUniqueStringTime()

                val fileStoreOutputOptions = FileOutputOptions.Builder(
                    File(
                        context.cacheDir,
                        "$name.$FILE_EXT"
                    )
                ).build()

                recording.value = videoCapture.output
                    .prepareRecording(context, fileStoreOutputOptions)
                    .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                        when (recordEvent) {
                            is VideoRecordEvent.Finalize -> {
                                onRecordFinish(
                                    RecordResultModel(
                                        durationString = timer.longValue.toTimerFormat(),
                                        rawDuration = timer.longValue,
                                        resultPath = recordEvent.outputResults.outputUri.toString(),
                                        error = recordEvent.cause,
                                        size = recordEvent.recordingStats.numBytesRecorded.byteToMega()
                                    )
                                )
                                timer.longValue = 0L
                                recording.value = null
                            }

                            is VideoRecordEvent.Status -> {
                                timer.longValue = recordEvent.recordingStats.recordedDurationNanos.nanoToSec()
                            }
                        }
                    }
            } else {
                recording.value?.stop()
            }
        }) {
            Text(
                text = if (recording.value == null) "Start Recording" else "Stop Recording\n${timer.longValue.toTimerFormat()}",
                textAlign = TextAlign.Center
            )
        }
    }
}