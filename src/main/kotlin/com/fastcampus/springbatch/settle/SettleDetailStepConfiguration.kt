package com.fastcampus.springbatch.settle

import com.fastcampus.springbatch.dto.ApiOrder
import com.fastcampus.springbatch.dto.SettleDetail
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.listener.ExecutionContextPromotionListener
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class SettleDetailStepConfiguration(
    private val jobRepository: JobRepository,
    private val platformTransactionManager: PlatformTransactionManager,
) {
    @Bean
    fun preSettleDetailStep(
        preSettleDetailReader: FlatFileItemReader<ApiOrder>,
        preSettleDetailProcessor: PreSettleDetailProcessor,
        preSettleDetailWriter: PreSettleDetailWriter,
        promotionListener: ExecutionContextPromotionListener,
    ): Step {
        return StepBuilder("preSettleDetailStep", jobRepository)
            .chunk<ApiOrder, Key>(5_000, platformTransactionManager)
            .reader(preSettleDetailReader)
            .processor(preSettleDetailProcessor)
            .writer(preSettleDetailWriter)
            .listener(promotionListener)
            .build()
    }

    @Bean
    @StepScope
    fun preSettleDetailReader(
        @Value("#{jobParameters['targetDate']}") targetDate: String,
    ): FlatFileItemReader<ApiOrder> {
        val fileName = "api_orders_${targetDate}.csv"

        return FlatFileItemReaderBuilder<ApiOrder>()
            .name("preSettleDetailReader")
            .resource(ClassPathResource("/data/$fileName"))
            .linesToSkip(1)
            .delimited()
            .names("id", "customerId", "url", "status", "createdAt")
            .targetType(ApiOrder::class.java)
            .build()
    }

    @Bean
    fun settleDetailStep(
        settleDetailReader: SettleDetailReader,
        settleDetailProcessor: SettleDetailProcessor,
        settleDetailWriter: JpaItemWriter<SettleDetail>,
    ) : Step {
        return StepBuilder("settleDetailStep", jobRepository)
            .chunk<KeyAndCount, SettleDetail>(1_000, platformTransactionManager)
            .reader(settleDetailReader)
            .processor(settleDetailProcessor)
            .writer(settleDetailWriter)
            .build()
    }

    // preSettleStep에서 사용한 ExecutionContext를 job level로 올려주는 역할
    @Bean
    fun promotionListener() : ExecutionContextPromotionListener {
        val listener = ExecutionContextPromotionListener()

        listener.setKeys(arrayOf("snapshots"))

        return listener
    }

    @Bean
    fun settleDetailWriter(
        entityManagerFactory: EntityManagerFactory,
    ) : JpaItemWriter<SettleDetail> {

        return JpaItemWriterBuilder<SettleDetail>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}
