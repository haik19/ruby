package com.tbnt.ruby


data class DataState(val status: DataStatus, val id: String = "", val progress: Float = 0.0f)

sealed interface DataStatus {
    object Progress : DataStatus
    object Failed : DataStatus
}

