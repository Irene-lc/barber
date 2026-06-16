package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.RazaRequestDto;
import edu.upb.barber.repository.dto.response.RazaResponseDto;
import edu.upb.barber.service.RazaService;
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
@RequestMapping("/api/v1/razas")
@AllArgsConstructor
public class RazaController {

    private final RazaService razaService;

    @GetMapping
    public ResponseEntity<List<RazaResponseDto>> listar() {
        try {
            return ResponseEntity.ok(razaService.listar());
        } catch (Exception e) {
            log.error("Error al listar razas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RazaResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return razaService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Raza", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<RazaResponseDto> guardar(@RequestBody RazaRequestDto dto) {
        try {
            return ResponseEntity.ok(razaService.guardar(dto));
        } catch (OperationException e) {
            log.error("Error al guardar Raza. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar Raza", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RazaResponseDto> actualizar(
            @PathVariable("id") String id,
            @RequestBody RazaRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(razaService.update(id, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar Raza. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Raza", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            razaService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Raza. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Raza", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
