package com.fastcampus.springbatch.settle

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersValidator
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.format.DateTimeFormatter

//@Configuration
class SettleJobConfiguration(
    private val jobRepository: JobRepository,
) {

    @Bean
    fun settleJob(
        preSettleDetailStep: Step,
        settleDetailStep: Step,
    ): Job {
        return JobBuilder("settleJob", jobRepository)
            .validator(DateFormatJobParametersValidator(arrayOf("targetDate")))
            .incrementer(RunIdIncrementer())
            .start(preSettleDetailStep)
            .next(settleDetailStep)
            .build()
    }
}

internal class DateFormatJobParametersValidator(
    private val names : Array<String>
) : JobParametersValidator {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun validate(parameters: JobParameters?) {
        names.forEach {
            parameters?.getString(it)?.let { date ->
                try {
                    dateFormatter.parse(date)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid date format for parameter $it")
                }
            }
        }
    }
}