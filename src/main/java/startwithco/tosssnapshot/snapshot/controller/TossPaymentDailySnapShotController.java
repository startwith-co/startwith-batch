package startwithco.tosssnapshot.snapshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import startwithco.tosssnapshot.base.BaseResponse;
import startwithco.tosssnapshot.common.service.CommonService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class TossPaymentDailySnapShotController {
    private final CommonService commonService;

    @PostMapping()
    ResponseEntity<BaseResponse<String>> saveSolutionEntity() throws Exception {
        String targetDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        commonService.executeDailyTossPaymentSettlement(targetDate);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }
}
