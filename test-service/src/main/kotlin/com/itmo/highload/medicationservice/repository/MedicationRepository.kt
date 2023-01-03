package com.itmo.highload.medicationservice.repository

import com.itmo.highload.medicationservice.model.Medication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository


interface MedicationRepository : CrudRepository<Medication, Long> {
    fun findAll(pageable: Pageable): Page<Medication>
}