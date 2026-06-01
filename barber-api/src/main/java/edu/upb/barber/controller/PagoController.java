package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.request.StereumDto;
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

    @PostMapping("/crear-cargo")
    public ResponseEntity<StereumDto> crearCargo(@RequestBody PagoRequestDto request) {
        try {
            StereumDto response = pagoService.crearCargo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al crear cargo en Stereum", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}