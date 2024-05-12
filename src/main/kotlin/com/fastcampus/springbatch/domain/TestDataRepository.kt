package com.fastcampus.springbatch.domain

import org.springframework.data.jpa.repository.JpaRepository

interface TestDataRepository : JpaRepository<TestData, Long> {
}