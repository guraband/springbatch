package com.fastcampus.springbatch.generator

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
            .build()
    }

    @Bean
    fun step(
        apiOrderGenerateReader: ApiOrderGenerateReader,
        apiOrderGenerateProcessor: ApiOrderGenerateProcessor,
    ) : Step {
        return StepBuilder("apiOrderGenerateStep", jobRepository)
            .chunk<Boolean, Any>(1000, platformTransactionManager)
            .reader(apiOrderGenerateReader)
            .processor(apiOrderGenerateProcessor)
            .writer {}
            .build()
    }
}