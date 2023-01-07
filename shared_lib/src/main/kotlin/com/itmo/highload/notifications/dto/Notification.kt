package com.itmo.highload.notifications.dto

data class Notification<T>(
    val destination: String,
    val notificationType: NotificationType,
    val time: Long, //UTC
    val body: T
)