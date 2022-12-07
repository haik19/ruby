package com.tbnt.ruby.repo

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.tbnt.ruby.DataState
import com.tbnt.ruby.repo.model.FileType
import com.tbnt.ruby.repo.model.LanguageData
import kotlinx.coroutines.flow.Flow

interface RubyDataRepo {
    fun storeData(
        snapshot: DataSnapshot,
        dataStateCallback: (dataState: DataState) -> Unit
    ): Flow<Boolean>

    suspend fun getData(): LanguageData?
    suspend fun gePurchasedData(): LanguageData?
    suspend fun getPurchasedIds(): List<String>
    suspend fun storePurchasedData(id: List<String>)
    fun downloadCurrentFile(
        fileType: FileType,
        uri: Uri,
        fileName: String,
        packageId: String,
        audioChosenLanguage: String,
        dataStateCallback: (dataState: DataState) -> Unit
    )

    suspend fun downloadPackage(packageId: String, langCode: String)
    fun checkFileStatus(fileId: String, uri: Uri): Flow<DataState>
    fun listenFullAudioState(
        dataStateCallback: (dataState: DataState) -> Unit
    )

    suspend fun sendFeedback(
        packageId: String,
        rating: Float,
        feedback: String,
        successCallBack: () -> Unit
    )

    fun isLangPackageExist(langCode: String): Boolean
    suspend fun deletePreviousLangPackage(langCode: String)
}