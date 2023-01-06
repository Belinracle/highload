package com.itmo.highload.redis.listener

import mu.KotlinLogging

class BaseMessageListener : MessageListener {
    private val logger = KotlinLogging.logger {}
    override fun messageReceived(message: String) {
        logger.info { "received message from redis\n message: $message " }
    }
}