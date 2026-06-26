package edu.upb.barber.service;

import edu.upb.barber.emailConfig.MailContentBuilder;
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

    @Value("${mail.smtp.from-mail}")
    private String mailFrom;
    @Value("${mail.smtp.mail-noreply}")
    private String mailNoreply;
    private final MailContentBuilder mailContentBuilder;
    @Autowired
    @Qualifier("javaMailSender")
    private JavaMailSender javaMailSender;

    @Async("taskLog")
    public void sendCitaConfirmada(
            String to,
            String clienteNombre,
            String servicioNombre,
            String empleadoNombre,
            String citaFecha,
            String citaHora,
            String sucursalNombre,
            String precioTotal
    ) {
        log.info("Enviando email de confirmacion de cita a: " + to);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("Tu Cita en KLIPP ha sido Confirmada");
            
            String message = mailContentBuilder.buildCitaConfirmada(
                    clienteNombre,
                    servicioNombre,
                    empleadoNombre,
                    citaFecha,
                    citaHora,
                    sucursalNombre,
                    precioTotal
            );

            messageHelper.setText(message, true);
        };
        javaMailSender.send(messagePreparator);
        log.info("Email de confirmacion de cita enviado a: " + to);
    }

    @Async("taskLog")
    public void sendPassword(String to, String password) {
        log.info("Enviando email a: " + to);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("Cita Confirmada");
            String message = mailContentBuilder.sendPassword(password);

            messageHelper.setText(message, true);
        };
        javaMailSender.send(messagePreparator);
        log.info("Email enviado a: " + to);
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

    @Async("taskLog")
    public void sendConfirmationEmail(String to, String clienteNombre, String servicioNombre, String empleadoNombre, String citaFecha, String citaHora, String sucursalNombre, String precioTotal) {
        log.info("Enviando email de confirmación de cita a: " + to);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(to);
            messageHelper.setFrom(new InternetAddress(mailFrom));
            messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
            messageHelper.setSubject("Cita Confirmada");
            String message = mailContentBuilder.sendConfirmation(
                clienteNombre, servicioNombre, empleadoNombre, citaFecha, citaHora, sucursalNombre, precioTotal
            );

            messageHelper.setText(message, true);
        };
        javaMailSender.send(messagePreparator);
        log.info("Email de confirmación de cita enviado a: " + to);
    }

    @Async("taskLog")
    public void cancelacionPedido(String to, String subject, String mensaje) {
        log.info("Enviando email de cancelacion de pedido a: " + to);
        try {
            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                messageHelper.setTo(to);
                messageHelper.setFrom(new InternetAddress(mailFrom));
                messageHelper.setReplyTo(new InternetAddress(mailNoreply, mailNoreply));
                messageHelper.setSubject(subject);
                messageHelper.setText(mensaje, false);
            };
            javaMailSender.send(messagePreparator);
            log.info("Email de cancelacion enviado a: " + to);
        } catch (Exception e) {
            log.error("Error al enviar email de cancelacion", e);
        }
    }
}
