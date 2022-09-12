package com.tbnt.ruby.entity

data class AudioPlayerEntity(
    val imageUrl: String,
    val title: String,
    val simpleAudioName: String = "",
    val fullAudioName: String = ""
)