package com.itmo.highload.userservice.controller

import com.itmo.highload.userservice.model.User
import com.itmo.highload.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import com.itmo.highload.feign.dto.UserDTO

@RestController
@RequestMapping("/api/user")
class UserController(val userService: UserService) {

    @PostMapping("/createUser")
    fun createUser(@RequestBody user: User): ResponseEntity<Any> { //можно в одну строчку ведь через =
        return ResponseEntity.ok(userService.create(user))
    }

    @PostMapping("/deleteUser")
    fun deleteUser(@RequestParam id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.remove(id))
    }

    @GetMapping("/{email}")
    fun getUser(@PathVariable email: String): ResponseEntity<Any> {
        val user = userService.findByEmail(email)
        val response = UserDTO(
            id = user.id ?: -1,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            status = user.status.toString(),
            roles = user.role.toString(),
            password = user.password
        )
        return ResponseEntity.ok(response)
    }

}
