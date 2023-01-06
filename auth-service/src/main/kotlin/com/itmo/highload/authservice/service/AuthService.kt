package com.itmo.highload.authservice.service

import com.itmo.highload.authservice.dto.AuthenticationRequestDTO
import com.itmo.highload.authservice.dto.AuthenticationResponseDTO
import com.itmo.highload.authservice.exception.AuthenticationException
import com.itmo.highload.feign.dto.UserDTO
import com.itmo.highload.feign.reactive_client.UserClient
import com.itmo.highload.authservice.security.JwtTokenProvider
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.util.function.BiConsumer


@Service
class AuthService(
    val userClient: UserClient,
    val jwtTokenProvider: JwtTokenProvider,
    val refreshTokenService: RefreshTokenService
) {

    fun getUserByEmail(userEmail: String): Mono<UserDTO> {
        return userClient.authenticate(userEmail)
            .doOnError { exception -> throw AuthenticationException("Something went wrong with userClient + ${exception.message}") }
    }

    fun register(userDTO: UserDTO): Mono<AuthenticationResponseDTO> {
        return userClient.createUser(userDTO)
            .doOnError { exception -> throw AuthenticationException("Something went wrong with userClient + ${exception.message}") }
            .map {
                AuthenticationResponseDTO(
                    jwtTokenProvider.createToken(it),
                    refreshTokenService.createRefreshToken(it.email).token
                )
            }
            .doOnError { exception -> throw AuthenticationException("Error while generating AuthenticationResponse: ${exception.message}") }
    }

    fun authenticate(authenticationRequestDTO: AuthenticationRequestDTO): Mono<AuthenticationResponseDTO> {
        return userClient.authenticate(authenticationRequestDTO.email!!)
            .doOnError { exception -> throw AuthenticationException("Something went wrong with userClient + ${exception.message}") }
            .handle(BiConsumer<UserDTO, SynchronousSink<UserDTO>> { user, sink ->
                if (user.password != authenticationRequestDTO.password) {
                    sink.error(AuthenticationException("Password doesn't match"))
                } else {
                    sink.next(user)
                }
            })
            .map {
                AuthenticationResponseDTO(
                    jwtTokenProvider.createToken(it),
                    refreshTokenService.createRefreshToken(it.email).token
                )
            }
            .doOnError { exception -> throw AuthenticationException("Error while generating AuthenticationResponse: ${exception.message}") }
    }
}