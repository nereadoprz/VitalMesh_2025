// chat/ChatScreen.kt
package com.example.vitalmesh.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitalmesh.R
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    onChatSelected: (Conversation) -> Unit
) {
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val conversations = MockChatData.conversations

    val filteredConversations = conversations.filter {
        it.user.name.contains(searchQuery, ignoreCase = true)
    }

    if (selectedConversation != null) {
        ChatDetailScreen(
            conversation = selectedConversation!!,
            onBackClick = { selectedConversation = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.military_olive))
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(filteredConversations) { conversation ->
                    ConversationItemCard(
                        conversation = conversation,
                        onClick = {
                            selectedConversation = conversation
                            onChatSelected(conversation)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(MaterialTheme.shapes.medium),
        placeholder = { Text("Search contacts...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun ConversationItemCard(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.military_khaki)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.military_green)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.user.avatar.ifEmpty { conversation.user.name.first().toString() },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conversation.user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.military_green),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = formatTime(conversation.lastTimestamp),
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.military_green)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = conversation.lastMessage,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.military_green).copy(alpha = 0.7f),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                StatusIndicator(status = conversation.user.status)
            }

            if (conversation.unreadCount > 0) {
                Badge(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    containerColor = Color.Red
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val color = when (status) {
        "Online" -> Color.Green
        "Away" -> Color.Yellow
        "Offline" -> Color.Gray
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")

    return when {
        dateTime.toLocalDate() == now.toLocalDate() -> dateTime.format(formatter)
        dateTime.toLocalDate() == now.minusDays(1).toLocalDate() -> "Yesterday"
        else -> {
            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM")
            dateTime.format(dateFormatter)
        }
    }
}
