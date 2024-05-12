package com.fastcampus.springbatch.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity(name = "test_data")
@Table(name = "test_data", catalog = "springbatch")
data class TestData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "original_text")
    var originalText: String,
    @Column(name = "processed_text")
    var processedText: String? = null,

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime,

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)