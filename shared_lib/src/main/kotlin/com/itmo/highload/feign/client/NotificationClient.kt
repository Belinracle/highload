package com.itmo.highload.feign.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "\${notification.service.app.name}",
    path = "\${notification.service.context.path}"
)
interface NotificationClient {

    @PostMapping("/user/{username}")
    fun sendNotification(
        @PathVariable("username") username: String,
        @RequestBody notification: String
    ): String
}