package com.itmo.highload.authservice.service

import com.itmo.highload.authservice.exception.TokenRefreshException
import com.itmo.highload.authservice.model.RefreshToken
import com.itmo.highload.authservice.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.*


@Component
class RefreshTokenService(
    @Autowired
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${refresh.token.duration.ms}")
    private var refreshTokenDurationMs: Long,
) {
    fun createRefreshToken(userEmail: String): RefreshToken {
        val refreshToken = RefreshToken(
            null, UUID.randomUUID().toString(), userEmail,
            Instant.now().plusMillis(refreshTokenDurationMs)
        );

        return refreshToken
    }

    fun findByToken(refreshToken: String): Flux<RefreshToken> {
        return refreshTokenRepository.findByToken(refreshToken)
            ?: throw TokenRefreshException("Refresh token not found")
    }

    fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token).doOnNext { println("deleted") }.block()
            throw TokenRefreshException(
                token.token +
                        ": refresh token was expired. Please make a new signin request"
            )
        }
        return token
    }
}