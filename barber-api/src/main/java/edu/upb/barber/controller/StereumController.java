package edu.upb.barber.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upb.barber.repository.dto.request.NotificacionRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;

import static org.springframework.http.ResponseEntity.ok;
// Controlador para manejar las notificaciones de Stereum
//Tras que stereum diga pagado, se supone que generamos factura y se marca la cita como pagada
@RestController
@Slf4j
@Controller
@RequestMapping("/api/v1/stereum")
public class StereumController {

    @Value("${stereum.api-secret}")
    private String secretKey;
    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> outbound(
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-Timestamp") int tiempo,
            @RequestBody String body) throws Exception {

        log.info("Llego aca");
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
                secretKey.getBytes(StandardCharsets.UTF_8))
                .hmacHex(body.getBytes(StandardCharsets.UTF_8));
        if (!signature.equals(hmac)) {
            throw new Exception("MessageCode.SIGN_REQUEST_INVALID");
        }

        if(Instant.now().getEpochSecond() - tiempo > 300) {
            throw new Exception("MessageCode.REQUEST_TIMEOUT");
        }


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NotificacionRequestDto notificacionRequestDto = objectMapper.readValue(body, NotificacionRequestDto.class);



        try {
            log.info("Notificación recibida: {}", notificacionRequestDto);
            return ok().build();
        } catch (Exception e) {
            throw e ;
        }
    }
}