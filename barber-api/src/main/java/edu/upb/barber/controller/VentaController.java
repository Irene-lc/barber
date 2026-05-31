package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.service.VentaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody VentaRequestDto request) {
        try {
            Venta ventaCreada = ventaService.crear(request);
            return ResponseEntity.ok(ventaCreada);
        } catch (Exception e) {
            log.error("Error al crear Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<Venta>> listar() {
        try {
            return ResponseEntity.ok(ventaService.listar());
        } catch (Exception e) {
            log.error("Error al listar Ventas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable String id) {
        try {
            return ventaService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
