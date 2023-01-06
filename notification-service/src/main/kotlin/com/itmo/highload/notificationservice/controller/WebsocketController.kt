package com.itmo.highload.notificationservice.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller


@Controller
class WebsocketController {
    @MessageMapping("/ws")
    @SendTo("/queue/notifications")
    fun send(message: String): String {
        return "test"
    }
}