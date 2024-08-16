package com.videouploader.ui.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.videouploader.model.RecordResultModel
import com.videouploader.ui.VideoUploaderViewModel

@Composable
fun UploaderUiView(
    data: RecordResultModel,
    viewModel: VideoUploaderViewModel,
    onBackPressed: () -> Unit = {},
    onUploadPressed: (RecordResultModel) -> Unit = {}
) {
    val uploadProgress = viewModel.uploadProgress.collectAsState()
    val uploadResult = viewModel.uploadResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
    ) {
        if (data.error != null) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                text = "Error recording, please retry\n\n${data.error.message}",
                textAlign = TextAlign.Center,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(1f)
                    .padding(bottom = 68.dp)
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.Gray)
                    ) {
                        VideoPlayer(
                            videoUri = data.resultPath,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }

                    TableCell(label = "File path", value = data.resultPath)
                    TableCell(label = "Duration", value = data.durationString)
                    TableCell(label = "Size", value = "${data.size} Mb")

                    val playbackUrl = uploadResult.value.playbackUrl
                    if (uploadProgress.value > 0 || playbackUrl.isNotEmpty()) {
                        val progress =
                            if (playbackUrl.isNotEmpty()) 100.0 else ((uploadProgress.value / data.size) * 100)
                        TableCell(label = "Upload", value = "${progress.toInt()} %")
                    }
                    if (playbackUrl.isNotEmpty()) {
                        TableCell(label = "Playback url", value = playbackUrl, selectable = true)
                    } else if (uploadResult.value.error != null) {
                        TableCell(
                            label = "Upload error",
                            value = uploadResult.value.error?.description ?: "Default error"
                        )
                    }
                }

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter),
        ) {
            Button(modifier = Modifier
                .weight(1f)
                .padding(8.dp), onClick = { onBackPressed() }) {
                Text(text = "Back")
            }
            if (data.error == null) {
                Button(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp), onClick = { onUploadPressed(data) }) {
                    Text(text = "Upload")
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    label: String,
    value: String?,
    selectable: Boolean = false
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row {
        Text(text = label, modifier = Modifier.weight(0.3f))
        Text(text = ":", modifier = Modifier.weight(0.05f), textAlign = TextAlign.Center)
        Text(text = value ?: "-", modifier = Modifier.weight(0.6f).selectable(selected = false, enabled = selectable) {
            clipboardManager.setText(AnnotatedString(value ?: "-", ParagraphStyle()))
            Toast.makeText(context, "Copy to clipboard", Toast.LENGTH_LONG).show()
        })
    }
}