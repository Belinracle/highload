package com.itmo.highload.authservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table
data class RefreshToken(
    @Id
    private val id: Long? = null,
    val token: String,
    val userEmail: String,
    val expiryDate: Instant
)