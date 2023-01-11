package com.itmo.highload.fileservice.service

import com.itmo.highload.fileservice.model.ClientFile
import com.itmo.highload.fileservice.repository.ClientFileRepository
import com.itmo.highload.redis.publisher.RedisPublisher
import io.minio.MinioAsyncClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipOutputStream

@Service
class ZipService(
    var minioService: MinioService,
    val clientFileRepository: ClientFileRepository,
    var minioAsyncClient: MinioAsyncClient,
    val redisPublisher: RedisPublisher,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun zipCreationThread(username: String, zipName: String) = coroutineScope {
        val clientFiles: List<ClientFile> =
            clientFileRepository.findByOwner(username).collectList().awaitSingle()
        logger.info { "fetched clientFiles $clientFiles" }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
        ZipCreationTask(zipOutputStream, minioService, clientFiles).execute()
        val fullFileName = "$zipName.zip"
        logger.info("writing zip file with baos ${byteArrayOutputStream.toByteArray().size}")
        minioAsyncClient.putObject(
            PutObjectArgs.builder().bucket("my-bucket").`object`(fullFileName).stream(
                ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                byteArrayOutputStream.toByteArray().size.toLong(),
                -1
            )
                .build()
        ).await()
    }

    suspend fun createZip(username: String, zipName: String) = runBlocking {
        logger.info { "fetching files for client $username" }
        launch {
            zipCreationThread(username, zipName)
        }
        logger.info { "aaaaaaaaaaaaaaaaaaaaaaaaaa $username" }
    }
}