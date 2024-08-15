package com.videouploader.ui.view

import androidx.compose.runtime.Composable
import com.videouploader.model.RecordResultModel

@Composable
fun CameraUiView(
    isPermissionGranted: Boolean,
    onRecordFinish: (result: RecordResultModel) -> Unit
) {
    if (isPermissionGranted) {
        VideoRecorderScreen(onRecordFinish = onRecordFinish)
    } else {
        PermissionUiView()
    }
}