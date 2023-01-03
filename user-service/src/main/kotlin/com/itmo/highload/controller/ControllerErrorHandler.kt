package com.itmo.highload.controller

import com.itmo.highload.exceptions.UnableToRemoveSuperadminException
import com.itmo.highload.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerErrorHandler {

    @ExceptionHandler
    fun removeSuperUserвExceptionHandler(exception: UnableToRemoveSuperadminException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun userNotFoundException(exception: UserNotFoundException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }
}