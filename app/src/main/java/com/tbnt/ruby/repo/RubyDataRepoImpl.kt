package com.tbnt.ruby.repo

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tbnt.ruby.*
import com.tbnt.ruby.repo.model.AudioBook
import com.tbnt.ruby.repo.model.FileType
import com.tbnt.ruby.repo.model.LanguageData
import com.tbnt.ruby.ui.mediaplayer.ProgressValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CopyOnWriteArrayList


private const val UPDATE_VERSION_KEY = "update_version_key"
private const val NEW_VERSION_DATA_KEY = "new_version_data_key"
private const val UPDATE_VERSION_FIELD = "update_version"
private const val DATA_VERSION = "1-0-0"
private const val PURCHASED_DATA_KEY = "purchased_data_key"
private const val ENG = "ENG"
private const val SIMPLE = "SampleAudiobooks"
private const val AUDIOBOOKS = "Audiobooks"
private const val FEEDBACKS = "Feedbacks"

class RubyDataRepoImpl(
    private val prefs: PreferencesService,
    private val gson: Gson,
    private val languageCode: String = ENG,
    private val filePath: File
) : RubyDataRepo {

    private val inProgressFiles = CopyOnWriteArrayList<String>()
    private var fullAudioDataStateCallback: (dataState: DataState) -> Unit = {}

    override fun storeData(
        snapshot: DataSnapshot,
        dataStateCallback: (dataState: DataState) -> Unit
    ) = flow {
        val previousVersion = prefs.preference(UPDATE_VERSION_KEY, 0)
        val newVersion: Int =
            (snapshot.getValue(true) as? Map<*, *>)?.get(UPDATE_VERSION_FIELD) as? Int ?: 1
        if (newVersion > previousVersion) {
            val dataJson = gson.toJson(((snapshot.getValue(false) as Map<*, *>)[DATA_VERSION]))
            prefs.putPreferences(NEW_VERSION_DATA_KEY, dataJson.toString())
            prefs.putPreferences(UPDATE_VERSION_KEY, newVersion)
            downloadSimpleAudios(dataStateCallback)
            emit(true)//data ready
        }
    }

    private fun downloadSimpleAudios(dataStateCallback: (dataState: DataState) -> Unit) {
        val jsonString = prefs.preference(NEW_VERSION_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)

        productionHashMap.forEach { (languageKey, languageData) ->
            crateAudioFolders(languageKey)
            languageData.audioBooks.forEach { audioBook ->
                inProgressFiles.add(generateFileId(audioBook.id, audioBook.sampleAudioFileName))
                downloadAndStoreSimpleAudioFile(
                    languageKey,
                    audioBook.sampleAudioFileName.toMp3Format(),
                    filePath.absolutePath + File.separator + SIMPLE + File.separator + languageKey
                ).addOnProgressListener {
                    val progress: Float = calculateProgress(it)
                    if (progress <= 0.0f) return@addOnProgressListener
                    dataStateCallback(
                        DataState(
                            DataStatus.Progress,
                            generateFileId(audioBook.id, audioBook.sampleAudioFileName),
                            if (progress <= 0.0f) ProgressValues.PROGRESS_START.value else progress
                        )
                    )
                }.addOnFailureListener {
                    dataStateCallback(
                        DataState(
                            DataStatus.Failed,
                            generateFileId(audioBook.id, audioBook.sampleAudioFileName)
                        )
                    )
                }.addOnSuccessListener {
                    inProgressFiles.remove(
                        generateFileId(
                            audioBook.id,
                            audioBook.sampleAudioFileName
                        )
                    )
                    dataStateCallback(
                        DataState(
                            DataStatus.Progress,
                            generateFileId(audioBook.id, audioBook.sampleAudioFileName),
                            ProgressValues.PROGRESS_COMPLETE.value
                        )
                    )
                }
            }
        }
    }

    private fun downloadAndStoreSimpleAudioFile(
        languageKey: String,
        fileName: String,
        exportFilePath: String,
    ): FileDownloadTask {
        val storageRef = Firebase.storage.reference.child(SIMPLE)
            .child(languageKey).child(fileName)
        return storageRef.getFile(File(exportFilePath, fileName))
    }

    private fun downloadAndStoreFullAudioFile(
        languageKey: String,
        packageId: String,
        fileName: String,
        exportFilePath: String,
    ): FileDownloadTask {
        val storageRef = Firebase.storage.reference.child(AUDIOBOOKS)
            .child(languageKey).child(packageId).child(fileName)
        return storageRef.getFile(File(exportFilePath, fileName))
    }

    private fun crateAudioFolders(langCode: String) {
        val langFolderSimple =
            File(filePath.absolutePath + File.separator + SIMPLE + File.separator + langCode)
        if (!langFolderSimple.exists()) {
            langFolderSimple.mkdirs()
        }
    }

    override suspend fun getData(): LanguageData? {
        val jsonString = prefs.preference(NEW_VERSION_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)
        return productionHashMap[languageCode.supportingLanCode()]
    }

    override suspend fun storePurchasedData(id: List<String>) {
        val jsonString = prefs.preference(NEW_VERSION_DATA_KEY, "{}")
        val productionHashMap = gson.fromJson<Map<String, LanguageData>?>(
            jsonString,
            object : TypeToken<Map<String, LanguageData>>() {}.type
        ).toMutableMap()

        val purchaseIds = getPurchasedIds()
        productionHashMap.forEach { (languageKey, languageData) ->
            val finalList = mutableListOf<AudioBook>()
            addCurrentPurchase(id, languageData, finalList)
            addPurchasedBooks(purchaseIds, languageData, finalList)
            productionHashMap[languageKey] =
                LanguageData(finalList.distinct(), emptyList(), languageData.settingsInfo)
        }
        prefs.putPreferences(PURCHASED_DATA_KEY, gson.toJson(productionHashMap))
    }

    override fun downloadCurrentFile(
        fileType: FileType,
        uri: Uri,
        fileName: String,
        packageId: String,
        audioChosenLanguage: String,
        dataStateCallback: (dataState: DataState) -> Unit
    ) {
        when (fileType) {
            FileType.SIMPLE -> {
                downloadAndStoreSimpleAudioFile(
                    languageCode.supportingLanCode(),
                    fileName.toMp3Format(),
                    filePath.absolutePath + File.separator + SIMPLE + File.separator + languageCode.supportingLanCode()
                )
            }
            FileType.FULL -> {
                val langFolder =
                    File(filePath.absolutePath + File.separator + AUDIOBOOKS + File.separator + audioChosenLanguage + File.separator + packageId)
                downloadAndStoreFullAudioFile(
                    languageCode.supportingLanCode(),
                    packageId,
                    fileName.toMp3Format(),
                    langFolder.absolutePath
                )
            }
        }.addOnProgressListener { task ->
            val progress: Float = calculateProgress(task)
            dataStateCallback(
                DataState(
                    DataStatus.Progress,
                    generateFileId(packageId, fileName),
                    if (progress <= 0.0f) ProgressValues.PROGRESS_START.value else progress
                )
            )
        }.addOnFailureListener {
            dataStateCallback(
                DataState(
                    DataStatus.Failed,
                    generateFileId(packageId, fileName)
                )
            )
        }.addOnSuccessListener {
            dataStateCallback(
                DataState(
                    DataStatus.Progress,
                    generateFileId(packageId, fileName),
                    ProgressValues.PROGRESS_COMPLETE.value
                )
            )
        }
    }

    override suspend fun downloadPackage(packageId: String, langCode: String) {
        val langFolder =
            File(filePath.absolutePath + File.separator + AUDIOBOOKS + File.separator + langCode + File.separator + packageId)
        if (!langFolder.exists()) {
            langFolder.mkdirs()
        }

        getData()?.let { apiModel ->
            apiModel.audioBooks.find { it.id == packageId }?.run {
                subpackage.forEach { subpackage ->
                    inProgressFiles.add(
                        generateFileId(
                            packageId,
                            subpackage.audioFileName
                        )
                    )
                    downloadAndStoreFullAudioFile(
                        langCode,
                        packageId,
                        subpackage.audioFileName.toMp3Format(),
                        langFolder.absolutePath
                    ).addOnProgressListener { task ->
                        val progress: Float = calculateProgress(task)
                        if (progress <= 0.0f) return@addOnProgressListener
                        fullAudioDataStateCallback(
                            DataState(
                                DataStatus.Progress,
                                generateFileId(packageId, subpackage.audioFileName),
                                if (progress <= 0.0f) ProgressValues.PROGRESS_START.value else progress
                            )
                        )
                    }.addOnFailureListener {
                        fullAudioDataStateCallback(
                            DataState(
                                DataStatus.Failed,
                                generateFileId(packageId, subpackage.audioFileName)
                            )
                        )
                    }.addOnSuccessListener {
                        inProgressFiles.remove(generateFileId(packageId, subpackage.audioFileName))
                        fullAudioDataStateCallback(
                            DataState(
                                DataStatus.Progress,
                                generateFileId(packageId, subpackage.audioFileName),
                                ProgressValues.PROGRESS_COMPLETE.value
                            )
                        )
                    }
                }
            }
        }
    }

    override fun checkFileStatus(fileId: String, uri: Uri): Flow<DataState> = flow {
        emit(
            when {
                isFileValid(uri) -> DataState(
                    DataStatus.Progress,
                    fileId,
                    ProgressValues.PROGRESS_COMPLETE.value
                )
                inProgressFiles.contains(fileId) -> DataState(
                    DataStatus.Progress,
                    fileId,
                    ProgressValues.PROGRESS_START.value
                )
                else -> DataState(DataStatus.Failed, fileId)
            }
        )
    }.flowOn(Dispatchers.Default)


    private fun calculateProgress(
        task:
        FileDownloadTask.TaskSnapshot
    ) =
        (1.0f * task.bytesTransferred / task.totalByteCount).takeIf { it > 0.0f } ?: 0.0f

    override fun listenFullAudioState(dataStateCallback: (dataState: DataState) -> Unit) {
        fullAudioDataStateCallback = dataStateCallback
    }

    override suspend fun sendFeedback(
        packageId: String,
        rating: Float,
        feedback: String,
        successCallBack: () -> Unit
    ) {
        Firebase.storage.reference.child(FEEDBACKS).child(feedBackFileName())
            .putBytes(
                "Name $packageId\n Rating $rating \n Feedback $feedback".toByteArray(),
                StorageMetadata.Builder().setContentType("text/plain").build()
            ).addOnSuccessListener {
                successCallBack()
            }
    }

    private fun feedBackFileName(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss")
        val now: LocalDateTime = LocalDateTime.now()
        return dateFormatter.format(now)
    }

    override fun isLangPackageExist(langCode: String): Boolean {
        val langFolder =
            File(filePath.absolutePath + File.separator + AUDIOBOOKS + File.separator + langCode)
        return langFolder.exists()
    }

    override suspend fun deletePreviousLangPackage(langCode: String) {
        if (isLangPackageExist(langCode)) {
            val langFolder =
                File(filePath.absolutePath + File.separator + AUDIOBOOKS + File.separator + langCode)
            langFolder.deleteRecursively()
        }
    }

    override suspend fun gePurchasedData(): LanguageData? {
        val jsonString = prefs.preference(PURCHASED_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)
        return productionHashMap[languageCode.supportingLanCode()]
    }

    override suspend fun getPurchasedIds(): List<String> {
        return gson.fromJson<Map<String, LanguageData>?>(
            prefs.preference(PURCHASED_DATA_KEY, "{}"),
            object : TypeToken<Map<String, LanguageData>>() {}.type
        ).toMutableMap()[languageCode.supportingLanCode()]?.audioBooks?.map { it.id } ?: emptyList()
    }

    private fun addPurchasedBooks(
        purchaseIds: List<String>,
        languageData: LanguageData,
        finalList: MutableList<AudioBook>
    ) {
        purchaseIds.forEach { id ->
            languageData.audioBooks.find { it.id == id }?.let {
                finalList.add(it)
            }
        }
    }

    private fun addCurrentPurchase(
        id: List<String>,
        languageData: LanguageData,
        finalList: MutableList<AudioBook>
    ) {
        id.forEach { purchased ->
            languageData.audioBooks.forEach {
                if (purchased == it.id) {
                    finalList.add(it)
                }
            }
        }
    }
}