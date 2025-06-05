package startwithco.tosssnapshot.batch.settlement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final JdbcTemplate jdbcTemplate;

    public TossSettlementTasklet(@Qualifier("tossSettlementWebClient") WebClient webClient, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
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
        }

        String sql = """
                INSERT INTO TOSS_PAYMENT_DAILY_SNAPSHOT_ENTITY (
                    order_id, payment_key, transaction_id, method, approved_at,
                    amount, intertest_fee, fee, supply_amount, vat, pay_out_amount
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (JsonNode settlement : allSettlements) {
            String orderId = settlement.path("orderId").asText(null);
            String paymentKey = settlement.path("paymentKey").asText(null);
            String transactionKey = settlement.path("transactionKey").asText(null);
            String method = settlement.path("method").asText(null);
            String approvedAt = settlement.path("approvedAt").asText(null);
            Long amount = settlement.path("amount").asLong(0);
            Long interestFee = settlement.path("interestFee").asLong(0);
            Long fee = settlement.path("fee").asLong(0);
            Long supplyAmount = settlement.path("supplyAmount").asLong(0);
            Long vat = settlement.path("vat").asLong(0);
            Long payOutAmount = settlement.path("payOutAmount").asLong(0);


            jdbcTemplate.update(sql,
                    orderId, paymentKey, transactionKey, method, approvedAt,
                    amount, interestFee, fee, supplyAmount, vat, payOutAmount);
        }

        return RepeatStatus.FINISHED;
    }
}