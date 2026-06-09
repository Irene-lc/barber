package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.response.DisponibilidadAgendaResponseDto;
import edu.upb.barber.service.DisponibilidadAgendaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/agenda-disponibilidad")
public class DisponibilidadAgendaController {

    private final DisponibilidadAgendaService disponibilidadAgendaService;

    @GetMapping
    public ResponseEntity<DisponibilidadAgendaResponseDto> consultar(
            @RequestParam("sucursal_id") String sucursalId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(value = "empleado_id", required = false) String empleadoId
    ) {
        try {
            return ResponseEntity.ok(disponibilidadAgendaService.consultar(sucursalId, fecha, empleadoId));
        } catch (Exception e) {
            log.error("Error al consultar disponibilidad de agenda", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
