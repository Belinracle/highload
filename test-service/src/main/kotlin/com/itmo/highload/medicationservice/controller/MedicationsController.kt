package com.itmo.highload.medicationservice.controller

import com.itmo.highload.feign.dto.InfinityScrollResultWrapper
import com.itmo.highload.feign.dto.MedicationDTO
import com.itmo.highload.medicationservice.service.MedicationService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/medications")
class MedicationsController(
    val medicationService: MedicationService,
) {

    @GetMapping("/viewMedications")
    fun viewMedications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): InfinityScrollResultWrapper<MedicationDTO> {
        val pageable = PageRequest.of(page, size)
        val page = medicationService.getAll(pageable)
        return InfinityScrollResultWrapper(page.content, !page.isLast)
    }

    @GetMapping("/findMedication/{id}")
    fun findMedication(@PathVariable id: Long): ResponseEntity<MedicationDTO> =
        ResponseEntity.ok(medicationService.find(id))

    @PostMapping("/addMedication")
    fun addMedication(@RequestBody medication: MedicationDTO): ResponseEntity<Any> =
        ResponseEntity.ok(medicationService.create(medication))

    @DeleteMapping("/removeMedication/{id}")
    fun removeMedication(@PathVariable id: Long): ResponseEntity<Any> = ResponseEntity.ok(medicationService.remove(id))

}