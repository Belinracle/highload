package com.itmo.highload.exception

import com.itmo.highload.exception.AuthenticationException
import org.springframework.http.HttpStatus


class JwtAuthenticationException(private val msg: String, val status: HttpStatus = HttpStatus.BAD_REQUEST) :
    AuthenticationException(msg) {
}