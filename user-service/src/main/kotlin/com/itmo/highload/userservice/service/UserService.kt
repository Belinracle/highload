package com.itmo.highload.userservice.service

import com.itmo.highload.userservice.exceptions.UserNotFoundException
import com.itmo.highload.userservice.jdbc.UserTemplate
import com.itmo.highload.userservice.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    val template: UserTemplate
) {

    fun getAll(): List<User>? = template.findAll();

    fun getById(id: Long): User {
        try {
            return template.findById(id) ?: throw UserNotFoundException("id: $id")
        } catch (ex: Exception) {
            throw UserNotFoundException("id: $id")
        }
    }

    fun findByEmail(email: String): User {
        try {
            return template.findByEmail(email) ?: throw UserNotFoundException("email: $email")
        } catch (ex: Exception) {
            throw UserNotFoundException("email: $email")
        }
    }

    fun create(employee: User) = template.insert(employee)

    fun remove(id: Long) = template.deleteById(id)
}
