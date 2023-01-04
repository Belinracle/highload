package com.example.fileservice.model

import javax.persistence.*;

@Entity
@Table(name = "clientfile")
data class ClientFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "useremail")
    var userEmail: String,
    @Column(name = "filename")
    var filename: String
)