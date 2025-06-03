package startwithco.tosssnapshot.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import startwithco.tosssnapshot.exception.ServerException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static startwithco.tosssnapshot.exception.code.ExceptionCodeMapper.*;
import static startwithco.tosssnapshot.exception.code.ExceptionCodeMapper.getCode;

@Service
@Slf4j
public class CommonService {
    @Value("${toss.payment.secret-key}")
    private String tossPaymentSecretKey;

    private final WebClient webClient;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public CommonService(
            @Qualifier("tossPaymentWebClient") WebClient webClient,
            TemplateEngine templateEngine,
            JavaMailSender javaMailSender,
            ObjectMapper objectMapper,
            JdbcTemplate jdbcTemplate
    ) {
        this.webClient = webClient;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void sendFailureEmail(String date, Exception cause) {
        try {
            String subject = "토스페이먼츠 정산 오류";

            Context context = new Context();
            context.setVariable("date", date);
            context.setVariable("errorMessage", cause.getMessage());

            String htmlContent = templateEngine.process("email-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setTo("startwith0325@gmail.com");
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));

            javaMailSender.send(mimeMessage);
        } catch (MessagingException mailEx) {
            log.error("📭 이메일 전송 실패: {}", mailEx.getMessage(), mailEx);
        }
    }

    public Mono<JsonNode> cancelTossPaymentApproval(String paymentKey) {
        String encodedSecretKey = Base64.getEncoder()
                .encodeToString((tossPaymentSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cancelReason", "가상 계좌 입금 날짜 초과");

        return webClient.post()
                .uri("/{paymentKey}/cancel", paymentKey)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                .header("Idempotency-Key", paymentKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .<JsonNode>handle((responseBody, sink) -> {
                    try {
                        sink.next(objectMapper.readTree(responseBody));
                    } catch (Exception e) {
                        sink.error(new ServerException(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "결제 응답 파싱 중 오류가 발생했습니다.",
                                getCode("결제 응답 파싱 중 오류가 발생했습니다.", ExceptionType.SERVER)
                        ));
                    }
                })
                .doOnSuccess(json -> log.info("✅ 결제 취소 성공: {}", json))
                .doOnError(WebClientResponseException.class, err -> {
                    throw new ServerException(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            err.getMessage(),
                            getCode(err.getMessage(), ExceptionType.SERVER)
                    );
                })
                .onErrorResume(err -> {
                    return Mono.error(new ServerException(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            err.getMessage(),
                            getCode(err.getMessage(), ExceptionType.SERVER)
                    ));
                });
    }

    @Transactional
    public void cancelExpiredVirtualAccounts() {
        LocalDateTime now = LocalDateTime.now();

        String selectSql = """
                    SELECT order_id, payment_key
                    FROM payment_entity
                    WHERE method = 'VIRTUAL_ACCOUNT'
                      AND payment_status = 'WAITING_FOR_DEPOSIT'
                      AND due_date < ?
                """;

        List<Map<String, Object>> targets = jdbcTemplate.queryForList(selectSql, now);

        for (Map<String, Object> row : targets) {
            String orderId = (String) row.get("order_id");
            String paymentKey = (String) row.get("payment_key");

            try {
                // 토스 결제 취소 API 호출
                cancelTossPaymentApproval(paymentKey).subscribe();
            } catch (Exception e) {
                log.error("❌ 자동 취소 실패 - orderId: {}, message: {}", orderId, e.getMessage());
            }
        }
    }
}