package com.itmo.highload.authservice.controller

import com.itmo.highload.authservice.dto.AuthenticationRequestDTO
import com.itmo.highload.authservice.dto.AuthenticationResponseDTO
import com.itmo.highload.authservice.dto.RefreshTokenResponse
import com.itmo.highload.authservice.exception.TokenRefreshException
import com.itmo.highload.feign.dto.UserDTO
import com.itmo.highload.authservice.security.JwtTokenProvider
import com.itmo.highload.authservice.service.AuthService
import com.itmo.highload.authservice.service.RefreshTokenService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/login")
    fun authenticate(@RequestBody request: AuthenticationRequestDTO): Mono<AuthenticationResponseDTO> {
        logger.info { "handling login request" }
        return authService.authenticate(request)
    }

    @PostMapping("/register")
    fun register(@RequestBody user: UserDTO): Mono<AuthenticationResponseDTO> {
        logger.info { "handling register request" }
        return authService.register(user)
    }

    @PostMapping("/parseToken")
    fun validateToken(@RequestBody token: String): Mono<UserDTO> {
        logger.info { "handling validate token request" }
        jwtTokenProvider.validateToken(token)
        val email = jwtTokenProvider.getUsername(token)

        return authService.getUserByEmail(email!!)
    }

    @PostMapping("/refreshToken")
    fun refreshToken(@RequestBody refreshToken: String): Mono<ResponseEntity<RefreshTokenResponse>> {
        logger.info { "handling refresh token request" }
        return refreshTokenService.findByToken(refreshToken)
            .next()
            .map { token -> refreshTokenService.verifyExpiration(token) }
            .map { token -> token.userEmail }
            .flatMap { userEmail -> authService.getUserByEmail(userEmail) }
            .map { userDTO ->
                ResponseEntity.ok(
                    RefreshTokenResponse(
                        refreshToken,
                        jwtTokenProvider.createToken(userDTO)
                    )
                )
            }
            .doOnError { exception -> throw TokenRefreshException("Something went wrong with refreshing refreshToken + ${exception.message}") }
    }

}
