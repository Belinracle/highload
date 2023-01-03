package com.itmo.highload.feign.dto

class InfinityScrollResultWrapper<T>(
    val items: List<T>,
    val hasMore: Boolean) {
}
