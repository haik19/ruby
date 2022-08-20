package com.tbnt.ruby.entity

data class AudioPreviewEntity(
    val audioId: String,
    val imageUrl: String,
    val title: String,
    val audioFilesCount: Int,
    val rating: Float,
    val duration: String,
    val desc: String,
    val tips: String,
    val isPurchased: Boolean
)