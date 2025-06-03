package startwithco.tosssnapshot.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

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
}