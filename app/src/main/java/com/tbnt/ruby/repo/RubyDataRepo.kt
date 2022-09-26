package com.tbnt.ruby.repo

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.FileDownloadTask
import com.tbnt.ruby.repo.model.LanguageData
import kotlinx.coroutines.flow.Flow

interface RubyDataRepo {
    fun storeData(snapshot: DataSnapshot): Flow<Boolean>
    suspend fun getData(): LanguageData?
    suspend fun gePurchasedData(): LanguageData?
    suspend fun getPurchasedIds(): List<String>
    suspend fun storePurchasedData(id: String)
    fun downloaSimpledFile(uri: Uri, fileName: String): FileDownloadTask
    suspend fun downloadPackage(packageId: String, langCode: String)
}