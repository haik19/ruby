package com.tbnt.ruby.entity

data class AudioBook(
    val id: String,
    val imageUrl: String,
    val title: String,
    val price: String,
    val rating: Float,
    val isPurchased: Boolean,
    val isFree: Boolean,
)