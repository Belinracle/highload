package com.itmo.highload.authservice.dto

data class AuthenticationRequestDTO(
    val email: String? = null,
    val password: String? = null
)