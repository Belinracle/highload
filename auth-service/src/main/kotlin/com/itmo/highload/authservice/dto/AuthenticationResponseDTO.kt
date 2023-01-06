package com.itmo.highload.authservice.dto

data class AuthenticationResponseDTO(
    val token: String,
    val refreshToken: String
)
