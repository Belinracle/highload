package com.example.fileservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.web.multipart.MultipartFile
import java.io.Serializable


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FileDto(
    val title: String? = null,
    val description: String? = null,
    val file: MultipartFile? = null,
    val url: String? = null,
    val size: Long? = null,
    val filename: String? = null
) : Serializable