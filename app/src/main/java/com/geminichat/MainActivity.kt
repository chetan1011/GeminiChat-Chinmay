package com.geminichat

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.geminichat.screen.ConversationScreen
import com.geminichat.ui.theme.GeminiChatTheme

class MainActivity : ComponentActivity() {


    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel by viewModels<MainViewModel>()

        setContent {
            GeminiChatTheme {

                var promptText by remember {
                    mutableStateOf("")
                }

                val conversations = mainViewModel.conversations
                val isGenerating by mainViewModel.isGenerating

                val keyboardController = LocalSoftwareKeyboardController.current

                val imageBitmaps: SnapshotStateList<Bitmap> = remember {
                    mutableStateListOf()
                }

                val context = LocalContext.current

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text(text = "Gemini Chat : Chinmay") },
                            )
                        },
                        bottomBar = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(imageBitmaps.size) { index ->
                                        val imageBitmap = imageBitmaps[index]
                                        Image(
                                            bitmap = imageBitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(100.dp)
                                                .animateItemPlacement()
                                                .border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                )
                                                .clickable {
                                                    imageBitmaps.remove(imageBitmap)
                                                }
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                        .wrapContentHeight(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = promptText,
                                        onValueChange = { promptText = it },
                                        label = { Text(text = "Message") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    FloatingActionButton(
                                        elevation = FloatingActionButtonDefaults.elevation(
                                            defaultElevation = if (isGenerating) 0.dp else 6.dp,
                                            pressedElevation = 0.dp
                                        ),
                                        onClick = {
                                            if (promptText.isNotBlank() && isGenerating.not()) {
                                                mainViewModel.sendText(promptText, imageBitmaps)
                                                promptText = ""
                                                imageBitmaps.clear()
                                                keyboardController?.hide()
                                            } else if (promptText.isBlank()) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter a message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        AnimatedContent(
                                            targetState = isGenerating,
                                            label = ""
                                        ) { generating ->
                                            if (generating) {
                                                CircularProgressIndicator()
                                            } else {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    ) { contentPadding ->
                        ConversationScreen(
                            conversations = conversations,
                            modifier = Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }
}


