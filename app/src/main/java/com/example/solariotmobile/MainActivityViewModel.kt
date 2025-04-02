package com.example.solariotmobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.repository.SettingRepository
import com.example.solariotmobile.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val settingRepository: SettingRepository) :
    ViewModel() {
        fun deleteToken() {
            viewModelScope.launch {
                settingRepository.clearToken()
            }
        }
}