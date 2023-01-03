package com.itmo.highload.feign.client

import com.itmo.highload.feign.dto.StorageDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "storage-client", url = "\${storage.service.url}")
interface StorageClient {

    @PostMapping("/registerStorage")
    fun registerStorage(@Validated @RequestBody storage: StorageDTO): ResponseEntity<Any>

}