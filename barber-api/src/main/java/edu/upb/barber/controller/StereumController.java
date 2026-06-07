package edu.upb.barber.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upb.barber.repository.dto.request.NotificacionRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stereum")
public class StereumController {
    @Value("${stereum.secret-key}")
    private String apiKey;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> outbound(
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-Timestamp") int tiempo,
            @RequestBody String body) throws Exception {

        System.out.println("llego aca");
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
                apiKey.getBytes(StandardCharsets.UTF_8))
                .hmacHex(body.getBytes(StandardCharsets.UTF_8));

        // verificar la firma
        if (!signature.equals(hmac)) {
            throw new Exception("MessageCode.SIGN_REQUEST_INVALID");
        }
        // verificar si desde el momento que Stereum envió han pasado más de 5 minutos
        if ((((int) System.currentTimeMillis() / 1000) - tiempo ) > 300) {
            throw new Exception("Se paso");
        }
       // convertir json en Objeto
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NotificacionRequestDto notificacion = objectMapper.readValue(body, NotificacionRequestDto.class);

        try {
            log.info("Not: {}", notificacion.getTransaction().getStatus());
            return ok().build();
        } catch (Exception e) {
            throw e ;
        }
    }
}
