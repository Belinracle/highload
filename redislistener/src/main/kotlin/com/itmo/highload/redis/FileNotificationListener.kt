package com.itmo.highload.redis

import com.google.gson.Gson
import com.itmo.highload.feign.client.NotificationClient
import com.itmo.highload.notifications.dto.Notification
import com.itmo.highload.redis.listener.MessageListener
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.stereotype.Component

@Component
@Primary
class FileNotificationListener(
    val notificationClient: NotificationClient
) : MessageListener {
    private val logger = KotlinLogging.logger {}
    val gson = Gson()

    override fun messageReceived(message: String) {
        logger.info { "handling message $message" }
        val notification = gson.fromJson(message, Notification::class.java)
        notificationClient.sendNotification(notification.destination, message);
    }

    @Bean
    fun http(): HttpMessageConverters {
        return HttpMessageConverters(GsonHttpMessageConverter())
    }

}