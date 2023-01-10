package com.itmo.highload.fileservice.config

import io.minio.MinioAsyncClient
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MinioConfig {
    @Value("\${minio.access.name}")
    var accessKey: String? = null

    @Value("\${minio.access.secret}")
    var accessSecret: String? = null

    @Value("\${minio.url}")
    var minioUrl: String? = null

    @Bean
    fun generateMinioClient(): MinioClient {
        return try {
            MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    @Bean
    fun generateMinioAsyncClient(): MinioAsyncClient {
        return try {
            MinioAsyncClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}