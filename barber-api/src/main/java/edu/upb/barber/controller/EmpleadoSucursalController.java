package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EmpleadoSucursalRequestDto;
import edu.upb.barber.repository.dto.response.EmpleadoSucursalResponseDto;
import edu.upb.barber.service.EmpleadoSucursalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/empleados-sucursales")
public class EmpleadoSucursalController {

    private final EmpleadoSucursalService empleadoSucursalService;

    @GetMapping
    public ResponseEntity<List<EmpleadoSucursalResponseDto>> listar() {
        try {
            return ResponseEntity.ok(empleadoSucursalService.listar());
        } catch (Exception e) {
            log.error("Error al listar EmpleadoSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoSucursalResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return empleadoSucursalService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener EmpleadoSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<EmpleadoSucursalResponseDto> crear(@RequestBody EmpleadoSucursalRequestDto dto) {
        try {
            return ResponseEntity.ok(empleadoSucursalService.save(dto));
        } catch (Exception e) {
            log.error("Error al crear EmpleadoSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoSucursalResponseDto> actualizar(
            @PathVariable("id") String id,
            @RequestBody EmpleadoSucursalRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(empleadoSucursalService.update(id, dto));
        } catch (Exception e) {
            log.error("Error al actualizar EmpleadoSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            empleadoSucursalService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar EmpleadoSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
