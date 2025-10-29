package com.example.growloop.chatbot

// ChatModels.kt
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, FAILED
}

// API Response Models
data class BotpressCreateUserResponse(
    val key: String
)

data class BotpressCreateConversationResponse(
    val conversation: ConversationData
)

data class ConversationData(
    val id: String
)

data class BotpressMessagesResponse(
    val messages: List<BotpressMessageData>
)

data class BotpressMessageData(
    val id: String,
    val payload: MessagePayload,
    val userId: String,
    val createdAt: String
)

data class MessagePayload(
    val text: String? = null,
    val type: String
)
