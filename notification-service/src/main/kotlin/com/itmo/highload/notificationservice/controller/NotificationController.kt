package com.itmo.highload.notificationservice.controller

import com.itmo.highload.notificationservice.service.NotificationService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notification")
class NotificationController(
    var notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/{username}")
    fun authenticate(
        @PathVariable username: String,
        @RequestBody notification: String
    ): String {
        logger.info { "handling sending notification to certain user $username" }
        return notificationService.handleNewNotification(username, notification)
    }

    @GetMapping("/allUsers")
    fun checkUsers(): String {
        return notificationService.getConnectedUsers().toString()
    }
}