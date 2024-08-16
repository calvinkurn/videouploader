package com.videouploader

import com.cloudinary.android.callback.ErrorInfo
import com.videouploader.model.RecordResultModel
import com.videouploader.repository.UploaderRepository
import com.videouploader.ui.VideoRecordFinish
import com.videouploader.ui.VideoUploaderViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VideoUploaderViewModelTest {
    private lateinit var viewModel: VideoUploaderViewModel
    private val uploaderRepository = mockk<UploaderRepository>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        viewModel = VideoUploaderViewModel(uploaderRepository)
    }

    @Test
    fun `should success upload video`() = runTest {
        val expectedUrlResult = "Expected_URL_Result"
        val onSuccessSlot = slot<(resultData: MutableMap<Any?, Any?>?) -> Unit>()

        coEvery { uploaderRepository.uploadVideo(any(), capture(onSuccessSlot), any(), any()) } answers {
            onSuccessSlot.captured(mutableMapOf("url" to expectedUrlResult))
        }
        viewModel.uploadVideo("")

        launch {
            viewModel.uploadResult.collectLatest {
                if (it.playbackUrl.isNotEmpty()) {
                    Assert.assertEquals(expectedUrlResult, it.playbackUrl)
                    this.cancel()
                }
            }
        }
    }

    @Test
    fun `should failed upload video`() = runTest {
        val expectedErrorMsg = "Expected_Error_Msg"
        val expectedErrorCode = ErrorInfo.UNKNOWN_ERROR
        val onErrorSlot = slot<(resultData: ErrorInfo?) -> Unit>()

        coEvery { uploaderRepository.uploadVideo(any(), any(), capture(onErrorSlot), any()) } answers {
            onErrorSlot.captured(ErrorInfo(expectedErrorCode, expectedErrorMsg))
        }
        viewModel.uploadVideo("")

        viewModel.uploadResult.take(1).collectLatest {
            Assert.assertEquals(expectedErrorMsg, it.error?.description)
            Assert.assertEquals(expectedErrorCode, it.error?.code)
        }
    }

    @Test
    fun `should update upload progress`() = runTest {
        val byte = 1_000_000L
        val expectedMegaByte = byte.byteToMega()
        val onUploadProgress = slot<(byte: Long, totalByte: Long) -> Unit>()

        coEvery { uploaderRepository.uploadVideo(any(), any(), any(), capture(onUploadProgress)) } answers {
            onUploadProgress.captured(byte, 10L)
        }
        viewModel.uploadVideo("")

        viewModel.uploadProgress.take(1).collectLatest {
            Assert.assertEquals(expectedMegaByte.toInt(), it.toInt())
        }
    }

    @Test
    fun `should route to uploader view`() = runTest {
        val expectedResultPath = "Result_Path"
        viewModel.updateUiState(VideoRecordFinish(RecordResultModel("",0L, expectedResultPath, null, 0.0)))

        viewModel.uiState.take(1).collectLatest {
            Assert.assertEquals(expectedResultPath, (it as VideoRecordFinish).result.resultPath)
        }
    }
}