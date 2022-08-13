package com.tbnt.ruby.entity

sealed interface Tips {
    data class Title(val text: String) : Tips
    data class Content(val text: String) : Tips
}