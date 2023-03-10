package com.itmo.highload.fileservice.service

import com.itmo.highload.fileservice.dto.UploadResponse
import com.itmo.highload.fileservice.model.ClientFile
import com.itmo.highload.notifications.dto.FileDto
import io.minio.*
import io.minio.messages.Item
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.InputStream
import java.util.concurrent.CompletableFuture


@Service
class MinioService(
    var minioClient: MinioClient,
    var minioAsyncClient: MinioAsyncClient,

    @Value("\${minio.bucket}")
    var defaultBucketName: String? = null,
) {
    private val logger = KotlinLogging.logger {}

    fun getListObjects(): List<FileDto>? {
        val objects: MutableList<FileDto> = ArrayList()
        try {
            val result: Iterable<Result<Item>> = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(defaultBucketName)
                    .recursive(true)
                    .build()
            )
            for (item in result) {
                objects.add(
                    FileDto(
                        filename = item.get().objectName(),
                        size = item.get().size(),
                    )
                )
            }
            return objects
        } catch (e: java.lang.Exception) {
            logger.error("Happened error when get list objects from minio: ", e)
        }
        return objects
    }

    fun uploadFile(request: Mono<MultipartFile>, file: ClientFile): Mono<FileDto> {
        logger.info { "uploading file $file" }
        return request.subscribeOn(Schedulers.boundedElastic()).map<FileDto?> { fileRequest ->
            try {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .`object`(file.fileIndex.toString())
                        .stream(fileRequest.inputStream, fileRequest.size, -1)
                        .build()
                )
            } catch (e: java.lang.Exception) {
                logger.error("Happened error when upload file: ", e)
            }
            file.toDTO()
        }
            .log()
    }

    suspend fun uploadAsync(file: FilePart, clientFile: ClientFile): FileDto  {
        logger.info { "uploading file async $clientFile" }
            val startMillis = System.currentTimeMillis();
            val temp = File(clientFile.fileIndex.toString());
            temp.canWrite();
            temp.canRead();
            try {
                println("absolute path " + temp.absolutePath)
                file.transferTo(temp).awaitFirstOrNull().let {
                    logger.info { "transferred" }
                    val uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .`object`(clientFile.fileIndex.toString())
                        .filename(temp.absolutePath)
                        .build()
                    logger.info { "uploading" }
                    minioAsyncClient.uploadObject(uploadObjectArgs).await()
                    logger.info { "uploaded" }
                    temp.delete()
                    logger.info(
                        "upload file execution time {} ms",
                        System.currentTimeMillis() - startMillis
                    )
                }
            }catch (e:Exception){
                logger.error { "some shit happened $e" }
            }
        return clientFile.toDTO()
    }


    fun upload(file: Mono<FilePart>, clientFile: ClientFile): Mono<FileDto> {
        logger.info { "uploading file $clientFile" }
        return file.subscribeOn(Schedulers.boundedElastic()).flatMap { multipartFile ->
            val startMillis = System.currentTimeMillis();
            val temp = File(clientFile.fileIndex.toString());
            temp.canWrite();
            temp.canRead();
            try {
                println("absolute path " + temp.absolutePath)
                multipartFile.transferTo(temp).doOnSuccess {
                    val uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .`object`(clientFile.fileIndex.toString())
                        .filename(temp.absolutePath)
                        .build()
                    val response = minioClient.uploadObject(uploadObjectArgs)
                    temp.delete()
                    logger.info(
                        "upload file execution time {} ms",
                        System.currentTimeMillis() - startMillis
                    )
                }.thenReturn(clientFile.toDTO())

            } catch (e: java.lang.Exception) {
                return@flatMap Mono.error(RuntimeException(e))
            }

        }.log();
    }

    fun download(name: String): InputStreamResource {
        val response: InputStream = minioClient.getObject(
            GetObjectArgs.builder().bucket(defaultBucketName).`object`(name).build()
        )
        return InputStreamResource(response)
    }
    fun downloadAsync(name: String): CompletableFuture<InputStreamResource> {
        return minioAsyncClient.getObject(
            GetObjectArgs.builder().bucket(defaultBucketName).`object`(name).build()
        ).thenApply { ins->InputStreamResource(ins) }
    }


    fun putObject(file: FilePart): Mono<UploadResponse> {
        return file.content()
            .subscribeOn(Schedulers.boundedElastic())
            .reduce(
                InputStreamCollector()
            ) { collector: InputStreamCollector, dataBuffer: DataBuffer ->
                collector.collectInputStream(
                    dataBuffer.asInputStream()
                )
            }
            .map { inputStreamCollector ->
                val startMillis = System.currentTimeMillis()
                try {
                    logger.info { file.headers().contentType.toString() }
                    val args = PutObjectArgs.builder().`object`(file.filename())
                        .contentType(file.headers().contentType.toString())
                        .bucket(defaultBucketName)
                        .stream(
                            inputStreamCollector.stream,
                            inputStreamCollector.stream.available().toLong(),
                            -1
                        )
                        .build()
                    val response = minioClient.putObject(args)
                    logger.info(
                        "upload file execution time {} ms",
                        System.currentTimeMillis() - startMillis
                    )
                    return@map UploadResponse(
                        bucket = response.bucket(),
                        objectName = response.`object`()
                    )
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }.log()
    }
}