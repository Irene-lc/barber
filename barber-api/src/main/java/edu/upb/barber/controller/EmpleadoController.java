package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EmpleadoRequestDto;
import edu.upb.barber.repository.entity.Empleado;
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
    public ResponseEntity<List<Empleado>> listar() {
        try {
            return ResponseEntity.ok(
                    empleadoService.listar());
        } catch (Exception e) {
            log.error("Error al listar empleados", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerPorId(
            @PathVariable String id
    ) {
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
    public ResponseEntity<Void> guardar(
            @RequestBody Empleado empleado
    ) {
        try {

            empleadoService.save(empleado);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al guardar Empleado", e);

            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable("id") String empleadoId,
            @RequestBody EmpleadoRequestDto dto
    ) {
        try {
            empleadoService.update(empleadoId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar Empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        try {

            empleadoService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al eliminar Empleado", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
