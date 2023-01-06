package com.itmo.highload.redis.publisher

import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisPublisher(
    var channelTopic: ChannelTopic,
    var reactiveTemplate: ReactiveRedisTemplate<String, String>
) {
    private val logger = KotlinLogging.logger {}

    fun publish(message: String): Mono<Long> {
        logger.info { "Publishing message to Redis topic ${channelTopic.topic}" }
        return reactiveTemplate.convertAndSend(channelTopic.topic, message)
    }
}