package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Cliente;
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
    public ResponseEntity<List<Cliente>> listar() {
        try {
            return ResponseEntity.ok(
                    clienteService.listar());
        } catch (Exception e) {
            log.error("Error al listar clientes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(
            @PathVariable String id
    ) {
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
    public ResponseEntity<Void> guardar(
            @RequestBody Cliente cliente
    ) {
        try {

            clienteService.save(cliente);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al guardar Cliente", e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        try {

            clienteService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al eliminar Cliente", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
