package com.fastcampus.springbatch.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

private val logger = KotlinLogging.logger {}

@Configuration
class FlowConfiguration {
    @Bean
    fun flowJob(
        jobRepository: JobRepository,
        step1: Step,
        step2: Step,
        step3: Step,
    ): Job {
        return JobBuilder("flowJob", jobRepository)
            .start(step1)
            .next(step2)
            .next(step3)
            .build()
    }

    @Bean
    fun step1(jobRepository: JobRepository, platformTransactionManager: PlatformTransactionManager): Step {
        return StepBuilder("step1", jobRepository)
            .tasklet({ _, _ ->
                logger.info { "Step1" }
                RepeatStatus.FINISHED
            }, platformTransactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun step2(jobRepository: JobRepository, platformTransactionManager: PlatformTransactionManager): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet({ _, _ ->
                logger.info { "Step2" }
                RepeatStatus.FINISHED
            }, platformTransactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun step3(jobRepository: JobRepository, platformTransactionManager: PlatformTransactionManager): Step {
        return StepBuilder("step3", jobRepository)
            .tasklet({ _, _ ->
                logger.info { "Step3" }
                RepeatStatus.FINISHED
            }, platformTransactionManager)
            .allowStartIfComplete(true)
            .build()
    }
}