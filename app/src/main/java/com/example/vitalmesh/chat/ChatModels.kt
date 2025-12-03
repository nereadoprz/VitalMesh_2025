// chat/ChatModels.kt
package com.example.vitalmesh.chat

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChatUser(
    val id: String,
    val name: String,
    val status: String = "Online",
    val avatar: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
data class Message(
    val id: String = "",
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isFromCurrentUser: Boolean = false
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return timestamp.format(formatter)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class Conversation(
    val id: String,
    val user: ChatUser,
    val lastMessage: String = "",
    val lastTimestamp: LocalDateTime = LocalDateTime.now(),
    val unreadCount: Int = 0,
    val messages: List<Message> = emptyList()
)

object MockChatData {
    val currentUser = ChatUser(
        id = "current_user",
        name = "You",
        status = "Online"
    )

    val users = listOf(
        ChatUser(
            id = "user1",
            name = "Sergeant García",
            status = "Online",
            avatar = "👨‍✈️"
        ),
        ChatUser(
            id = "user2",
            name = "Corporal López",
            status = "Online",
            avatar = "👨‍💼"
        ),
        ChatUser(
            id = "user3",
            name = "Private Martínez",
            status = "Away",
            avatar = "👨‍🦱"
        ),
        ChatUser(
            id = "user4",
            name = "Sergeant Rodriguez",
            status = "Offline",
            avatar = "👨‍🦲"
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val conversations = listOf(
        Conversation(
            id = "conv1",
            user = users[0],
            lastMessage = "¿Cómo está el equipo?",
            lastTimestamp = LocalDateTime.now().minusMinutes(5),
            unreadCount = 2,
            messages = listOf(
                Message(
                    id = "msg1",
                    senderId = "user1",
                    senderName = "Sergeant García",
                    text = "Hola, ¿cómo estás?",
                    timestamp = LocalDateTime.now().minusHours(2),
                    isFromCurrentUser = false
                ),
                Message(
                    id = "msg2",
                    senderId = "current_user",
                    senderName = "You",
                    text = "¡Bien! ¿Y tú?",
                    timestamp = LocalDateTime.now().minusMinutes(30),
                    isFromCurrentUser = true
                ),
                Message(
                    id = "msg3",
                    senderId = "user1",
                    senderName = "Sergeant García",
                    text = "¿Cómo está el equipo?",
                    timestamp = LocalDateTime.now().minusMinutes(5),
                    isFromCurrentUser = false
                )
            )
        ),
        Conversation(
            id = "conv2",
            user = users[1],
            lastMessage = "Entendido, nos vemos en 15 minutos",
            lastTimestamp = LocalDateTime.now().minusHours(1),
            unreadCount = 0,
            messages = listOf(
                Message(
                    id = "msg4",
                    senderId = "user2",
                    senderName = "Corporal López",
                    text = "¿Dónde estás?",
                    timestamp = LocalDateTime.now().minusHours(1).minusMinutes(30),
                    isFromCurrentUser = false
                ),
                Message(
                    id = "msg5",
                    senderId = "current_user",
                    senderName = "You",
                    text = "Estoy en la base",
                    timestamp = LocalDateTime.now().minusHours(1).minusMinutes(20),
                    isFromCurrentUser = true
                ),
                Message(
                    id = "msg6",
                    senderId = "user2",
                    senderName = "Corporal López",
                    text = "Entendido, nos vemos en 15 minutos",
                    timestamp = LocalDateTime.now().minusHours(1),
                    isFromCurrentUser = false
                )
            )
        ),
        Conversation(
            id = "conv3",
            user = users[2],
            lastMessage = "Reportando posición",
            lastTimestamp = LocalDateTime.now().minusHours(3),
            unreadCount = 0,
            messages = listOf(
                Message(
                    id = "msg7",
                    senderId = "user3",
                    senderName = "Private Martínez",
                    text = "Reportando posición",
                    timestamp = LocalDateTime.now().minusHours(3),
                    isFromCurrentUser = false
                )
            )
        ),
        Conversation(
            id = "conv4",
            user = users[3],
            lastMessage = "Último contacto hace 2 días",
            lastTimestamp = LocalDateTime.now().minusDays(2),
            unreadCount = 0,
            messages = listOf(
                Message(
                    id = "msg8",
                    senderId = "user4",
                    senderName = "Sergeant Rodriguez",
                    text = "Volveré en línea pronto",
                    timestamp = LocalDateTime.now().minusDays(2),
                    isFromCurrentUser = false
                )
            )
        )
    )
}