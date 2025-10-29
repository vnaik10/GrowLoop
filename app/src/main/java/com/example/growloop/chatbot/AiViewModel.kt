import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growloop.chatbot.ChatMessage
import com.example.growloop.chatbot.MessageStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val apiService = BotpressApiService()

    val messages = mutableStateListOf<ChatMessage>()
    val isInitialized = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val isTyping = mutableStateOf(false)

    init {
        initializeBot()
    }

    private fun initializeBot() {
        viewModelScope.launch {
            isLoading.value = true
            Log.d("ChatViewModel", "Starting initialization...")

            val success = apiService.initialize()
            isInitialized.value = success
            isLoading.value = false

            Log.d("ChatViewModel", "Initialization result: $success")

            if (success) {
                // Welcome message only when API is connected
                messages.add(
                    ChatMessage(
                        text = "Hi Vikas! 🌱 I'm your GrowLoop Assistant. I'm here to help you with sustainable fashion choices. How can I assist you today?",
                        isFromUser = false
                    )
                )
            } else {
                // Error message - but no offline functionality
                Log.e("ChatViewModel", "API initialization failed")
                messages.add(
                    ChatMessage(
                        text = "⚠️ Unable to connect to chat service. Please check your internet connection and try again.",
                        isFromUser = false
                    )
                )
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Don't allow sending if not initialized
        if (!isInitialized.value) {
            Log.e("ChatViewModel", "Cannot send message - API not initialized")
            messages.add(
                ChatMessage(
                    text = "⚠️ Chat service is not connected. Please restart the chat or check your connection.",
                    isFromUser = false
                )
            )
            return
        }

        val userMessage = ChatMessage(
            text = text,
            isFromUser = true,
            status = MessageStatus.SENDING
        )
        messages.add(userMessage)

        viewModelScope.launch {
            Log.d("ChatViewModel", "Sending message to Botpress API...")

            val success = apiService.sendMessage(text)
            Log.d("ChatViewModel", "Message send result: $success")

            val index = messages.indexOf(userMessage)
            if (index != -1) {
                messages[index] = userMessage.copy(
                    status = if (success) MessageStatus.SENT else MessageStatus.FAILED
                )
            }

            if (success) {
                // Fetch bot response
                fetchBotResponseWithPolling()
            } else {
                // Show error message if send fails
                messages.add(
                    ChatMessage(
                        text = "⚠️ Failed to send message. Please check your connection and try again.",
                        isFromUser = false
                    )
                )
            }
        }
    }

    private suspend fun fetchBotResponseWithPolling() {
        isTyping.value = true
        var newBotMessageFound = false

        // Poll up to 5 times (10 seconds total)
        for (attempt in 1..5) {
            try {
                Log.d("ChatViewModel", "Fetching bot response - attempt $attempt/5")
                val allMessages = apiService.getMessages()
                Log.d("ChatViewModel", "Fetched ${allMessages.size} total messages")

                val existingIds = messages.map { it.id }.toSet()
                val newBotMessages = allMessages.filter { msg ->
                    !msg.isFromUser && msg.id !in existingIds
                }

                if (newBotMessages.isNotEmpty()) {
                    Log.d("ChatViewModel", "✓ Found ${newBotMessages.size} new bot message(s)")
                    newBotMessages.forEach { botMessage ->
                        messages.add(botMessage)
                    }
                    newBotMessageFound = true
                    break // Exit loop once messages found
                } else {
                    Log.d("ChatViewModel", "No new messages yet, waiting...")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching bot response (attempt $attempt)", e)
            }

            // Wait 2 seconds before next attempt (except on last attempt)
            if (attempt < 5) {
                delay(2000)
            }
        }

        isTyping.value = false

        if (!newBotMessageFound) {
            Log.e("ChatViewModel", "✗ No bot response received after 5 attempts")
            messages.add(
                ChatMessage(
                    text = "⚠️ The bot didn't respond. This might be a connection issue or the bot needs more time. Try asking again.",
                    isFromUser = false
                )
            )
        }
    }

    // Retry connection function (optional - can be called from UI)
    fun retryConnection() {
        if (!isInitialized.value) {
            Log.d("ChatViewModel", "Retrying connection...")
            messages.clear()
            initializeBot()
        }
    }
}
