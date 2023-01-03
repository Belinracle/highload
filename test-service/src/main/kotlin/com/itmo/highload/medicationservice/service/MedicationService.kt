package com.itmo.highload.medicationservice.service

import com.itmo.highload.feign.dto.MedicationDTO
import com.itmo.highload.medicationservice.model.Medication
import com.itmo.highload.medicationservice.repository.MedicationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class MedicationService(
    val repository: MedicationRepository
) {

    fun Medication.toDto() = MedicationDTO(
        name = medname,
        description = description,
        cost = cost,
    )

    fun MedicationDTO.toEntity() = Medication(
        medname = name,
        description = description,
        cost = cost,
    )

    fun getAll(pageable: Pageable): Page<MedicationDTO> =
        repository.findAll(pageable).map { it.toDto() }

    fun create(medication: MedicationDTO): MedicationDTO? = repository.save(
        medication.toEntity()
    ).toDto()

    fun remove(id: Long) {
        if (repository.existsById(id)) repository.deleteById(id)
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun find(id: Long): MedicationDTO? = (repository.findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)).toDto()

}