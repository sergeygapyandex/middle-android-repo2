package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi(),
) {

    fun getReplyMessage(): Flow<String> {
        var currentDelay = INITIAL_DELAY

        return api.getReply().retryWhen { cause, attempt ->
            if (attempt < MAX_RETRIES && cause is Exception) {
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                true
            } else {
                false
            }
        }
    }

    private companion object {
        const val MAX_RETRIES = 3L
        const val INITIAL_DELAY = 1000L
        const val DELAY_FACTOR = 2L
    }
}
