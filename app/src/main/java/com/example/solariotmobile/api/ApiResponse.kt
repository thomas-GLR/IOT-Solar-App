package com.example.solariotmobile.api

data class ApiResponse<T> (
    var message: String,
    var loading: Boolean,
    var failure: Boolean,
    var data: T) {
}