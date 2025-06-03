package startwithco.tosssnapshot.batch.settlement;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TossSettlementBatchConfiguration {

    private final TossSettlementTasklet tossSettlementTasklet;

    @Bean
    public Job tossSettlementJob(JobRepository jobRepository, Step tossSettlementStep) {
        return new JobBuilder("tossSettlementJob", jobRepository)
                .start(tossSettlementStep)
                .build();
    }

    @Bean
    public Step tossSettlementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("tossSettlementStep", jobRepository)
                .tasklet(tossSettlementTasklet, transactionManager)
                .build();
    }
}