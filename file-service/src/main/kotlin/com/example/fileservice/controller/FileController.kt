package com.example.fileservice.controller

import com.example.fileservice.dto.FileDto
import com.example.fileservice.model.ClientFile
import com.example.fileservice.repository.ClientFileRepository
import com.example.fileservice.service.MinioService
import mu.KotlinLogging
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("api/file")
class FileController(
    var minioService: MinioService,
    val clientFileRepository: ClientFileRepository
) {
    private val logger = KotlinLogging.logger {}

    fun FileDto.toClientFile() = ClientFile(
        description = description,
        filename = filename,
        size = size,
        title = title,
        owner = owner,
    )

    @PostMapping("/upload")
    fun send(
        @ModelAttribute("fileDTO") fileDto: FileDto,
        @RequestParam("file") request: MultipartFile
    ): Mono<ResponseEntity<FileDto>> {
        logger.info { "uploading file for user $fileDto" }
        val fileIndex = UUID.randomUUID()
        val clientFile: ClientFile = fileDto.toClientFile()
        clientFile.fileIndex = fileIndex
        return clientFileRepository.save(clientFile).flatMap { savedFileInfo ->
            minioService.uploadFile(Mono.just(request), savedFileInfo)
        }.map { result -> ResponseEntity.ok().body(result) }
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