package startwithco.tosssnapshot.batch.settlement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class TossSettlementTasklet implements Tasklet {

    @Value("${toss.payment.secret-key}")
    private String tossSecretKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public TossSettlementTasklet(@Qualifier("tossSettlementWebClient") WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String encodedSecretKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        int page = 1;
        int size = 5000;
        List<JsonNode> allSettlements = new ArrayList<>();

        while (true) {
            String url = String.format(
                    "/v1/settlements?startDate=%s&endDate=%s&dateType=paidOutDate&page=%d&size=%d",
                    targetDate, targetDate, page, size
            );

            try {
                String response = webClient.get()
                        .uri(url)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(Duration.ofSeconds(60));

                JsonNode root = objectMapper.readTree(response);
                JsonNode settlements = root.path("settlements");

                if (settlements.isArray()) {
                    for (JsonNode node : settlements) {
                        allSettlements.add(node);
                    }
                }

                boolean hasNext = root.path("hasNext").asBoolean(false);
                if (!hasNext) break;

                page++;
            } catch (WebClientResponseException e) {
                log.error("❌ [API 응답 오류] page {} | status: {}, body: {}",
                        page, e.getStatusCode(), e.getResponseBodyAsString());
                throw e;
            } catch (IOException e) {
                log.error("❌ [JSON 파싱 오류] page {} | {}", page, e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("❌ [알 수 없는 오류] page {} | {}", page, e.getMessage());
                throw e;
            }
        }

        for (JsonNode settlement : allSettlements) {
            log.info("🔍 정산 항목: {}", settlement.toPrettyString());
            // TODO: 정산 엔티티로 변환 후 저장 로직 추가 가능
        }

        log.info("✅ 전체 정산 조회 완료 ({}): 총 {}건", targetDate, allSettlements.size());
        return RepeatStatus.FINISHED;
    }
}