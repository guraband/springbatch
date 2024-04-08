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
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager

private val logger = KotlinLogging.logger {}

@Configuration
class ItemReaderJobConfiguration {
    @Bean
    fun itemReaderJob(
        jobRepository: JobRepository,
        step: Step,
    ): Job {
        return JobBuilder("itemReaderJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step)
            .build()
    }

    @Bean
    fun step(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
//        flatFileItemReader: ItemReader<User>,
        jpaPagingItemReader: ItemReader<User>,
    ): Step {
        return StepBuilder("itemReaderStep", jobRepository)
            .chunk<User, User>(1000, platformTransactionManager)
            .reader(jpaPagingItemReader)
            .writer(System.out::println)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun flatFileItemReader(): FlatFileItemReader<User> {
            return FlatFileItemReaderBuilder<User>()
                .name("flatFileItemReader")
                .resource(ClassPathResource("users.txt"))
                .linesToSkip(2)
                .delimited().delimiter(",")
                .names("name", "age", "region", "phoneNo")
                .targetType(User::class.java)
                .build()
    }

    @Bean
    fun jpaPagingItemReader(
        entityManagerFactory: EntityManagerFactory
    ) : ItemReader<User> {
        return JpaPagingItemReaderBuilder<User>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(50)
            .queryString("select u from USER u order by u.id")
            .build()
    }
}