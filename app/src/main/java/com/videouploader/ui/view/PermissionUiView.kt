package com.videouploader.ui.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionUiView() {
    val context = LocalContext.current as ComponentActivity

    Column {
        Text("Permission is required for recording. Please enable it in the settings.")

        Button(onClick = {
            openAppSettings(context)
        }) {
            Text("Open Settings")
        }
    }

}

private fun openAppSettings(context: ComponentActivity) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}