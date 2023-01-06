package com.itmo.highload.authservice.repository

import com.itmo.highload.authservice.model.RefreshToken
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, Long> {

    @Query("SELECT * FROM refreshToken WHERE token = :token")
    fun findByToken(token: String?): Flux<RefreshToken>?
}