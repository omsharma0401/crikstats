package com.omsharma.playerstats.data.model

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val body: T
)