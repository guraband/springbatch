package com.fastcampus.springbatch.config

import com.fastcampus.springbatch.dto.User
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager

private val logger = KotlinLogging.logger {}

@Configuration
class MultiThreadedJobConfig {
    @Bean
    fun job(
        jobRepository: JobRepository,
        step: Step,
    ): Job {
        return JobBuilder("multiThreadJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step)
            .build()
    }

    @Bean
    fun step(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
        jpaPagingItemReader: ItemReader<User>,
    ): Step {
        return StepBuilder("step", jobRepository)
            .chunk<User, User>(50, platformTransactionManager)
            .reader(jpaPagingItemReader)
            .writer { result -> logger.info { result } }
            .taskExecutor(SimpleAsyncTaskExecutor())
            .build()
    }

    @Bean
    fun jpaPagingItemReader(
        entityManagerFactory: EntityManagerFactory
    ): ItemReader<User> {
        return JpaPagingItemReaderBuilder<User>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(100)
            .saveState(false)
            .queryString("select u from USER u order by u.id")
            .build()
    }
}