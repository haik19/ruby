package com.tbnt.ruby.entity

data class AudioPlayerEntity(
    val state: State = State.INITIAL,
    val imageUrl: String,
    val title: String,
    val simpleAudioName: String = "",
    val fullAudioName: String = ""
)

enum class State {
    INITIAL,
    RETRY
}