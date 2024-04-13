package com.fastcampus.springbatch.generator

import com.fastcampus.springbatch.dto.ApiOrder
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.DefaultJobParametersValidator
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.PathResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ApiOrderGenerateJobConfiguration(
    private val jobRepository: JobRepository,
    private val platformTransactionManager: PlatformTransactionManager,
) {
    @Bean
    fun apiOrderGenerateJob(step: Step): Job {
        return JobBuilder("apiOrderGenerateJob", jobRepository)
            .start(step)
            .incrementer(RunIdIncrementer())
            .validator(DefaultJobParametersValidator(arrayOf("targetDate", "totalCount"), emptyArray()))
            .build()
    }

    @Bean
    fun step(
        apiOrderGenerateReader: ApiOrderGenerateReader,
        apiOrderGenerateProcessor: ApiOrderGenerateProcessor,
    ): Step {
        // chunk : 1000 -> 5s579ms
        // chunk : 10000 -> 4s256ms
        return StepBuilder("apiOrderGenerateStep", jobRepository)
            .chunk<Boolean, ApiOrder>(10000, platformTransactionManager)
            .reader(apiOrderGenerateReader)
            .processor(apiOrderGenerateProcessor)
            .writer(apiOrderGenerateWriter(null))
            .build()
    }

    @Bean
    @StepScope
    fun apiOrderGenerateWriter(
        @Value("#{jobParameters['targetDate']}") targetDate: String?,
    ): FlatFileItemWriter<ApiOrder> {
        val fileName = "api_orders_${targetDate}.csv"

        return FlatFileItemWriterBuilder<ApiOrder>()
            .name("apiOrderGenerateWriter")
            .resource(PathResource("src/main/resources/data/$fileName"))
            .delimited()
            .names("id", "customerId", "url", "status", "createdAt")
            .headerCallback {
                it.write("id,customerId,url,status,createdAt")
            }
            .build()
    }
}