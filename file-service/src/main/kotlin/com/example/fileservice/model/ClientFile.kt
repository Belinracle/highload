package com.example.fileservice.model

import javax.persistence.*;

@Entity
@Table(name = "clientFile")
data class ClientFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "userEmail")
    var userEmail: String,
    @Column(name = "filename")
    var filename: String
)