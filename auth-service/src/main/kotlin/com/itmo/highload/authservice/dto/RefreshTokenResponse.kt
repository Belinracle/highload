package com.itmo.highload.authservice.dto

data class RefreshTokenResponse(
    val refreshToken: String,
    val newJWTToken: String
)