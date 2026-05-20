package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.MascotaRequest;
import edu.upb.barber.repository.dto.response.MascotaResponse;
import edu.upb.barber.service.MascotaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/mascotas")
public class MascotaController {
    private final MascotaService service;

    @GetMapping
    public ResponseEntity<List<MascotaResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar mascotas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar mascota con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody MascotaRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable String id, @RequestBody MascotaRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar mascota con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar mascota con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
