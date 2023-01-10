package com.itmo.highload.redis.config

import com.itmo.highload.redis.listener.MessageListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer

@Configuration
class RedisConfiguration {

    @Bean
    fun container(
        factory: ReactiveRedisConnectionFactory,
        messageListeners: List<MessageListener>
    ): ReactiveRedisMessageListenerContainer? {
        val container = ReactiveRedisMessageListenerContainer(factory)

        messageListeners.forEach { messageListener ->
            container.receive(messageListener.getListenableTopic()).subscribe {
                messageListener.messageReceived(it.message)
            }
        }
        return container
    }
}