package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication(scanBasePackages = ["com.itmo.highload"], exclude = [R2dbcAutoConfiguration::class])
@EnableReactiveFeignClients
@EnableEurekaClient
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
