package com.geminichat.screen

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationScreen(
    conversations: SnapshotStateList<Triple<String, String, List<Bitmap>?>>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(conversations.size) { index ->
            val conversation = conversations[index]
            MessageItem(
                isInComing = conversation.first == "received",
                images = conversation.third ?: emptyList(),
                content = conversation.second,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}


@Composable
fun MessageItem(
    isInComing: Boolean,
    images: List<Bitmap>,
    content: String,
    modifier: Modifier = Modifier
) {

    val cardShape by remember {
        derivedStateOf {
            if (isInComing) {
                RoundedCornerShape(
                    16.dp,
                    16.dp,
                    16.dp,
                    0.dp
                )
            } else {
                RoundedCornerShape(
                    16.dp,
                    16.dp,
                    0.dp,
                    16.dp
                )
            }
        }
    }

    val cardPadding by remember {
        derivedStateOf {
            if (isInComing) {
                PaddingValues(end = 24.dp)
            } else {
                PaddingValues(start = 24.dp)
            }
        }
    }

    Column(modifier = modifier) {
        if (images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
            ) {
                items(images.size) { index ->
                    val image = images[index]
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .height(60.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            )
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(cardPadding),
            shape = cardShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isInComing) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring()
                    )
                )
            }
        }
    }
}