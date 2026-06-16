package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.SucursalRequestDto;
import edu.upb.barber.repository.dto.response.SucursalResponseDto;
import edu.upb.barber.service.SucursalService;
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
@RequestMapping("/api/v1/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalResponseDto>> listar() {
        try {
            return ResponseEntity.ok(sucursalService.listar());
        } catch (Exception e) {
            log.error("Error al listar sucursales", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponseDto> obtenerPorId(@PathVariable String id) {
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
    public ResponseEntity<SucursalResponseDto> guardar(@RequestBody SucursalRequestDto dto) {
        try {
            return ResponseEntity.ok(sucursalService.save(dto));
        } catch (OperationException e) {
            log.error("Error al guardar Sucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar Sucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponseDto> actualizar(
            @PathVariable("id") String sucursalId,
            @RequestBody SucursalRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(sucursalService.update(sucursalId, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar Sucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Sucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            sucursalService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Sucursal. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Sucursal", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
