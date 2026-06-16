package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.MascotaRequestDto;
import edu.upb.barber.repository.dto.response.MascotaResponseDto;
import edu.upb.barber.service.MascotaService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        } catch (OperationException e) {
            log.error("Error al guardar Mascota. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar Mascota", e);
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
        } catch (OperationException e) {
            log.error("Error al actualizar Mascota. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            mascotaService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Mascota. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Mascota", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
