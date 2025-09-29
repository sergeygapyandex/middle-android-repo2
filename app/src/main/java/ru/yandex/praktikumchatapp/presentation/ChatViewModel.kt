package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

@Suppress("SwallowedException", "TooGenericExceptionCaught")
class ChatViewModel(
    val isWithReplies: Boolean = true,
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private var consecutiveFailures = 0
    private val handler = CoroutineExceptionHandler { _, throwable ->
        println("Ошибка в корутине: $throwable")
    }

    init {
        viewModelScope.launch(handler) {
            while (isWithReplies) {
                try {
                    repository.getReplyMessage()
                        .catch { exception ->
                            consecutiveFailures++
                        }
                        .collect { response ->
                            consecutiveFailures = 0
                            _messages.update { oldValue ->
                                oldValue + Message.OtherMessage(response)
                            }
                        }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
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
        const val FALLBACK_DELAY = 10000L
    }
}
