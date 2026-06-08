package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.repository.dto.response.PagoResponseDto;
import edu.upb.barber.service.PagoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<PagoResponseDto> crear(@RequestBody PagoRequestDto request) {
        try {
            return ResponseEntity.ok(pagoService.crear(request));
        } catch (Exception e) {
            log.error("Error al crear Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDto> actualizar(@PathVariable String id, @RequestBody PagoRequestDto request) {
        try {
            return ResponseEntity.ok(pagoService.update(id, request));
        } catch (Exception e) {
            log.error("Error al actualizar Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            pagoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDto>> listar() {
        try {
            return ResponseEntity.ok(pagoService.listar());
        } catch (Exception e) {
            log.error("Error al listar Pagos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return pagoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
