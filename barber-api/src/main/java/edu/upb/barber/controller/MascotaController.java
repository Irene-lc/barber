package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.MascotaRequestDto;
import edu.upb.barber.repository.dto.response.MascotaResponseDto;
import edu.upb.barber.service.MascotaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/mascotas")
@AllArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<MascotaResponseDto>> listar() {
        try {
            return ResponseEntity.ok(mascotaService.listar());
        } catch (Exception e) {
            log.error("Error al listar mascotas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return mascotaService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDto> guardar(@RequestBody MascotaRequestDto dto) {
        try {
            return ResponseEntity.ok(mascotaService.guardar(dto));
        } catch (Exception e) {
            log.error("Error al guardar Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> actualizar(
            @PathVariable("id") String mascotaId,
            @RequestBody MascotaRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(mascotaService.update(mascotaId, dto));
        } catch (Exception e) {
            log.error("Error al actualizar Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            mascotaService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}