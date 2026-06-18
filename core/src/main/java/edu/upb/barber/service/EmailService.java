package edu.upb.barber.service;

import edu.upb.barber.config.MailContentBuilder;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    @Value("${mail.smtp.from-mail}")
    private String mailFrom;
    @Value("${mail.smtp.mail-noreply}")
    private String mailNoreply;
    private final MailContentBuilder mailContentBuilder;
    @Autowired
    @Qualifier("javaMailSender")
    private JavaMailSender javaMailSender;

    @Async("taskLog")
    public void sendPassword(String to, String nombre, String password) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("KLIPP — Bienvenido al equipo");
            messageHelper.setText(mailContentBuilder.sendPassword(nombre, password), true);
        };
        javaMailSender.send(messagePreparator);
    }

    @Async("taskLog")
    public void sendCitaConfirmacion(String to, String clienteNombre, String fecha, String hora,
                                     java.util.List<String> servicios,
                                     String empleadoNombre, String sucursalNombre) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("KLIPP — Tu cita está confirmada ✓");
            messageHelper.setText(mailContentBuilder.sendCitaConfirmacion(clienteNombre, fecha, hora, servicios, empleadoNombre, sucursalNombre), true);
        };
        javaMailSender.send(messagePreparator);
    }

    @Async("taskLog")
    public void sendResetPassword(String to, String nombre, String resetLink) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("KLIPP — Restablece tu contraseña");
            messageHelper.setText(mailContentBuilder.sendResetPassword(nombre, resetLink), true);
        };
        javaMailSender.send(messagePreparator);
    }
}
