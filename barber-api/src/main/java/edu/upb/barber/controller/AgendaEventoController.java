package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import edu.upb.barber.repository.dto.request.WalkInRequestDto;
import edu.upb.barber.repository.dto.response.AgendaEventoCreateResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoResponseDto;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.service.AgendaEventoService;
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
@RequestMapping("/api/v1/agenda-eventos")
public class AgendaEventoController {

    private final AgendaEventoService agendaEventoService;

    @GetMapping
    public ResponseEntity<List<AgendaEventoResponseDto>> listar() {
        try {
            return ResponseEntity.ok(agendaEventoService.listar());
        } catch (Exception e) {
            log.error("Error al listar AgendaEventos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaEventoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return agendaEventoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener AgendaEvento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<AgendaEventoCreateResponseDto> crear(
            @RequestBody AgendaEventoCreateRequestDto request
    ) {
        try {
            AgendaEventoCreateResponseDto response = agendaEventoService.crear(request);
            return ResponseEntity.ok(response);
        } catch (OperationException e) {
            log.error("Error al crear AgendaEvento. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear AgendaEvento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/walk-in")
    public ResponseEntity<?> registrarWalkIn(
            @RequestBody WalkInRequestDto request
    ) {
        try {
            agendaEventoService.registrarWalkIn(request);
            return ResponseEntity.ok(java.util.Map.of("message", "Walk-in registrado exitosamente"));
        } catch (OperationException e) {
            log.error("Error al registrar Walk-in. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al registrar Walk-in", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaEventoCreateResponseDto> actualizar(
            @PathVariable String id,
            @RequestBody AgendaEventoCreateRequestDto request
    ) {
        try {
            AgendaEventoCreateResponseDto response = agendaEventoService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (OperationException e) {
            log.error("Error al actualizar AgendaEvento. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar AgendaEvento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            agendaEventoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar AgendaEvento. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar AgendaEvento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<AgendaEventoResponseDto> actualizarEstado(
            @PathVariable String id,
            @RequestParam EstadoEvento nuevoEstado
    ) {
        try {
            return ResponseEntity.ok(agendaEventoService.actualizarEstado(id, nuevoEstado));
        } catch (OperationException e) {
            log.error("Error al actualizar estado de AgendaEvento. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar estado de AgendaEvento", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
