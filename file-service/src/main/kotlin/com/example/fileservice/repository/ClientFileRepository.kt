package com.example.fileservice.repository

import com.example.fileservice.model.ClientFile
import org.springframework.data.repository.CrudRepository

interface ClientFileRepository : CrudRepository<ClientFile, Long> {
    fun findByFilename(filename: String): Collection<ClientFile>
    fun findByUserEmail(userEmail: String): Collection<ClientFile>
}