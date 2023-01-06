package com.itmo.highload.redis.listener

interface MessageListener {
    fun messageReceived(message: String)
}