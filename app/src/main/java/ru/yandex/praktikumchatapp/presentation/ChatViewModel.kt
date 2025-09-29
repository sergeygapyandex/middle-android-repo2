package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository
import kotlin.math.pow

@Suppress("SwallowedException", "TooGenericExceptionCaught")
class ChatViewModel(
    val isWithReplies: Boolean = true,
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private var consecutiveFailures = 0

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                try {
                    repository.getReplyMessage()
                        .catch { exception ->
                            consecutiveFailures++
                            val multiplier = 2.0.pow((consecutiveFailures - 1).toDouble()).toLong()
                            val backoffDelay = minOf(
                                INITIAL_BACKOFF * multiplier,
                                MAX_BACKOFF
                            )
                            delay(backoffDelay)
                        }
                        .collect { response ->
                            consecutiveFailures = 0
                            _messages.update { oldValue ->
                                oldValue + Message.OtherMessage(response)
                            }
                        }
                } catch (e: Exception) {
                    consecutiveFailures++
                    delay(FALLBACK_DELAY)
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _messages.update { oldMessages ->
            oldMessages + Message.MyMessage(messageText)
        }
    }

    private companion object {
        const val INITIAL_BACKOFF = 2000L
        const val MAX_BACKOFF = 30000L
        const val FALLBACK_DELAY = 10000L
    }
}
