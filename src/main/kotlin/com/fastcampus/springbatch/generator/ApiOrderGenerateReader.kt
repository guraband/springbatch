package com.fastcampus.springbatch.generator

import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
@StepScope
class ApiOrderGenerateReader(
    @Value("#{jobParameters['totalCount']}") totalCount : String,
) : ItemReader<Boolean> {

    val totalCount: Long = totalCount.toLong()
    var current : AtomicLong = AtomicLong(0)
    override fun read(): Boolean? {
        if (current.incrementAndGet() > totalCount) {
            return null
        }

        return true
    }
}
