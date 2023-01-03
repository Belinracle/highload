package com.example.fileservice.controller

import com.example.fileservice.repository.ClientFileRepository
import com.example.fileservice.dto.UploadResponse
import com.example.fileservice.model.ClientFile
import com.example.fileservice.service.MinioAdapter
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("api/file")
class FileController(
    var minioAdapter: MinioAdapter,
    val clientFileRepository: ClientFileRepository
) {
    @PostMapping("/upload")
    fun send(
        @RequestParam("userEmail") userEmail: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<UploadResponse> {
        val filename = file.originalFilename
        if (clientFileRepository.findByFilename(filename!!).isEmpty()) {
            var uploadResponse =
                minioAdapter.uploadFile(file)
            clientFileRepository.save(ClientFile(userEmail = userEmail, filename = filename))
            return ResponseEntity.ok(uploadResponse)
        }
        return ResponseEntity.badRequest().header("Error reason", "file already uploaded ").build()
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
        return minioAdapter.putObject(files)
    }

    @RequestMapping(path = ["/"], method = [RequestMethod.GET])
    fun download(
        @RequestParam(value = "filename") fileName: String
    ): ResponseEntity<Mono<InputStreamResource>> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .body(minioAdapter.download(fileName))
    }
}