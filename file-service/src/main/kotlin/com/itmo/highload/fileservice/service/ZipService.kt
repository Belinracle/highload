package com.itmo.highload.fileservice.service

import com.itmo.highload.fileservice.repository.ClientFileRepository
import com.itmo.highload.redis.publisher.RedisPublisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.zip.ZipOutputStream
import kotlin.concurrent.thread

@Service
class ZipService(
    var minioService: MinioService,
    val clientFileRepository: ClientFileRepository,
    val redisPublisher: RedisPublisher,
) {
    private val logger = KotlinLogging.logger {}

    fun zipCreationThread(username: String)= runBlocking {
        withContext(Dispatchers.IO) {
            clientFileRepository.findByOwner(username).collectList().map { clientFiles ->
                logger.info { "fetched clientFiles $clientFiles" }
                val byteArrayOutputStream = ByteArrayOutputStream()
                val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
                ZipCreationTask(zipOutputStream, minioService, clientFiles)
            }.block()
        }?.execute()
    }
    fun createZip(username: String, zipName: String) = runBlocking{
        logger.info { "fetching files for client $username"}
            thread {
                zipCreationThread(username)
            }
        logger.info { "aaaaaaaaaaaaaaaaaaaaaaaaaa $username"}
        }
}