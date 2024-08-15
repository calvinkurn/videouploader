package com.videouploader.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.videouploader.model.RecordResultModel

@Composable
fun UploaderUiView(
    data: String? = null
) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        text = "Uploader Ui View\n\n$data",
        textAlign = TextAlign.Center
    )
}