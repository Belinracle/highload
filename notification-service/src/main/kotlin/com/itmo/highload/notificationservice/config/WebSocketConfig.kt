package com.itmo.highload.notificationservice.config

import com.itmo.highload.notificationservice.handshake.WebsocketConnectionAuthenticationHandler
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/queue")
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/users")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(WebsocketConnectionAuthenticationHandler())
    }
}
