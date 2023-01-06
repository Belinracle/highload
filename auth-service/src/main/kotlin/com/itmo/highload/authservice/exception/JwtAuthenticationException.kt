package com.itmo.highload.authservice.exception

import org.springframework.http.HttpStatus


class JwtAuthenticationException(
    private val msg: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) :
    AuthenticationException(msg) {
}