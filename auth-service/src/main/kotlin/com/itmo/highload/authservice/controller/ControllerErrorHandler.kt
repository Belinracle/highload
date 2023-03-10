package com.itmo.highload.authservice.controller

import com.itmo.highload.authservice.exception.AuthenticationException
import com.itmo.highload.authservice.exception.ErrorMessageModel
import com.itmo.highload.authservice.exception.TokenRefreshException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerErrorHandler {

    @ExceptionHandler
    fun authenticationExceptionHandler(exception: AuthenticationException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            exception.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun refreshTokenException(exception: TokenRefreshException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            exception.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }
}