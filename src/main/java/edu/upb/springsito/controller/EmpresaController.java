package edu.upb.springsito.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.upb.springsito.dto.EmpresaGuardarDto;
import edu.upb.springsito.dto.EmpresaListarDto;
import edu.upb.springsito.repository.entity.Empresa;
import edu.upb.springsito.service.EmpresaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/v1/empresas")
public class EmpresaController {

    private final EmpresaService service;

    @GetMapping("/listar")
    public ResponseEntity<List<EmpresaListarDto>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<EmpresaListarDto> guardar(@RequestBody EmpresaGuardarDto entity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al guardar empresa. nit={}", entity.nit(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error inesperado al guardar empresa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/buscar-por-nombre")
    public ResponseEntity<Empresa> buscarPorNombreEmpresa(@RequestBody Map<String, String> body) {
        try {
            String nombre = body.get("nombre");
            if (nombre == null || nombre.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Empresa empresa = service.buscarPorNombreEmpresa(nombre);
            if (empresa != null) {
                return ResponseEntity.ok(empresa);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
