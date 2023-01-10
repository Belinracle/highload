package com.itmo.highload.fileservice.service

import com.itmo.highload.fileservice.model.ClientFile
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import mu.KotlinLogging
import org.springframework.core.io.InputStreamResource
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.coroutines.CoroutineContext

class ZipCreationTask(
    private val zipOutputStream: ZipOutputStream,
    private val minioService: MinioService,
    private val clientFiles: List<ClientFile>
) {
    private val logger = KotlinLogging.logger {}
    val mutex = Mutex()
//    private val contextCor: CoroutineContext = newFixedThreadPoolContext(2, "Test Pool")

    @OptIn(DelicateCoroutinesApi::class)
    private val context: CoroutineContext = newSingleThreadContext("Single thread")
    suspend fun execute() = coroutineScope{
        logger.info { "handling creation zip for files $clientFiles" }
        val jobs = mutableListOf<Job>()
        for (clientFile in clientFiles) {
            jobs.add(launch(context = context) { handleOneFile(clientFile) })
        }
        jobs.joinAll()
        logger.info { "all coroutines finished" }
    }

    private suspend fun handleOneFile(clientFile: ClientFile) {
//        delay(1000)
        logger.info { "start handling file $clientFile in ${Thread.currentThread().name}" }
        val isr = getObjectFromMinio(clientFile.fileIndex.toString())
        logger.info { "got object from minio, start filling zip with $clientFile" }
        fillZip(clientFile.filename!!, isr)
        logger.info { "finished handling file $clientFile" }
    }

    private fun getObjectFromMinio(filename: String): InputStreamResource {
        return minioService.download(filename)
    }

    private suspend fun fillZip(filename: String, isr: InputStreamResource) {
        val zipEntry = ZipEntry(filename)
        try {
            zipOutputStream.putNextEntry(zipEntry)
            val bytes = ByteArray(1024)
            var length: Int
            val istr = isr.inputStream
            while (istr.read(bytes).also { length = it } >= 0) {
                zipOutputStream.write(bytes, 0, length)
            }
        } catch (ignored: IOException) {
            logger.error { "Some error $ignored" }
        }
    }
}