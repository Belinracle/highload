package com.example.fileservice.controller

import com.example.fileservice.dto.FileDto
import com.example.fileservice.dto.UploadResponse
import com.example.fileservice.repository.ClientFileRepository
import com.example.fileservice.service.MinioService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("api/file")
class FileController(
    var minioService: MinioService,
    val clientFileRepository: ClientFileRepository
) {
    @PostMapping("/upload")
    fun send(
        @RequestParam("userEmail") userEmail: String,
        @ModelAttribute("file") request: FileDto
    ): ResponseEntity<FileDto> {
        return ResponseEntity.ok().body(minioService.uploadFile(request));
    }

    @RequestMapping(
        path = ["/stream"],
        method = [RequestMethod.POST],
        produces = [MimeTypeUtils.APPLICATION_JSON_VALUE],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadStream(
        @RequestPart(value = "files", required = true) files: FilePart,
        @RequestParam(value = "ttl", required = false) ttl: Int
    ): Mono<UploadResponse> {
        return minioService.putObject(files)
    }

    @RequestMapping(path = ["/"], method = [RequestMethod.GET])
    fun download(
        @RequestParam(value = "filename") fileName: String
    ): ResponseEntity<Mono<InputStreamResource>> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .body(minioService.download(fileName))
    }

    @GetMapping("/all")
    fun getFiles(): ResponseEntity<Any?>? {
        return ResponseEntity.ok(minioService.getListObjects())
    }
}