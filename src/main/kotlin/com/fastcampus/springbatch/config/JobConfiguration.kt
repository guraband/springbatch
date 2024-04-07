package com.fastcampus.springbatch.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

private val logger = KotlinLogging.logger {}

// Job name must be specified in case of multiple jobs 방지를 위해 테스트가 완료된 것은 주석 처리
//@Configuration
class JobConfiguration {
    @Bean
    fun job(jobRepository: JobRepository, stepForSkipTest: Step): Job {
        return JobBuilder("job-skip-test", jobRepository)
            .start(stepForSkipTest)
            .build()
    }

    @Bean
    fun step(jobRepository: JobRepository, platformTransactionManager: PlatformTransactionManager): Step {
        val tasklet: Tasklet = object : Tasklet {
            private var count = 0

            @Throws(Exception::class)
            override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
                count++
                return if (count == 15) {
                    logger.info { "job finished" }
                    RepeatStatus.FINISHED
                } else {
                    logger.info { "job continuable $count" }
                    RepeatStatus.CONTINUABLE
                }
            }
        }

        val itemReader = object : ItemReader<Int?> {
            private var count = 0

            override fun read() : Int? {
                count++

                logger.info { "read $count"}

                if (count == 15) {
                    logger.info { "read finished" }
                    return null
                }

                return count
            }
        }
        /*
        return StepBuilder("step", jobRepository)
            .tasklet(tasklet, platformTransactionManager)
            .build()
        */
        return StepBuilder("step", jobRepository)
            .chunk<Any, Any>(10, platformTransactionManager)
            .reader(itemReader)
//            .processor()
            .writer { }
//            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun stepForSkipTest(jobRepository: JobRepository, platformTransactionManager: PlatformTransactionManager): Step {
        val itemReader = object : ItemReader<Int?> {
            private var count = 0

            override fun read() : Int? {
                count++

                logger.info { "read $count"}

                if (count > 15) {
                    logger.info { "강제 exception 처리 $count" }
                    throw IllegalArgumentException("강제 exception 처리")
                }

                return count
            }
        }

        return StepBuilder("step", jobRepository)
            .chunk<Any, Any>(10, platformTransactionManager)
            .reader(itemReader)
            .writer { }
            .faultTolerant()
            .skipPolicy { t, skipCount -> t is IllegalArgumentException && skipCount < 5 }
            .build()
    }
}