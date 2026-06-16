package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.InventarioSucursalRequestDto;
import edu.upb.barber.repository.dto.response.InventarioSucursalResponseDto;
import edu.upb.barber.service.InventarioSucursalService;
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
@RequestMapping("/api/v1/inventario")
public class InventarioSucursalController {

    private final InventarioSucursalService inventarioSucursalService;

    @GetMapping
    public ResponseEntity<List<InventarioSucursalResponseDto>> listar() {
        try {
            return ResponseEntity.ok(inventarioSucursalService.listar());
        } catch (Exception e) {
            log.error("Error al listar InventarioSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioSucursalResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return inventarioSucursalService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener InventarioSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<InventarioSucursalResponseDto> crear(@RequestBody InventarioSucursalRequestDto dto) {
        try {
            return ResponseEntity.ok(inventarioSucursalService.save(dto));
        } catch (OperationException e) {
            log.error("Error al crear InventarioSucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear InventarioSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioSucursalResponseDto> actualizar(
            @PathVariable("id") String inventarioId,
            @RequestBody InventarioSucursalRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(inventarioSucursalService.update(inventarioId, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar InventarioSucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar InventarioSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            inventarioSucursalService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar InventarioSucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar InventarioSucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
