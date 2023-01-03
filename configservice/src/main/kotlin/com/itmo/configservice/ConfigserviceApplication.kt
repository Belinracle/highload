package com.itmo.configservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
class ConfigserviceApplication

fun main(args: Array<String>) {
    runApplication<ConfigserviceApplication>(*args)
}
