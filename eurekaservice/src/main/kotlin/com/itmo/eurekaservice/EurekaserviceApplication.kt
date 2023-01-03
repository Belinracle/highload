package com.itmo.eurekaservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaserviceApplication

fun main(args: Array<String>) {
    runApplication<EurekaserviceApplication>(*args)
}
