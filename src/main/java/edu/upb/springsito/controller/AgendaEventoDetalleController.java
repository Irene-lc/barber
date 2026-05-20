package edu.upb.springsito.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.upb.springsito.repository.entity.AgendaEventoDetalle;
import edu.upb.springsito.service.AgendaEventoDetalleService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/agenda-evento-detalles")
public class AgendaEventoDetalleController {

    private final AgendaEventoDetalleService service;

    @GetMapping("/listar")
    public ResponseEntity<List<AgendaEventoDetalle>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<AgendaEventoDetalle> guardar(@RequestBody AgendaEventoDetalle entity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}