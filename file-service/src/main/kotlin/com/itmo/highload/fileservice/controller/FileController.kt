package com.itmo.highload.fileservice.controller

import com.google.gson.Gson
import com.itmo.highload.fileservice.model.ClientFile
import com.itmo.highload.fileservice.repository.ClientFileRepository
import com.itmo.highload.fileservice.service.MinioService
import com.itmo.highload.notifications.dto.FileDto
import com.itmo.highload.notifications.dto.Notification
import com.itmo.highload.notifications.dto.NotificationType
import com.itmo.highload.redis.publisher.RedisPublisher
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@RestController
@RequestMapping("api/file")
class FileController(
    var minioService: MinioService,
    val clientFileRepository: ClientFileRepository,
    val redisPublisher: RedisPublisher,
    @Value("\${spring.redis.topic}")
    var redisTopic: String
) {
    private val logger = KotlinLogging.logger {}
    val gson = Gson()

    fun FileDto.toClientFile() = ClientFile(
        description = description,
        filename = filename,
        size = size,
        title = title,
        owner = owner,
    )

    @PostMapping("/upload")
    fun send(
        @RequestPart("owner") owner: FormFieldPart,
        @RequestPart("filename") filename: FormFieldPart,
        @RequestPart("file") request: Mono<FilePart>
    ): Mono<ResponseEntity<FileDto>> {
        return clientFileRepository.findByOwner(owner.value()).collectList()
            .flatMap { clientsFiles ->
                if (clientsFiles.map { clientFile -> clientFile.filename }.contains(filename.value())) {
                    return@flatMap Mono.just(
                        ResponseEntity.badRequest().body(FileDto(filename = "some shit"))
                    )
                } else {
                    val fileDto = FileDto(owner = owner.value(), filename = filename.value())
                    logger.info { "uploading file for user $fileDto" }
                    val fileIndex = UUID.randomUUID()
                    val clientFile: ClientFile = fileDto.toClientFile()
                    clientFile.fileIndex = fileIndex
                    clientFileRepository.save(clientFile).flatMap { savedFileInfo ->
                        minioService.upload(request, savedFileInfo)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnSuccess {
                                redisPublisher.publish(redisTopic, generateNotificationJson(it))
                                    .subscribe { response -> logger.info { "published redis message to $response listeners" } }
                            }
                    }.map { result -> ResponseEntity.ok().body(result) }
                }
            }
    }

    @PostMapping("/uploadCor")
    suspend fun uploadCor(
        @RequestPart("owner") owner: FormFieldPart,
        @RequestPart("filename") filename: FormFieldPart,
        @RequestPart("file") request: FilePart
    ): ResponseEntity<FileDto>  = runBlocking{
        val clientFiles: List<ClientFile> = clientFileRepository.findByOwner(owner.value()).collectList().awaitSingle()
        logger.info { "existing files $clientFiles" }
        return@runBlocking if (clientFiles.map { clientFile -> clientFile.filename }.contains(filename.value())) {
            ResponseEntity.badRequest().body(FileDto(filename = "some shit"))
        } else {
            val fileDto = FileDto(owner = owner.value(), filename = filename.value())
            logger.info { "uploading file for user $fileDto" }
            val fileIndex = UUID.randomUUID()
            val clientFile: ClientFile = fileDto.toClientFile()
            clientFile.fileIndex = fileIndex
            val savedClientFile = clientFileRepository.save(clientFile).awaitSingle()
            val minioUploadedFile = minioService.uploadAsync(request, savedClientFile)
            async {
                redisPublisher.publish(redisTopic, generateNotificationJson(minioUploadedFile))
                    .subscribe { response -> logger.info { "published redis message to $response listeners" } }
            }
            ResponseEntity.ok(minioUploadedFile)
        }
    }

    private fun generateNotificationJson(fileDto: FileDto): String {
        return gson.toJson(
            Notification<FileDto>(
                destination = fileDto.owner!!,
                notificationType = NotificationType.FILE_UPLOADED,
                body = fileDto,
                time = System.currentTimeMillis(),
            )
        )
    }

    @GetMapping("/filename")
    fun download(
        @RequestParam(value = "filename") fileName: String
    ): ResponseEntity<InputStreamResource> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .body(minioService.download(fileName))
    }


    @GetMapping("/all")
    fun getFiles(): ResponseEntity<Any?>? {
        return ResponseEntity.ok(minioService.getListObjects())
    }

    @GetMapping("/userfile")
    fun getClientFiles(@RequestParam(value = "username") username: String): Flux<FileDto> {
        logger.info { "requesting $username files" }
        return clientFileRepository.findByOwner(username)
            .map { clientFile ->
                clientFile.toDTO()
            }
    }
}