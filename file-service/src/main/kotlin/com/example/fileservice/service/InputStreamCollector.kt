package com.example.fileservice.service

import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import java.io.InputStream


class InputStreamCollector {
    private var targetStream: ByteArrayOutputStream = ByteArrayOutputStream()

    fun collectInputStream(input: InputStream?): InputStreamCollector {
        IOUtils.copy(input, targetStream)
        return this
    }

    val stream: InputStream
        get() = ByteArrayInputStream(targetStream.toByteArray())
}