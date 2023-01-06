package com.itmo.highload.feign.client

import com.itmo.highload.feign.dto.OrderDTO
import com.itmo.highload.feign.dto.OrderItemsDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@FeignClient(name = "order-client", url = "\${order.service.url}")
interface OrderClient {
    @GetMapping("/getOrders")
    fun getAllOrders(
        @RequestParam id: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<Page<OrderDTO>>

    @PostMapping("/createOrder")
    fun createOrder(@RequestBody order: OrderDTO): ResponseEntity<Any>

    @PostMapping("/updateOrder/{id}")
    fun updateOrder(@PathVariable id: Long, @RequestBody order: OrderDTO): ResponseEntity<Any>

    @PostMapping("/updateOrderItems")
    fun updateOrderItems(
        @RequestParam orderId: Long,
        @RequestParam medicationId: Long,
        @RequestBody orderItem: OrderItemsDTO
    ): ResponseEntity<Any>

}
