package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.HorarioEmpleadoRequestDto;
import edu.upb.barber.repository.dto.response.HorarioEmpleadoResponseDto;
import edu.upb.barber.service.HorarioEmpleadoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/horarios-empleados")
public class HorarioEmpleadoController {

    private final HorarioEmpleadoService horarioEmpleadoService;

    @GetMapping
    public ResponseEntity<List<HorarioEmpleadoResponseDto>> listar() {
        try {
            return ResponseEntity.ok(horarioEmpleadoService.listar());
        } catch (Exception e) {
            log.error("Error al listar HorarioEmpleados", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return horarioEmpleadoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener HorarioEmpleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<HorarioEmpleadoResponseDto> crear(@RequestBody HorarioEmpleadoRequestDto dto) {
        try {
            return ResponseEntity.ok(horarioEmpleadoService.save(dto));
        } catch (Exception e) {
            log.error("Error al crear HorarioEmpleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoResponseDto> actualizar(
            @PathVariable("id") String horarioId,
            @RequestBody HorarioEmpleadoRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(horarioEmpleadoService.update(horarioId, dto));
        } catch (Exception e) {
            log.error("Error al actualizar HorarioEmpleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            horarioEmpleadoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar HorarioEmpleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
