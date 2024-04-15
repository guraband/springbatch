package com.fastcampus.springbatch.dto

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "SettleDetail")
class SettleDetail(
    val customerId: Long,
    val serviceId: Long,
    val count: Long,
    val fee: Long,
    val targetDate: LocalDate,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)