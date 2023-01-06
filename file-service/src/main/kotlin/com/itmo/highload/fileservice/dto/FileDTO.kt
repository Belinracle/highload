package com.itmo.highload.fileservice.dto

import com.itmo.highload.fileservice.model.ClientFile
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
) : Serializable {
    fun toClientFile() = ClientFile(
        description = description,
        filename = filename,
        size = size,
        title = title,
        owner = owner,
    )
}