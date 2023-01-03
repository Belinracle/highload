package com.itmo.highload.dto

data class AuthenticationRequestDTO(
    val email: String? = null,
    val password: String? = null
)