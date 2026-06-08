package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.dto.response.ClienteResponseDto;
import edu.upb.barber.service.ClienteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listar() {
        try {
            return ResponseEntity.ok(clienteService.listar());
        } catch (Exception e) {
            log.error("Error al listar clientes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return clienteService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> guardar(@RequestBody ClienteRequestDto dto) {
        try {
            return ResponseEntity.ok(clienteService.save(dto));
        } catch (Exception e) {
            log.error("Error al guardar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> actualizar(
            @PathVariable("id") String clienteId,
            @RequestBody ClienteRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(clienteService.update(clienteId, dto));
        } catch (Exception e) {
            log.error("Error al actualizar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            clienteService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
