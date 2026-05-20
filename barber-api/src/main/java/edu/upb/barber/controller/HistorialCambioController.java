package edu.upb.barber.controller;

import edu.upb.barber.dto.request.HistorialCambioRequest;
import edu.upb.barber.dto.response.HistorialCambioResponse;
import edu.upb.barber.service.HistorialCambioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/historial")
public class HistorialCambioController {
    private final HistorialCambioService service;

    @GetMapping
    public ResponseEntity<List<HistorialCambioResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar historial de cambios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialCambioResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar historial con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody HistorialCambioRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar historial", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
