package com.itmo.highload.fileservice.model

import com.itmo.highload.notifications.dto.FileDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "clientfile")
data class ClientFile(
    @Id
    var id: Long? = null,
    var owner: String? = null,
    var fileIndex: UUID? = null,
    var filename: String? = null,
    var size: Long? = null,
    var title: String? = null,
    var description: String? = null,
){
    fun toDTO() = FileDto(
        id = id,
        filename = filename,
        size = size,
        title = title,
        owner = owner,
        description = description
    )
}