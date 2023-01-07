package com.itmo.highload.notificationservice.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.user.SimpUser
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Service

@Service
class NotificationService(
    val simpMessagingTemplate: SimpMessagingTemplate,
    val simpUserRegistry: SimpUserRegistry,
    val accumulator: NotificationAccumulator
) {
    fun handleNewNotification(username: String, notification: String): String {
        return if (getConnectedUsers().map { simpUser -> simpUser.name }
                .contains(username)) {
            simpMessagingTemplate.convertAndSendToUser(
                username,
                "queue/notifications",
                notification
            )
            "Successfully sent message to $username"
        } else {
            accumulator.cacheNotification(username, notification)
            "User $username is currently not connected. Cached notification"
        }
    }

    fun getConnectedUsers(): Set<SimpUser> {
        return simpUserRegistry.users
    }

    fun getStoredNotifications(username: String): List<String> {
        return accumulator.getAndClear(username) ?: emptyList()
    }
}