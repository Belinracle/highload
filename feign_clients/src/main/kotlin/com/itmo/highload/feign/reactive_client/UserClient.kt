package com.itmo.highload.feign.reactive_client

import com.itmo.highload.feign.dto.UserDTO
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "\${user.service.app.name}", path = "\${user.service.context.path}")
@Component
interface UserClient {

    @PostMapping("/createUser")
    fun createUser(@RequestBody userDTO: UserDTO): Mono<UserDTO>;


    @GetMapping("/{email}")
    fun authenticate(@PathVariable email: String): Mono<UserDTO>
}