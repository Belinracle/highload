package com.itmo.highload.redis.listener

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class BaseMessageListener(
    @Value("\${spring.redis.topic}")
    var redisTopic: String
) : MessageListener {
    private val logger = KotlinLogging.logger {}
    override fun getListenableTopic(): ChannelTopic {
        return ChannelTopic(redisTopic)
    }

    override fun messageReceived(message: String) {
        logger.info { "received message from redis\n message: $message " }
    }
}