package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.VentaDetalleRequest;
import edu.upb.barber.repository.dto.response.VentaDetalleResponse;
import edu.upb.barber.service.VentaDetalleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/ventas-detalle")
public class VentaDetalleController {
    private final VentaDetalleService service;

    @GetMapping
    public ResponseEntity<List<VentaDetalleResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar detalles de venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDetalleResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar detalle de venta con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody VentaDetalleRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar detalle de venta", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable String id, @RequestBody VentaDetalleRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar detalle de venta con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar detalle de venta con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
