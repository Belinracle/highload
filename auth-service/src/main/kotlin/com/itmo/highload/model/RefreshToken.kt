package com.itmo.highload.model

import org.springframework.data.annotation.Id
import java.time.Instant

data class RefreshToken(
    @Id
    private val id: Long? = null,
    val token: String,
    val userEmail: String,
    val expiryDate: Instant
)