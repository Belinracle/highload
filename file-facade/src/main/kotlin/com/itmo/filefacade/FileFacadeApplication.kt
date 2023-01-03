package com.itmo.filefacade

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication(scanBasePackages = ["com.itmo.highload"])
@EnableEurekaClient
@EnableReactiveFeignClients
class FileFacadeApplication

fun main(args: Array<String>) {
    runApplication<FileFacadeApplication>(*args)
}
