package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->

                    val currentMessages = _messages.value ?: emptyList()
                    _messages.value =
                        currentMessages + Message.OtherMessage(response)

                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        val currentMessages = _messages.value ?: emptyList()
        _messages.value = currentMessages + Message.MyMessage(messageText)
    }
}