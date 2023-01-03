package com.itmo.highload.feign.dto

import java.util.*

class OrderDTO (
    val employeeId: Long?,
    val customerId: Long?,
    val date: Date? = null,
    val address: String? = null,
    val status: String,
)