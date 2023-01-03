package com.itmo.highload.model

import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.*


@Entity
@Table(name = "users")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email")
    var email: String,

    @Column(name = "password")
    var password: String, //certified kotlin programmer style. Зачем везде нуллы то ?

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    val role: Role,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: Status,
){}
