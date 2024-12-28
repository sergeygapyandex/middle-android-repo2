package ru.yandex.praktikumchatapp.presentation

sealed class Message {
    data class MyMessage(val text: String) : Message()
    data class OtherMessage(val text: String) : Message()
}