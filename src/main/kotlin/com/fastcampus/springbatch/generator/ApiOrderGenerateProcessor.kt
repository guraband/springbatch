package com.fastcampus.springbatch.generator

import com.fastcampus.springbatch.dto.ApiOrder
import com.fastcampus.springbatch.dto.ServicePolicy
import com.fastcampus.springbatch.dto.Status
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Component
class ApiOrderGenerateProcessor() : ItemProcessor<Boolean, ApiOrder> {

    private val customerIds = (0 until 20).toList()
    private val servicePolicies = enumValues<ServicePolicy>().toList()
    private val random = ThreadLocalRandom.current()
    private val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    override fun process(item: Boolean): ApiOrder? {

        val customerId = customerIds[random.nextInt(customerIds.size)].toLong()
        val servicePolicy = servicePolicies[random.nextInt(servicePolicies.size)]
        val status = if (random.nextInt(5) % 5 == 0) {
            Status.FAIL
        } else {
            Status.SUCCESS
        }

        return ApiOrder(
            UUID.randomUUID().toString(),
            customerId,
            servicePolicy.url,
            status,
            now,
        )
    }
}
