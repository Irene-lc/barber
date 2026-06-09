package edu.upb.barber.controller;

import edu.upb.barber.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhooks/stereum")
public class StereumWebhookController {

    private static final long MAX_WEBHOOK_AGE_MILLIS = 5 * 60 * 1000;

    private final PagoService pagoService;

    @Value("${stereum.webhook-secret:${stereum.api-key}}")
    private String webhookSecret;

    @PostMapping(
            value = "/outbound",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> outbound(
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-tiempo") long tiempo,
            @RequestBody String body) throws Exception {

        if (webhookExpirado(tiempo)) {
            log.warn("Webhook Stereum rechazado por timestamp expirado: {}", tiempo);
            return ResponseEntity.status(401).build();
        }

        String expectedSignature = hmacSha256Hex(webhookSecret, body);
        if (!firmaValida(signature, expectedSignature)) {
            log.warn("Webhook Stereum rechazado por firma invalida");
            return ResponseEntity.status(401).build();
        }

        pagoService.procesarNotificacionStereum(body);
        return ResponseEntity.ok().build();
    }

    private boolean webhookExpirado(long tiempo) {
        long tiempoMillis = tiempo < 10_000_000_000L ? tiempo * 1000 : tiempo;
        return Math.abs(System.currentTimeMillis() - tiempoMillis) > MAX_WEBHOOK_AGE_MILLIS;
    }

    private String hmacSha256Hex(String secret, String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean firmaValida(String signature, String expectedSignature) {
        return MessageDigest.isEqual(
                signature.getBytes(StandardCharsets.UTF_8),
                expectedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }
}
