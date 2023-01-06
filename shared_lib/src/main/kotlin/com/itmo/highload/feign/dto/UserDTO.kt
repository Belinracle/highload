package com.itmo.highload.feign.dto

data class UserDTO (
    val id: Long,
    var email: String,
    var password: String,
    val firstName: String,
    var lastName: String,
    val roles: String,
    var status: String
)