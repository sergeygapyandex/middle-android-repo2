package ru.yandex.praktikumchatapp.data

class ChatRepository(
    private val api: ChatApi = ChatApi(),
) {

    fun getReplyMessage() = api.getReply()
}
