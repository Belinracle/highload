package com.example.fileservice.service

import com.example.fileservice.dto.FileDto
import com.example.fileservice.dto.UploadResponse
import com.example.fileservice.model.ClientFile
import io.minio.*
import io.minio.messages.Item
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.InputStream
import java.util.function.BiFunction


@Service
class MinioService(
    var minioClient: MinioClient,

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


    fun download(name: String?): Mono<InputStreamResource> {
        return Mono.fromCallable {
            val response: InputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(defaultBucketName).`object`(name).build()
            )
            InputStreamResource(response)
        }.subscribeOn(Schedulers.boundedElastic())
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