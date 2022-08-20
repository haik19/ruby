package com.tbnt.ruby.repo

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tbnt.ruby.PreferencesService
import com.tbnt.ruby.repo.model.AudioBook
import com.tbnt.ruby.repo.model.LanguageData


private const val UPDATE_VERSION_KEY = "update_version_key"
private const val NEW_VERSION_DATA_KEY = "new_version_data_key"
private const val UPDATE_VERSION_FIELD = "update_version"
private const val DATA_VERSION = "1-0-0"
private const val PURCHASED_DATA_KEY = "purchased_data_key"

class RubyDataRepoImpl(
    private val prefs: PreferencesService,
    private val gson: Gson,
    private val languageCode: String = "ENG"
) : RubyDataRepo {

    override suspend fun storeData(snapshot: DataSnapshot) {
        val previousVersion = prefs.preference(UPDATE_VERSION_KEY, 0)
        val newVersion: Int =
            (snapshot.getValue(true) as? Map<*, *>)?.get(UPDATE_VERSION_FIELD) as? Int ?: 1
        if (newVersion > previousVersion) {
            prefs.putPreferences(UPDATE_VERSION_KEY, newVersion)
            val dataJson = gson.toJson(((snapshot.getValue(false) as Map<*, *>)[DATA_VERSION]))
            prefs.putPreferences(NEW_VERSION_DATA_KEY, dataJson.toString())
        }
    }

    override suspend fun getData(): LanguageData? {
        val jsonString = prefs.preference(NEW_VERSION_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)
        return productionHashMap[languageCode.uppercase()]
    }

    override suspend fun storePurchasedData(id: String) {
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
            productionHashMap[languageKey] = LanguageData(finalList, emptyList())
        }
        prefs.putPreferences(PURCHASED_DATA_KEY, gson.toJson(productionHashMap))
        val stringJson = prefs.preference(PURCHASED_DATA_KEY, "{}")
        println("string json $stringJson")
    }

    override suspend fun gePurchasedData(langCode: String): LanguageData? {
        val jsonString = prefs.preference(PURCHASED_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)
        return productionHashMap[langCode]
    }

    override suspend fun getPurchasedIds(): List<String> {
        return gson.fromJson<Map<String, LanguageData>?>(
            prefs.preference(PURCHASED_DATA_KEY, "{}"),
            object : TypeToken<Map<String, LanguageData>>() {}.type
        ).toMutableMap()[languageCode.uppercase()]?.audioBooks?.map { it.id } ?: emptyList()
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
        id: String,
        languageData: LanguageData,
        finalList: MutableList<AudioBook>
    ) {
        languageData.audioBooks.forEach {
            if (it.id == id) {
                finalList.add(it)
            }
        }
    }
}