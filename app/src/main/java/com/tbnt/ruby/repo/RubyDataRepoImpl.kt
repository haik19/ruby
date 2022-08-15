package com.tbnt.ruby.repo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tbnt.ruby.PreferencesService
import com.tbnt.ruby.repo.model.LanguageData
import org.json.JSONObject
import kotlin.collections.HashMap


private const val UPDATE_VERSION_KEY = "update_version_key"
private const val NEW_VERSION_DATA_KEY = "new_version_data_key"
private const val UPDATE_VERSION_FIELD = "update_version"
private const val DATA_VERSION = "1-0-0"

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
            val dataJson =
                JSONObject(((snapshot.getValue(false) as Map<*, *>)[DATA_VERSION] as HashMap<*, *>))
            prefs.putPreferences(NEW_VERSION_DATA_KEY, dataJson.toString())
        }
    }

    override suspend fun getData(): LanguageData? {
        val jsonString = prefs.preference(NEW_VERSION_DATA_KEY, "{}")
        val productionHashMap: Map<String, LanguageData> = gson.fromJson(jsonString, object :
            TypeToken<Map<String, LanguageData>>() {}.type)
        return productionHashMap[languageCode.uppercase()]
    }
}