package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.UsuarioRequest;
import edu.upb.barber.repository.dto.response.UsuarioResponse;
import edu.upb.barber.service.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar usuarios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar usuario con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody UsuarioRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar usuario", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable String id, @RequestBody UsuarioRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar usuario con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar usuario con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
