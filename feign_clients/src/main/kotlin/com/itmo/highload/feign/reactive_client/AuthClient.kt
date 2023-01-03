package com.itmo.highload.feign.reactive_client

import com.itmo.highload.feign.dto.UserDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "\${auth.service.app.name}", path = "\${auth.service.context.path}")
interface AuthClient {
    @PostMapping("/parseToken")
    fun validateToken(@RequestBody token: String): Mono<UserDTO>
}