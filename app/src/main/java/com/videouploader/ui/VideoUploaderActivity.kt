package com.videouploader.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.videouploader.CAMERA_ROUTE
import com.videouploader.CAMERA_UI_PERMISSION_REQUEST_CODE
import com.videouploader.UPLOAD_ROUTE
import com.videouploader.UPLOAD_ROUTE_ACTION
import com.videouploader.UPLOAD_ROUTE_PARAM_KEY
import com.videouploader.di.VideoUploaderApplication
import com.videouploader.ui.theme.VideoUploaderTheme
import com.videouploader.ui.view.CameraUiView
import com.videouploader.ui.view.UploaderUiView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class VideoUploaderActivity @Inject constructor() : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: VideoUploaderViewModel by viewModels { viewModelFactory }

    private val isPermissionGranted = mutableStateOf(false)

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isPermissionGranted.value = checkRequiredPermission()

        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()

            VideoUploaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    navController?.let { controller ->
                        NavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = controller,
                            startDestination = CAMERA_ROUTE
                        ) {
                            composable(CAMERA_ROUTE) {
                                CameraUiView(isPermissionGranted.value) { recordResult ->
                                    if (recordResult.error == null) {
                                        viewModel.updateUiState(VideoRecordSuccess(recordResult))
                                    } else {
                                        // TODO: Error effect
                                    }
                                }
                            }
                            composable(
                                route = UPLOAD_ROUTE,
                                arguments = listOf(
                                    navArgument(UPLOAD_ROUTE_PARAM_KEY) {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    }
                                )
                            ) {
                                val dataString = it.arguments?.getString(UPLOAD_ROUTE_PARAM_KEY)
                                UploaderUiView(dataString)
                            }
                        }
                    }
                }
            }
        }

        initInjection()
        observeUiState()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_UI_PERMISSION_REQUEST_CODE) {
            val result = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (result != isPermissionGranted.value) {
                isPermissionGranted.value = result
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkRequiredPermission(false).let {
            if (it != isPermissionGranted.value) {
                isPermissionGranted.value = it
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                when (it) {
                    is VideoRecordSuccess -> {
                        navController?.navigate(UPLOAD_ROUTE_ACTION + it.result.uri.toString())
                    }
                }
            }
        }
    }

    private fun initInjection() {
        (application as VideoUploaderApplication).appComponent.inject(this)
    }

    private fun checkRequiredPermission(isNeedToRequest: Boolean = true): Boolean {
        val cameraPermission =
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val audioPermission =
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        return if (cameraPermission && audioPermission) {
            true
        } else {
            if (!isNeedToRequest) return false

            val requestList = mutableListOf<String>()
            if (!cameraPermission) {
                requestList.add(Manifest.permission.CAMERA)
            }
            if (!audioPermission) {
                requestList.add(Manifest.permission.RECORD_AUDIO)
            }

            ActivityCompat.requestPermissions(
                this,
                requestList.toTypedArray(),
                CAMERA_UI_PERMISSION_REQUEST_CODE
            )

            return false
        }
    }
}