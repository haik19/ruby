package com.tbnt.ruby.repo

import com.google.firebase.database.DataSnapshot
import com.tbnt.ruby.repo.model.LanguageData

interface RubyDataRepo {
    suspend fun storeData(snapshot: DataSnapshot)
    suspend fun getData(): LanguageData?
}