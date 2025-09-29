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

@Suppress("SwallowedException", "TooGenericExceptionCaught")
class ChatViewModel(
    val isWithReplies: Boolean = true,
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                try {
                    repository.getReplyMessage()
                        .catch { exception ->
                            println("${exception.message}")
                        }
                        .collect { response ->
                            _messages.update { oldValue ->
                                oldValue + Message.OtherMessage(response)
                            }
                        }
                } catch (e: Exception) {
                    delay(DELAY)
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
        const val DELAY = 5000L
    }
}
