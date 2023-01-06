package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.ComponentScan
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication(exclude = [R2dbcAutoConfiguration::class])
@ComponentScan(basePackages = ["com.itmo.highload.authservice", "com.itmo.highload.feign"])
@EnableReactiveFeignClients
@EnableEurekaClient
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
