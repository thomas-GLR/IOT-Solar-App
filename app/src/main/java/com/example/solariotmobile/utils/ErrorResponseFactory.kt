package com.example.solariotmobile.utils

import okhttp3.ResponseBody

class ErrorResponseFactory {
    companion object {
        fun createErrorMessage(code: Int, responseBody: ResponseBody?): String {
            val errorMessage = if (responseBody != null) responseBody.string() else "aucun message renvoyé"
            return when (code) {
                401 -> "[$code] Les identifiants sont incorrects"
                else -> "Une erreur est survenue {code : $code} {message : $errorMessage}"
            }
        }
    }
}