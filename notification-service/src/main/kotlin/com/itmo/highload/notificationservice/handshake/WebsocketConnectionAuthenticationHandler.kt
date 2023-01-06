package com.itmo.highload.notificationservice.handshake

import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor


class WebsocketConnectionAuthenticationHandler : ChannelInterceptor {
    private val log = KotlinLogging.logger {}
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor: StompHeaderAccessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
        log.info { "connection headers " + accessor.messageHeaders }
        val headers: Map<String, List<String>> =
            accessor.getHeader(StompHeaderAccessor.NATIVE_HEADERS) as Map<String, List<String>>
        log.info { "user connecting in: ${headers["user"]?.get(0)}" }
        if (StompCommand.CONNECT == accessor.command) {
            accessor.user = headers["user"]?.get(0)?.let { StompClientPrincipal(it) };
        }
        return message;
    }
}