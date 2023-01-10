package com.itmo.highload.fileservice.controller

import com.itmo.highload.fileservice.service.ZipService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/zip")
class ZipController(val zipService: ZipService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/prepare")
    fun prepare(
        @RequestParam("owner") owner: String,
        @RequestParam("zipname") zipname: String
    ): ResponseEntity<String> {
        zipService.createZip(owner, zipname)
        logger.info{ "запрос на создание зипа обработан"}
        return ResponseEntity.ok("все ок")
    }

}