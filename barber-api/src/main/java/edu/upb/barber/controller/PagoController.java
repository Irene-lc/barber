package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.service.PagoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/generar-qr")
    public ResponseEntity<GenerarPagoResponseDto> generarCobroQR(@RequestBody GenerarPagoRequestDto request) {
        try {
            GenerarPagoResponseDto response = pagoService.generarCobroQR(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar cobro QR", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
