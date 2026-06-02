package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.SucursalRequestDto;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.SucursalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<Sucursal>> listar() {
        try {
            return ResponseEntity.ok(
                    sucursalService.listar());
        } catch (Exception e) {
            log.error("Error al listar sucursales", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerPorId(
            @PathVariable String id
    ) {
        try {

            return sucursalService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {

            log.error("Error al obtener Sucursal", e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody Sucursal sucursal
    ) {
        try {

            sucursalService.save(sucursal);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al guardar Sucursal", e);

            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable("id") String sucursalId,
            @RequestBody SucursalRequestDto dto
    ) {
        try {
            sucursalService.update(sucursalId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar Sucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        try {

            sucursalService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al eliminar Sucursal", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
