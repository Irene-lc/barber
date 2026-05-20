package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.SucursalRequest;
import edu.upb.barber.repository.dto.response.SucursalResponse;
import edu.upb.barber.service.SucursalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/sucursales")
public class SucursalController {
    private final SucursalService service;

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar sucursales", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar sucursal con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody SucursalRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar sucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable String id, @RequestBody SucursalRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar sucursal con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar sucursal con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
