package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EmpleadoRequestDto;
import edu.upb.barber.repository.dto.response.EmpleadoResponseDto;
import edu.upb.barber.service.EmpleadoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDto>> listar() {
        try {
            return ResponseEntity.ok(empleadoService.listar());
        } catch (Exception e) {
            log.error("Error al listar empleados", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return empleadoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponseDto> guardar(@RequestBody EmpleadoRequestDto dto) {
        try {
            return ResponseEntity.ok(empleadoService.save(dto));
        } catch (Exception e) {
            log.error("Error al guardar Empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> actualizar(
            @PathVariable("id") String empleadoId,
            @RequestBody EmpleadoRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(empleadoService.update(empleadoId, dto));
        } catch (Exception e) {
            log.error("Error al actualizar Empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            empleadoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
