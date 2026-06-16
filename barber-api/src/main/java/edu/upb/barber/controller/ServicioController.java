package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ServicioRequestDto;
import edu.upb.barber.repository.dto.response.ServicioResponseDto;
import edu.upb.barber.service.ServicioService;
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
@RequestMapping("/api/v1/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponseDto>> listar() {
        try {
            return ResponseEntity.ok(servicioService.listar());
        } catch (Exception e) {
            log.error("Error al listar servicios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return servicioService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Servicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ServicioResponseDto> guardar(@RequestBody ServicioRequestDto dto) {
        try {
            return ResponseEntity.ok(servicioService.save(dto));
        } catch (OperationException e) {
            log.error("Error al guardar Servicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar Servicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDto> actualizar(
            @PathVariable("id") String servicioId,
            @RequestBody ServicioRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(servicioService.update(servicioId, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar Servicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Servicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            servicioService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Servicio. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Servicio", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
