package com.itmo.highload.notifications.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.io.Serializable


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FileDto(
    val id: Long? = null,
    val owner: String? = null,
    val filename: String? = null,
    val size: Long? = null,
    val title: String? = null,
    val description: String? = null
) : Serializable