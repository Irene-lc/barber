package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EmpresaRequest;
import edu.upb.barber.repository.dto.response.EmpresaResponse;
import edu.upb.barber.service.EmpresaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/empresas")
public class EmpresaController {
    private final EmpresaService service;

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> listarEmpresas() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar empresas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar empresa con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody EmpresaRequest empresa) {
        try {
            service.guardar(empresa);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable String id,
            @RequestBody EmpresaRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar empresa con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar empresa con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
