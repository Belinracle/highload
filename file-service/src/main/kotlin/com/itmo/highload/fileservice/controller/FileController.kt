package com.itmo.highload.fileservice.controller

import com.google.gson.Gson
import com.itmo.highload.fileservice.model.ClientFile
import com.itmo.highload.fileservice.repository.ClientFileRepository
import com.itmo.highload.fileservice.service.MinioService
import com.itmo.highload.notifications.dto.FileDto
import com.itmo.highload.notifications.dto.Notification
import com.itmo.highload.notifications.dto.NotificationType
import com.itmo.highload.redis.publisher.RedisPublisher
import mu.KotlinLogging
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
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
        @RequestPart("owner") owner: String,
        @RequestPart("filename") filename: String,
        @RequestPart("file") request: Mono<FilePart>
    ): Mono<ResponseEntity<FileDto>> {
        val fileDto = FileDto(owner = owner, filename = filename)
        logger.info { "uploading file for user $fileDto" }
        val fileIndex = UUID.randomUUID()
        val clientFile: ClientFile = fileDto.toClientFile()
        clientFile.fileIndex = fileIndex
        return clientFileRepository.save(clientFile).flatMap { savedFileInfo ->
            minioService.upload(request, savedFileInfo)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess {
                    redisPublisher.publish(generateNotificationJson(it))
                        .subscribe { response -> logger.info { "published redis message to $response listeners" } }
                }
        }.map { result -> ResponseEntity.ok().body(result) }
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
    ): Mono<ResponseEntity<InputStreamResource>> {
        return minioService.download(fileName).map { iss ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(iss)
        }
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