package com.tbnt.ruby.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.tbnt.ruby.PreferencesService
import com.tbnt.ruby.PreferencesServiceImpl
import com.tbnt.ruby.repo.RubyDataRepo
import com.tbnt.ruby.repo.RubyDataRepoImpl
import com.tbnt.ruby.ui.audiopreview.AudioPreviewViewModel
import com.tbnt.ruby.ui.audiosubpackages.AudioSubPackagesViewModel
import com.tbnt.ruby.ui.auidiobooks.AudioBooksViewModel
import com.tbnt.ruby.ui.mediaplayer.MediaPlayerViewModel
import com.tbnt.ruby.ui.myaudiobooks.MyAudioBooksViewModel
import com.tbnt.ruby.ui.profile.LanguageViewModel
import com.tbnt.ruby.ui.splash.SplashViewModel
import com.tbnt.ruby.ui.tips.TipsPageViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

fun appModule(context: Context) = module {

    factory<PreferencesService> {
        PreferencesServiceImpl(context, "${context.packageName}_preferences")
    }

    single {
        FirebaseDatabase.getInstance()
    }

    single<RubyDataRepo> {
        RubyDataRepoImpl(get(), Gson(), Locale.getDefault().isO3Language, context.filesDir)
    }

    viewModel {
        SplashViewModel(get())
    }

    viewModel {
        AudioBooksViewModel(get())
    }

    viewModel {
        AudioPreviewViewModel(get())
    }

    viewModel {
        TipsPageViewModel(get())
    }

    viewModel {
        MyAudioBooksViewModel(get())
    }

    viewModel {
        AudioSubPackagesViewModel(get())
    }

    viewModel {
        MediaPlayerViewModel(get())
    }

    viewModel {
        LanguageViewModel()
    }
}