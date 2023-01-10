package com.itmo.highload.fileservice.controller

import com.itmo.highload.fileservice.service.ZipService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/zip")
class ZipController(val zipService: ZipService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/prepare")
    suspend fun prepare(
        @RequestParam("owner") owner: String,
        @RequestParam("zipname") zipname: String
    ): ResponseEntity<String>  = runBlocking{
        async { zipService.createZip(owner, zipname)}
        logger.info{ "запрос на создание зипа обработан"}
        return@runBlocking ResponseEntity.ok("все ок")
    }

}