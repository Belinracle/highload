package com.itmo.highload.notificationservice.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.user.SimpUser
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Service

@Service
class NotificationService(
    val simpMessagingTemplate: SimpMessagingTemplate,
    val simpUserRegistry: SimpUserRegistry
) {
    fun handleNewNotification(username: String, notification: String): String {
        return try {
            simpMessagingTemplate.convertAndSendToUser(
                username,
                "queue/notifications",
                notification
            )
            "Successfully sent message to $username"
        } catch (e: Exception) {
            "Error while sending notification to $username with error $e"
        }
    }

    fun getConnectedUsers():Set<SimpUser>{
        return simpUserRegistry.users
    }
}