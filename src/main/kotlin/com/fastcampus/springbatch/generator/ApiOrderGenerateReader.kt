package com.fastcampus.springbatch.generator

import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component

@Component
class ApiOrderGenerateReader() : ItemReader<Boolean> {
    override fun read(): Boolean? {
        return null
    }
}
