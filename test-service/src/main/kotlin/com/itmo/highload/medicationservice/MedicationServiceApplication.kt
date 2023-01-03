package com.itmo.highload.medicationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackages = ["com.itmo.highload"])
@EnableEurekaClient
class MedicationServiceApplication
fun main(args: Array<String>) {
    runApplication<MedicationServiceApplication>(*args)
}
