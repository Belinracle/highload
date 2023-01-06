package com.itmo.highload.authservice.exception

class TokenRefreshException(reason: String) : RuntimeException(reason)