package com.tbnt.ruby

interface PreferencesService {
    fun <T> putPreferences(key: String, any: T)
    fun <T> preference(key: String, default: T): T
    fun remove(key: String)
}