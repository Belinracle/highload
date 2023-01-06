package com.itmo.highload.notificationservice.handshake

import java.security.Principal

class StompClientPrincipal(
    private val username: String
) : Principal {
    override fun getName(): String {
        return username
    }
}