package com.itmo.highload.fileservice.config

import io.minio.MinioClient
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


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
            val httpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build()
            MinioClient.builder()
                .endpoint(minioUrl)
                .httpClient(httpClient)
                .credentials(accessKey, accessSecret)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}