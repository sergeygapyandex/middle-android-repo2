package ru.yandex.praktikumchatapp.data

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    suspend fun getReplyMessage(): String {
        return api.getReply()
    }
}