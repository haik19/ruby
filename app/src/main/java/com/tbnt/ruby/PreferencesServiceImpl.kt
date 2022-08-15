package com.tbnt.ruby

import android.content.Context

class PreferencesServiceImpl(
    context: Context,
    private val preferencesFilePath: String
) : PreferencesService {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(preferencesFilePath, Context.MODE_PRIVATE)
    }

    override fun <T> preference(key: String, default: T): T = sharedPreferences.run {
        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY") when (default) {
            is Boolean -> getBoolean(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Float -> getFloat(key, default)
            is Long -> getLong(key, default)
            is Set<*> -> getStringSet(key, default as Set<String>)
            else -> throw IllegalArgumentException("value type is not supported")
        } as T
    }

    override fun <T> putPreferences(key: String, any: T) {
        @Suppress("UNCHECKED_CAST") with(sharedPreferences.edit()) {
            when (any) {
                is Boolean -> putBoolean(key, any)
                is String -> putString(key, any)
                is Int -> putInt(key, any)
                is Float -> putFloat(key, any)
                is Long -> putLong(key, any)
                is Set<*> -> putStringSet(key, any as Set<String>)
                else -> throw IllegalArgumentException("value type is not supported")
            }.apply()
        }
    }
}