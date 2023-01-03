package com.itmo.highload.feign.dto

import org.jetbrains.annotations.NotNull

class OrderItemsDTO (
    @field:NotNull
    val amount: Int,
    var orderId: Long?,
    var medicationId: Long?,
)