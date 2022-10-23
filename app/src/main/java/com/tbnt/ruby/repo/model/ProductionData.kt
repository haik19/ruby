package com.tbnt.ruby.repo.model

import com.google.gson.annotations.SerializedName

data class LanguageData(
    @SerializedName("audiobooks") val audioBooks: List<AudioBook>,
    @SerializedName("tips") val tips: List<TipsItem>,
    @SerializedName("settings_info") val settingsInfo: SettingsInfo
)

data class SettingsInfo(
    @SerializedName("contact_mail") val contactEmail: String,
    @SerializedName("terms_and_privacy_url") val termsUrl: String
)

data class AudioBook(
    @SerializedName("id") val id: String,
    @SerializedName("audiofiles_count") val audioBooksCount: Int,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("name") val name: String,
    @SerializedName("rating_stars") val ratingStars: Float,
    @SerializedName("sample_audio_file_name") val sampleAudioFileName: String,
    @SerializedName("subpackage") val subpackage: List<Subpackage>,
    @SerializedName("tips") val tips: List<String>,
)

data class Subpackage(
    @SerializedName("audio_file_name") val audioFileName: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("name") val name: String
)


data class TipsItem(
    @SerializedName("name") val name: String,
    @SerializedName("tips") val tips: List<String>
)