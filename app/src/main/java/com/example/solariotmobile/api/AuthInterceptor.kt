package com.example.solariotmobile.api

import com.example.solariotmobile.ui.settings.SettingRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val settingRepository: SettingRepository
//    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { settingRepository.getToken() }
        val request = chain.request().newBuilder()

        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(request.build())
    }
}
