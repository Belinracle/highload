package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackages = ["com.itmo.highload.redis","com.itmo.highload.feign"])
@EnableFeignClients
@EnableEurekaClient
class RedisApplication

fun main(args: Array<String>) {
    runApplication<RedisApplication>(*args)
}
