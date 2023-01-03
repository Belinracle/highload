package com.itmo.highload.medicationservice.model;

import org.hibernate.annotations.Check
import org.jetbrains.annotations.NotNull
import javax.persistence.*;

@Entity
@Table(name = "medication")
@Check(constraints = "COST > 0")
class Medication (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @NotNull
    var medname: String,

    var description: String? = null,

    var cost: Float,
) {
    constructor(medname: String, description: String?, cost: Float) : this(0, medname, description, cost) {

    }

    constructor() : this("", "", 0.0f) {

    }
}
