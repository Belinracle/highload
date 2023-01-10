package com.itmo.highload.redis.listener

import org.springframework.data.redis.listener.ChannelTopic

interface MessageListener {
    fun getListenableTopic(): ChannelTopic
    fun messageReceived(message: String)
}