package com.example.solariotmobile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.solariotmobile.api.RetrofitProvider
import com.example.solariotmobile.ui.command.CommandRepository
import com.example.solariotmobile.ui.settings.SettingRepository
import dagger.hilt.android.HiltAndroidApp

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "setting"
)

@HiltAndroidApp
class IOTSolarApplication: Application() {

    lateinit var settingRepository: SettingRepository
    lateinit var retrofitProvider: RetrofitProvider

    override fun onCreate() {
        super.onCreate()
        settingRepository = SettingRepository(dataStore)
        retrofitProvider = RetrofitProvider(settingRepository)
    }
}