package startwithco.tosssnapshot.batch.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import startwithco.tosssnapshot.common.service.CommonService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossSettlementScheduler {

    private final JobLauncher jobLauncher;
    private final Job tossSettlementJob;
    private final CommonService commonService;

    @Scheduled(cron = "0 0 3 * * *")
    public void runTossSettlementJob() {
        String targetDate = LocalDate.now().minusDays(1).toString();

        JobParameters params = new JobParametersBuilder()
                .addString("targetDate", targetDate)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(tossSettlementJob, params);
        } catch (Exception e) {
            commonService.sendFailureEmail(LocalDateTime.now().toString(), e);

            log.error("❌ 정산 배치 실행 실패: {}", e.getMessage(), e);
        }
    }
}