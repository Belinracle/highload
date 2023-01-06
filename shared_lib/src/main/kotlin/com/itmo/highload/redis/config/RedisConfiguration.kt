package com.itmo.highload.redis.config

import com.itmo.highload.redis.listener.BaseMessageListener
import com.itmo.highload.redis.listener.MessageListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer

@Configuration
class RedisConfiguration(
    @Value("\${spring.redis.topic}")
    var redisTopic: String

) {

    @Bean
    fun messageListener(): MessageListener {
        return BaseMessageListener()
    }

    @Bean
    fun topic(): ChannelTopic? {
        return ChannelTopic(redisTopic)
    }

    @Bean
    fun container(
        factory: ReactiveRedisConnectionFactory,
        messageListener: MessageListener
    ): ReactiveRedisMessageListenerContainer? {
        val container = ReactiveRedisMessageListenerContainer(factory)
        container.receive(topic())
            .subscribe { messageListener.messageReceived(it.message) }
        return container
    }
}