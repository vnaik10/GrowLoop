import android.util.Log
import com.example.growloop.chatbot.ChatMessage
import com.example.growloop.chatbot.MessageStatus
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

// Fixed API Response Models
data class CreateUserResponse(
    @SerializedName("user") val user: UserData,
    @SerializedName("key") val key: String
)

data class UserData(
    @SerializedName("id") val id: String
)

data class CreateConversationResponse(
    @SerializedName("conversation") val conversation: ConversationData
)

data class ConversationData(
    @SerializedName("id") val id: String
)

data class ListMessagesResponse(
    @SerializedName("messages") val messages: List<MessageItem>
)

data class MessageItem(
    @SerializedName("id") val id: String,
    @SerializedName("payload") val payload: Map<String, Any>,
    @SerializedName("userId") val userId: String,
    @SerializedName("createdAt") val createdAt: String
)

class BotpressApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d("BotpressAPI", "→ ${request.method} ${request.url}")
            val response = chain.proceed(request)
            Log.d("BotpressAPI", "← Response: ${response.code}")
            response
        }
        .build()

    private val gson = Gson()

    private val clientId = "d59b5133-e775-48dd-9f99-e71bae2faba0"
    private val baseUrl = "https://chat.botpress.cloud/$clientId"

    private var userId: String? = null
    private var userKey: String? = null  // ← This was missing!
    private var conversationId: String? = null

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("Botpress", "═══════════════════════════════")
            Log.d("Botpress", "Starting Botpress Initialization")
            Log.d("Botpress", "Client ID: $clientId")
            Log.d("Botpress", "═══════════════════════════════")

            // Step 1: Create User
            if (!createUser()) {
                Log.e("Botpress", "✗ Failed at Step 1: Create User")
                return@withContext false
            }

            // Step 2: Create Conversation
            if (!createConversation()) {
                Log.e("Botpress", "✗ Failed at Step 2: Create Conversation")
                return@withContext false
            }

            Log.d("Botpress", "═══════════════════════════════")
            Log.d("Botpress", "✓ Initialization SUCCESS!")
            Log.d("Botpress", "User ID: $userId")
            Log.d("Botpress", "User Key: ${userKey?.take(20)}...")
            Log.d("Botpress", "Conversation ID: $conversationId")
            Log.d("Botpress", "═══════════════════════════════")

            return@withContext true
        } catch (e: Exception) {
            Log.e("Botpress", "✗ Initialization Exception", e)
            return@withContext false
        }
    }

    private suspend fun createUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/users"

            Log.d("Botpress", "Creating user at: $url")

            val request = Request.Builder()
                .url(url)
                .post("{}".toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                Log.d("Botpress", "Create User Response Code: ${response.code}")
                Log.d("Botpress", "Create User Response Body: $body")

                if (response.isSuccessful && body != null) {
                    try {
                        val userResponse = gson.fromJson(body, CreateUserResponse::class.java)
                        userId = userResponse.user.id  // ← Fixed: Get ID from nested user object
                        userKey = userResponse.key      // ← Save the key!
                        Log.d("Botpress", "✓ User created: $userId")
                        Log.d("Botpress", "✓ User key: ${userKey?.take(20)}...")
                        return@withContext true
                    } catch (e: Exception) {
                        Log.e("Botpress", "✗ Failed to parse user response", e)
                        return@withContext false
                    }
                } else {
                    Log.e("Botpress", "✗ User creation failed with code: ${response.code}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e("Botpress", "✗ User creation exception", e)
            return@withContext false
        }
    }

    private suspend fun createConversation(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/conversations"

            Log.d("Botpress", "Creating conversation at: $url")

            val request = Request.Builder()
                .url(url)
                .post("{}".toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")
                .addHeader("x-user-key", userKey ?: "")  // ← Added the missing header!
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                Log.d("Botpress", "Create Conversation Response Code: ${response.code}")
                Log.d("Botpress", "Create Conversation Response Body: $body")

                if (response.isSuccessful && body != null) {
                    try {
                        val convResponse = gson.fromJson(body, CreateConversationResponse::class.java)
                        conversationId = convResponse.conversation.id  // ← Fixed: Get ID from nested conversation object
                        Log.d("Botpress", "✓ Conversation created: $conversationId")
                        return@withContext true
                    } catch (e: Exception) {
                        Log.e("Botpress", "✗ Failed to parse conversation response", e)
                        return@withContext false
                    }
                } else {
                    Log.e("Botpress", "✗ Conversation creation failed with code: ${response.code}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e("Botpress", "✗ Conversation creation exception", e)
            return@withContext false
        }
    }

    suspend fun sendMessage(text: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // CORRECT URL: Use /messages NOT /conversations/{id}/messages
            val url = "$baseUrl/messages"

            Log.d("Botpress", "Sending message to: $url")

            // CORRECT BODY: Include conversationId in the body
            val requestBody = """{
            "conversationId": "$conversationId",
            "payload": {
                "type": "text",
                "text": "$text"
            }
        }"""

            Log.d("Botpress", "Request body: $requestBody")

            val request = Request.Builder()
                .url(url)
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")
                .addHeader("x-user-key", userKey ?: "")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                Log.d("Botpress", "Send Message Response: ${response.code}")
                Log.d("Botpress", "Send Message Body: $body")

                if (response.isSuccessful) {
                    Log.d("Botpress", "✓ Message sent successfully")
                    return@withContext true
                } else {
                    Log.e("Botpress", "✗ Message send failed: ${response.code}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e("Botpress", "✗ Send message exception", e)
            return@withContext false
        }
    }

    // Add this data class at the top with other models
    data class ListMessagesResponseV2(
        @SerializedName("messages") val messages: List<MessageItem>
    )

    // Replace getMessages() function
    suspend fun getMessages(): List<ChatMessage> = withContext(Dispatchers.IO) {
        try {
            // Use the conversation list messages endpoint
            val url = "$baseUrl/conversations/$conversationId/messages"

            Log.d("Botpress", "Fetching messages from: $url")

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("x-user-key", userKey ?: "")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                Log.d("Botpress", "Get Messages Response: ${response.code}")
                Log.d("Botpress", "Get Messages Body: $body")

                if (response.isSuccessful && body != null) {
                    try {
                        val messagesResponse = gson.fromJson(body, ListMessagesResponseV2::class.java)

                        Log.d("Botpress", "Total messages: ${messagesResponse.messages.size}")

                        // Log all messages for debugging
                        messagesResponse.messages.forEach { msg ->
                            Log.d("Botpress", "Message: userId=${msg.userId}, text=${msg.payload["text"]}")
                        }

                        val chatMessages = messagesResponse.messages
                            .filter { msg ->
                                // Bot messages are where userId is NOT our user ID
                                msg.userId != userId
                            }
                            .mapNotNull { msg ->
                                val text = msg.payload["text"] as? String
                                if (!text.isNullOrBlank()) {
                                    Log.d("Botpress", "✓ Bot message found: $text")
                                    ChatMessage(
                                        id = msg.id,
                                        text = text,
                                        isFromUser = false,
                                        timestamp = System.currentTimeMillis(),
                                        status = MessageStatus.DELIVERED
                                    )
                                } else null
                            }

                        Log.d("Botpress", "✓ Returning ${chatMessages.size} bot messages")
                        return@withContext chatMessages
                    } catch (e: Exception) {
                        Log.e("Botpress", "✗ Parse error", e)
                        return@withContext emptyList()
                    }
                } else {
                    Log.e("Botpress", "✗ Failed: ${response.code}, Body: $body")
                    return@withContext emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("Botpress", "✗ Exception", e)
            return@withContext emptyList()
        }
    }


}
