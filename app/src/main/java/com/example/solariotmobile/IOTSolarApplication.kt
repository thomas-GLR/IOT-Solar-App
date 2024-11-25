package com.example.solariotmobile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.solariotmobile.api.RetrofitProvider
import com.example.solariotmobile.ui.settings.SettingRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "setting"
)

class IOTSolarApplication: Application() {

    lateinit var settingRepository: SettingRepository
    lateinit var retrofitProvider: RetrofitProvider

    override fun onCreate() {
        super.onCreate()
        settingRepository = SettingRepository(dataStore)
        retrofitProvider = RetrofitProvider(settingRepository)
    }
}