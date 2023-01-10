package com.itmo.highload.redis.publisher

import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisPublisher(
    var reactiveTemplate: ReactiveRedisTemplate<String, String>
) {
    private val logger = KotlinLogging.logger {}

    fun publish(topic: String, message: String): Mono<Long> {
        logger.info { "Publishing message to Redis topic $topic" }
        return reactiveTemplate.convertAndSend(topic, message)
    }
}