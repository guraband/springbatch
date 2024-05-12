package com.fastcampus.springbatch.config

import com.fastcampus.springbatch.domain.TestData
import com.fastcampus.springbatch.dto.TempApiRequest
import com.fastcampus.springbatch.service.TempApiFeignService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class TempApiJobConfiguration {
    private val logger = KotlinLogging.logger {}

    companion object {
        const val CHUNK_SIZE = 500
    }

    @Bean
    fun tempApiJob(
        jobRepository: JobRepository,
        tempApiStep: Step,
    ): Job {
        return JobBuilder("tempApiJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(tempApiStep)
            .build()
    }

    @Bean
    fun tempApiStep(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
        testDataReader: ItemReader<TestData>,
        tempApiProcessor: TempApiProcessor,
        testDataWriter: JpaItemWriter<TestData>,
    ): Step {
        return StepBuilder("tempApiStep", jobRepository)
            .chunk<TestData, TestData>(CHUNK_SIZE, platformTransactionManager)
            .reader(testDataReader)
            .processor(tempApiProcessor)
            .writer(testDataWriter)
            .build()
    }

    @Bean
    fun testDataReader(
        entityManagerFactory: EntityManagerFactory
    ): ItemReader<TestData> {
        return JpaPagingItemReaderBuilder<TestData>()
            .name("reader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("select d from test_data d order by d.id")
            .build()
    }

    @Bean
    fun testDataWriter(
        entityManagerFactory: EntityManagerFactory,
    ): JpaItemWriter<TestData> {
        return JpaItemWriterBuilder<TestData>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}

@Component
class TempApiProcessor(
    private val tempApiFeignService: TempApiFeignService,
) : ItemProcessor<TestData, TestData> {
    override fun process(item: TestData): TestData {
        val response = tempApiFeignService.process(TempApiRequest(item.originalText))
        item.processedText = response.result
        return item
    }
}