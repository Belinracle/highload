package com.example.fileservice.dto

data class UploadResponse(
    var id: String? = null,
    var objectName: String? = null,
    var bucket: String? = null,
)