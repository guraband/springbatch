package com.fastcampus.springbatch.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "SettleGroup")
class SettleGroup(
    val customerId: Long,
    val serviceId: Long,
    val totalCount: Long,
    val totalFee: Long,
    val createdAt: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)