package edu.upb.barber.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upb.barber.repository.dto.request.NotificacionRequestDto;
import edu.upb.barber.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stereum")
public class StereumController {

    @Value("${stereum.api-key}")
    private String secretKey;

    private final PagoService pagoService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> recibirNotificacion(
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-Timestamp") long timestamp,
            @RequestBody byte[] rawBody) {

        String bodyStr = new String(rawBody, StandardCharsets.UTF_8);

        // Verificar firma HMAC-SHA256 usando bytes crudos
        String hmacEsperado = new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
                secretKey.getBytes(StandardCharsets.UTF_8))
                .hmacHex(rawBody);

        if (!hmacEsperado.equals(signature)) {
            log.warn("Webhook de Stereum rechazado: firma invalida.");
            log.warn("-> Esperada : {}", hmacEsperado);
            log.warn("-> Recibida : {}", signature);
            log.warn("-> Body     : {}", bodyStr);
            
            // Si es un evento de prueba de Stereum (ping/validacion), a veces
            // la firma puede fallar por diferencias de secretos. Vamos a 
            // rechazarlo pero con log claro.
            return ResponseEntity.status(401).build();
        }

        // Verificar que el timestamp no sea mayor a 5 minutos (anti-replay)
        if (Instant.now().getEpochSecond() - timestamp > 300) {
            if (bodyStr.contains("\"notification_type\":\"test\"")) {
                log.info("Timestamp expirado pero es un evento de prueba. Se permite el paso.");
            } else {
                log.warn("Webhook de Stereum rechazado: timestamp expirado. Timestamp={}", timestamp);
                return ResponseEntity.status(400).build();
            }
        }

        // Deserializar el payload
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        NotificacionRequestDto notificacion;
        try {
            notificacion = objectMapper.readValue(rawBody, NotificacionRequestDto.class);
        } catch (Exception e) {
            log.error("Error al deserializar el cuerpo del webhook de Stereum: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        log.info("Webhook de Stereum recibido y validado: tipo={}, transaccionId={}",
                notificacion.getNotificationType(),
                notificacion.getTransaction() != null ? notificacion.getTransaction().getId() : "null");

        // Procesar la notificacion y actualizar el estado del pago
        try {
            pagoService.procesarNotificacionWebhook(notificacion);
        } catch (Exception e) {
            log.error("Error al procesar la notificacion del webhook de Stereum: {}", e.getMessage(), e);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok().build();
    }
}