package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.data.web.config.EnableSpringDataWebSupport

@SpringBootApplication(scanBasePackages = ["com.itmo.highload"])
@EnableSpringDataWebSupport
@EnableEurekaClient
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}
