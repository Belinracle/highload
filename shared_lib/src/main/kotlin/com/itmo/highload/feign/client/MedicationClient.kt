package com.itmo.highload.feign.client

import com.itmo.highload.feign.dto.InfinityScrollResultWrapper
import com.itmo.highload.feign.dto.MedicationDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@FeignClient(name = "\${medication.service.app.name}", path = "\${medication.service.context.path}")
interface MedicationClient {
    @GetMapping("/viewMedications")
    fun viewMedications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): InfinityScrollResultWrapper<MedicationDTO>

    @GetMapping("/findMedication/{id}")
    fun findMedication(@PathVariable id: Long): ResponseEntity<MedicationDTO>

    @PostMapping("/addMedication")
    fun addMedication(@RequestBody medication: MedicationDTO): ResponseEntity<Any>

    @DeleteMapping("/removeMedication/{id}")
    fun removeMedication(@PathVariable id: Long): ResponseEntity<Any>

}