package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.HorarioEmpleadoFechaRequestDto;
import edu.upb.barber.repository.dto.response.HorarioEmpleadoFechaResponseDto;
import edu.upb.barber.service.HorarioEmpleadoFechaService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/horarios-empleados-fecha")
public class HorarioEmpleadoFechaController {

    private final HorarioEmpleadoFechaService horarioEmpleadoFechaService;

    @GetMapping
    public ResponseEntity<List<HorarioEmpleadoFechaResponseDto>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(horarioEmpleadoFechaService.listar(desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoFechaResponseDto> obtenerPorId(@PathVariable String id) {
        return horarioEmpleadoFechaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HorarioEmpleadoFechaResponseDto> crear(@RequestBody HorarioEmpleadoFechaRequestDto dto) {
        try {
            return ResponseEntity.ok(horarioEmpleadoFechaService.save(dto));
        } catch (OperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear HorarioEmpleadoFecha", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoFechaResponseDto> actualizar(@PathVariable String id, @RequestBody HorarioEmpleadoFechaRequestDto dto) {
        try {
            return ResponseEntity.ok(horarioEmpleadoFechaService.update(id, dto));
        } catch (OperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar HorarioEmpleadoFecha", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            horarioEmpleadoFechaService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar HorarioEmpleadoFecha", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
