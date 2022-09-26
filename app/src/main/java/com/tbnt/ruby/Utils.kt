package com.tbnt.ruby

private val supportedLanguages = listOf("eng", "rus")

fun String.supportingLanCode() = (if (supportedLanguages.contains(this)) this else "eng").uppercase()