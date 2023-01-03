package com.itmo.highload.dto

data class RefreshTokenResponse(
    val refreshToken: String,
    val newJWTToken: String
)