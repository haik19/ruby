package com.tbnt.ruby.entity

sealed interface ResponseStatus {
    object Loading : ResponseStatus
    object Success : ResponseStatus
    object Failed : ResponseStatus
}