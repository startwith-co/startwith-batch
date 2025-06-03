package startwithco.tosssnapshot.batch.cancel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import startwithco.tosssnapshot.common.service.CommonService;

@Component
@RequiredArgsConstructor
@Slf4j
public class VirtualCancelScheduler {
    private final CommonService commonService;

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void runAutoCancel() {
        try {
            log.info("가상계좌 자동 취소 스케줄러");

            commonService.cancelExpiredVirtualAccounts();
        } catch (Exception e) {
            log.error("❌ 가상계좌 자동 취소 스케줄러 실패: {}", e.getMessage(), e);
        }
    }
}