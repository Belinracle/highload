package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.itmo.highload.redis"])
class RedisApplication

fun main(args: Array<String>) {
    runApplication<RedisApplication>(*args)
}
