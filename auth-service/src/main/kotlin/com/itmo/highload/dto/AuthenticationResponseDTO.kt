package com.itmo.highload.dto

data class AuthenticationResponseDTO(
    val token: String,
    val refreshToken: String
)
