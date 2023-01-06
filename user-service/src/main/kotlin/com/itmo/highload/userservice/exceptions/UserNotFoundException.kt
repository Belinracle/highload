package com.itmo.highload.userservice.exceptions

//TODO можно сделать хорошую иеррахию ошибок ( если уж EDD ) для работы с ControllerAdvice
class UserNotFoundException(private val predicate: String): RuntimeException("User not found by $predicate") {
}
