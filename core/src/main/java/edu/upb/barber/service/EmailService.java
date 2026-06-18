package edu.upb.barber.service;

import edu.upb.barber.service.emailConfig.MailContentBuilder;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {
    private static final String BANNER_PNG = "images/logo_klipp_transparente.png";
    private static final String LINKEDIN_PNG = "images/linkedin@2x.png";
    private static final String X_PNG = "images/twitter@2x.png";

    @Value("${mail.smtp.from-mail}")
    private String mailFrom;
    @Value("${mail.smtp.mail-noreply}")
    private String mailNoreply;
    private final MailContentBuilder mailContentBuilder;
    @Autowired
    @Qualifier("javaMailSender")
    private JavaMailSender javaMailSender;

    @Async("taskLog")
    public void sendPassword(String to, String password) {
        log.info("Enviando email a: " + to);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("Cita Confirmada");
            String message = mailContentBuilder.sendPassword(password);

            messageHelper.setText(message, true);
            messageHelper.addInline("banner", new ClassPathResource(BANNER_PNG));
            messageHelper.addInline("imageLinkedin", new ClassPathResource(LINKEDIN_PNG));
            messageHelper.addInline("imageX", new ClassPathResource(X_PNG));
        };
        javaMailSender.send(messagePreparator);
        log.info("Email enviado a: " + to);
    }
}
