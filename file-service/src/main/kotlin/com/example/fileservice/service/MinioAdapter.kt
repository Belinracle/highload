package com.example.fileservice.service

import com.example.fileservice.dto.UploadResponse
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.UploadObjectArgs
import io.minio.messages.Bucket
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.InputStream
import java.util.function.BiFunction

@Service
class MinioAdapter(
    @Autowired
    var minioClient: MinioClient? = null,

    @Value("\${minio.bucket.name}")
    var defaultBucketName: String? = null,

    @Value("\${minio.default.folder}")
    var defaultBaseFolder: String? = null
) {
    private val logger = KotlinLogging.logger {}

    private val allBuckets: Flux<Bucket>
        get() = try {
            Flux.fromIterable(minioClient!!.listBuckets())
                .subscribeOn(Schedulers.boundedElastic())
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }

    fun uploadFile(file: MultipartFile): UploadResponse {
        val startMillis = System.currentTimeMillis()
        val temp = File(file.name)
        temp.canWrite()
        temp.canRead()
        try {
            logger.info(temp.absolutePath)
            // blocking to complete io operation
            file.transferTo(temp)
            val uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(defaultBucketName)
                .`object`(file.name)
                .filename(temp.absolutePath)
                .build()
            val response = minioClient!!.uploadObject(uploadObjectArgs)
            temp.delete()
            logger.info(
                "upload file execution time {} ms",
                System.currentTimeMillis() - startMillis
            )
            return UploadResponse(
                id = response.etag().toString(),
                bucket = response.bucket(),
                objectName = response.`object`()
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    fun download(name: String?): Mono<InputStreamResource> {
        return Mono.fromCallable {
            val response: InputStream = minioClient!!.getObject(
                GetObjectArgs.builder().bucket(defaultBucketName).`object`(name).build()
            )
            InputStreamResource(response)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun putObject(file: FilePart): Mono<UploadResponse> {
        return file.content()
            .subscribeOn(Schedulers.boundedElastic())
            .reduce(
                InputStreamCollector(),
                BiFunction<InputStreamCollector, DataBuffer, InputStreamCollector> { collector: InputStreamCollector, dataBuffer: DataBuffer ->
                    collector.collectInputStream(
                        dataBuffer.asInputStream()
                    )
                })
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
                    val response = minioClient!!.putObject(args)
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