package com.tbnt.ruby

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import com.tbnt.ruby.ui.profile.LANGUAGE_CODE_KEY
import java.io.File
import java.util.*

private val supportedLanguages = listOf("eng", "rus")

fun String.supportingLanCode() =
    (if (supportedLanguages.contains(this)) this else "eng").uppercase()

fun isFileValid(uri: Uri) = File(uri.path.orEmpty()).length() != 0L

fun generateFileId(packageId: String, fileName: String) = packageId.plus(fileName)

fun String.toMp3Format() = plus(".mp3")

fun chosenLanguage(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
    .getString(LANGUAGE_CODE_KEY, Locale.getDefault().isO3Language.supportingLanCode()).orEmpty()
