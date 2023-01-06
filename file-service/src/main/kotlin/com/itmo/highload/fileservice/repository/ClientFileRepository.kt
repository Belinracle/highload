package com.itmo.highload.fileservice.repository

import com.itmo.highload.fileservice.model.ClientFile
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ClientFileRepository : ReactiveCrudRepository<ClientFile, Long> {
    fun findByFilename(filename: Mono<String>): Flux<ClientFile>
    fun findByOwner(owner: String): Flux<ClientFile>
}