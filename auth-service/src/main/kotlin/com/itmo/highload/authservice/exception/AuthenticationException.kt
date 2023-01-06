package com.itmo.highload.authservice.exception

open class AuthenticationException(reason: String) : RuntimeException(reason)