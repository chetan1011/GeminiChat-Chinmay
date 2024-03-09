package com.geminichat

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.ai.client.generativeai.GenerativeModel
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.time.Duration.Companion.seconds

class MainViewModelTest {

    @Mock
    private lateinit var mainViewModel: MainViewModel


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = StandardTestDispatcher()
    private val apiKey = "AIzaSyC1ouktfCbem0ihuqSRSUjeAsfkT4BJ_m4"

    private var geneminiProModel: GenerativeModel? = null

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mainViewModel = MainViewModel()


        geneminiProModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        ).apply {
            startChat()
        }
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun testMainViewModal_isGenerating_true() = runTest {
        println("before isGenerating:" + mainViewModel.isGenerating.value)
        mainViewModel.sendText("whats is android?", SnapshotStateList<Bitmap>())
        println("after isGenerating:" + mainViewModel.isGenerating.value)
        Assert.assertEquals(true, mainViewModel.isGenerating.value)

    }

    @Test
    fun testMainViewModal_isGenerating_question1() = runTest(timeout = 30.seconds) {
        var respStatus = false

        geneminiProModel?.generateContentStream("What is android?")
            ?.collect { chunk ->
                println("Processing chunk: ${chunk.text}")
                if (chunk.text?.isNotEmpty() == true) {
                    respStatus = true
                }
            }
        Assert.assertEquals(true, respStatus)
    }


    @Test
    fun testMainViewModal_isGenerating_question2() = runTest(timeout = 30.seconds) {
        var respStatus = false
        geneminiProModel?.generateContentStream("What is google?")?.collect { chunk ->
            println("testMainViewModal_isGenerating_question2 chunk.text::" + chunk.text)
            if (chunk.text?.length!! > 0) {
                respStatus = true
            }
        }

        Assert.assertEquals(true, respStatus)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}


