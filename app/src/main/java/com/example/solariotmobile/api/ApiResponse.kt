package com.example.solariotmobile.api

interface ApiResponse<T> {
    val message: String
    val loading: Boolean
    val failure: Boolean
    val data: T
}