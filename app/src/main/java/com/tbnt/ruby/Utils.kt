package com.tbnt.ruby

private val supportedLanguages = listOf("ENG", "RUS")

fun String.supportingLanCode() = if (supportedLanguages.contains(this)) this else "ENG"