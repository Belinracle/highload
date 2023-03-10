package com.itmo.highload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
@SpringBootApplication(
    scanBasePackages = ["com.itmo.highload.redis","com.itmo.highload.fileservice"],
    exclude = [R2dbcAutoConfiguration::class]
)
@EnableEurekaClient
class FileServiceApplication

fun main(args: Array<String>) {
    runApplication<FileServiceApplication>(*args)
}
