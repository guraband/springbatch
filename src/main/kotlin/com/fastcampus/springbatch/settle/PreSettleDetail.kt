package com.fastcampus.springbatch.settle

import com.fastcampus.springbatch.dto.ApiOrder
import com.fastcampus.springbatch.dto.ServicePolicy
import com.fastcampus.springbatch.dto.Status
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class PreSettleDetailProcessor : ItemProcessor<ApiOrder, Key> {
    override fun process(item: ApiOrder): Key? {
        if (item.status == Status.FAIL) {
            return null
        }

        val serviceId = ServicePolicy.findByUrl(item.url).id

        return Key(item.customerId!!, serviceId)
    }
}

@Component
class PreSettleDetailWriter : ItemWriter<Key>, StepExecutionListener {
    private lateinit var stepExecution: StepExecution

    override fun beforeStep(stepExecution: StepExecution) {
        this.stepExecution = stepExecution

        val snapshotMap: ConcurrentMap<Key, Long> = ConcurrentHashMap()
        stepExecution.executionContext.put("snapshots", snapshotMap)
    }

    override fun write(chunk: Chunk<out Key>) {
        val snapshotMap = stepExecution.executionContext["snapshots"] as ConcurrentMap<Key, Long>
        chunk.forEach {
            snapshotMap.compute(it) { _, v -> v?.plus(1) ?: 1 }
        }
    }
}

data class Key(
    val customerId: Long,
    val serviceId: Long,
) : Serializable