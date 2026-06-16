package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ComboServicioRequestDto;
import edu.upb.barber.repository.dto.response.ComboServicioResponseDto;
import edu.upb.barber.service.ComboServicioService;
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
@RequestMapping("/api/v1/combos")
public class ComboServicioController {

    private final ComboServicioService comboServicioService;

    @GetMapping
    public ResponseEntity<List<ComboServicioResponseDto>> listar() {
        try {
            return ResponseEntity.ok(comboServicioService.listar());
        } catch (Exception e) {
            log.error("Error al listar ComboServicios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComboServicioResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return comboServicioService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener ComboServicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ComboServicioResponseDto> crear(@RequestBody ComboServicioRequestDto dto) {
        try {
            return ResponseEntity.ok(comboServicioService.save(dto));
        } catch (OperationException e) {
            log.error("Error al crear ComboServicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear ComboServicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComboServicioResponseDto> actualizar(
            @PathVariable("id") String comboId,
            @RequestBody ComboServicioRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(comboServicioService.update(comboId, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar ComboServicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar ComboServicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            comboServicioService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar ComboServicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar ComboServicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
