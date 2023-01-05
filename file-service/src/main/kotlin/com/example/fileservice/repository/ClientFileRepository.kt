package com.example.fileservice.repository

import com.example.fileservice.model.ClientFile
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ClientFileRepository : ReactiveCrudRepository<ClientFile, Long> {
    fun findByFilename(filename: Mono<String>): Flux<ClientFile>
    fun findByOwner(owner: String): Flux<ClientFile>
}