package com.fastcampus.springbatch.dto

import jakarta.persistence.*

@Entity(name = "USER")
@Table(name = "USER")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String? = null,
    var age: String? = null,
    var region: String? = null,

    @Column(name="phone_no")
    var phoneNo: String? = null,
)