package com.fastcampus.springbatch.settle

import com.fastcampus.springbatch.dto.ApiOrder
import com.fastcampus.springbatch.dto.ServicePolicy
import com.fastcampus.springbatch.dto.SettleDetail
import com.fastcampus.springbatch.dto.Status
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class SettleDetailReader : ItemReader<KeyAndCount>, StepExecutionListener {
    private lateinit var iterator: Iterator<Map.Entry<Key, Long>>

    override fun read(): KeyAndCount? {
        if (!iterator.hasNext()) {
            return null
        }

        val map = iterator.next()
        return KeyAndCount(map.key, map.value)
    }

    override fun beforeStep(stepExecution: StepExecution) {
        val jobExecution = stepExecution.jobExecution
        val snapshots = jobExecution.executionContext["snapshots"] as ConcurrentMap<Key, Long>
        iterator = snapshots.entries.iterator()
    }
}

@Component
class SettleDetailProcessor : ItemProcessor<KeyAndCount, SettleDetail>, StepExecutionListener {
    private lateinit var stepExecution: StepExecution

    override fun process(item: KeyAndCount): SettleDetail {
        val key = item.key
        val servicePolicy = ServicePolicy.findById(key.serviceId)

        return SettleDetail(
            customerId = key.customerId,
            serviceId = key.serviceId,
            count = item.count,
            fee = servicePolicy.fee * item.count,
            targetDate = LocalDate.parse(
                stepExecution.jobParameters.getString("targetDate")!!,
                DateTimeFormatter.ofPattern("yyyyMMdd")
            ),
        )
    }

    override fun beforeStep(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }
}

data class KeyAndCount(
    val key: Key,
    val count: Long,
) : Serializable