package com.fastcampus.springbatch.generator

import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class ApiOrderGenerateProcessor() : ItemProcessor<Any, Any> {
    override fun process(item: Any): Any? {
        return null
    }
}
