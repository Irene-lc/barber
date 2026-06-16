package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.dto.response.VentaResponseDto;
import edu.upb.barber.service.VentaService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDto> crear(@RequestBody VentaRequestDto request) {
        try {
            VentaResponseDto ventaCreada = ventaService.crear(request);
            return ResponseEntity.ok(ventaCreada);
        } catch (OperationException e) {
            log.error("Error al crear Venta. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDto>> listar() {
        try {
            return ResponseEntity.ok(ventaService.listar());
        } catch (Exception e) {
            log.error("Error al listar Ventas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return ventaService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDto> actualizar(@PathVariable String id, @RequestBody VentaRequestDto request) {
        try {
            return ResponseEntity.ok(ventaService.update(id, request));
        } catch (OperationException e) {
            log.error("Error al actualizar Venta. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            ventaService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Venta. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
