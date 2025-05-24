package com.example.solariotmobile.utils

import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    fun numberToFranceFormat(number: Int): String {
        val formatteur = NumberFormat.getInstance(Locale.FRANCE)
        return formatteur.format(number)
    }
}