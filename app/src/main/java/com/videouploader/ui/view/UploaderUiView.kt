package com.videouploader.ui.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.videouploader.model.RecordResultModel

@Composable
fun UploaderUiView(
    data: RecordResultModel,
    onBackPressed: () -> Unit = {},
    onUploadPressed: (RecordResultModel) -> Unit = {}
) {
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
                            .padding(bottom = 16.dp)
                            .background(Color.Gray)
                    ) {
                        Text(text = "TODO: Video preview")
                    }

                    TableCell(label = "File path", value = data.resultPath)
                    TableCell(label = "Duration", value = data.durationString)
                    TableCell(label = "Size", value = "${data.size} Mb")
                    TableCell(label = "Upload status", value = "-")
                    TableCell(label = "Uploaded path", value = "-")
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
    value: String?
) {
    Row {
        Text(text = label, modifier = Modifier.weight(0.3f))
        Text(text = ":", modifier = Modifier.weight(0.05f), textAlign = TextAlign.Center)
        Text(text = value ?: "-", modifier = Modifier.weight(0.6f))
    }
}