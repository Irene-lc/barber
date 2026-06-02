package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EspecieRequestDto;
import edu.upb.barber.repository.dto.response.EspecieResponseDto;
import edu.upb.barber.service.EspecieService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/especies")
@AllArgsConstructor
public class EspecieController {

    private final EspecieService especieService;

    @GetMapping
    public ResponseEntity<List<EspecieResponseDto>> listar() {
        try {
            return ResponseEntity.ok(especieService.listar());
        } catch (Exception e) {
            log.error("Error al listar especies", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecieResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return especieService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Especie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<EspecieResponseDto> guardar(@RequestBody EspecieRequestDto dto) {
        try {
            return ResponseEntity.ok(especieService.guardar(dto));
        } catch (Exception e) {
            log.error("Error al guardar Especie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecieResponseDto> actualizar(
            @PathVariable("id") String id,
            @RequestBody EspecieRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(especieService.update(id, dto));
        } catch (Exception e) {
            log.error("Error al actualizar Especie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            especieService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Especie", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}