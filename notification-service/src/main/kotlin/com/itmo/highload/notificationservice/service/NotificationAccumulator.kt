package com.itmo.highload.notificationservice.service

import org.springframework.stereotype.Component

@Component
class NotificationAccumulator {
    val cache: MutableMap<String, MutableList<String>> = mutableMapOf<String, MutableList<String>>()
    fun cacheNotification(username: String, notificationJson: String) {
        if (!cache.containsKey(username)) {
            cache[username] = mutableListOf()
        }
        cache[username]?.add(notificationJson)
    }

    fun getAndClear(username: String): List<String>? {
        return cache.remove(username)
    }
}