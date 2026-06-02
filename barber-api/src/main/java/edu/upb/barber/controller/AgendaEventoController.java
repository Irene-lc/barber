package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import edu.upb.barber.repository.dto.response.AgendaEventoCreateResponseDto;
import edu.upb.barber.service.AgendaEventoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/agenda-eventos")
public class AgendaEventoController {

    private final AgendaEventoService agendaEventoService;

    @PostMapping
    public ResponseEntity<AgendaEventoCreateResponseDto> crear(
            @RequestBody AgendaEventoCreateRequestDto request
    ) {
        try {
            AgendaEventoCreateResponseDto response = agendaEventoService.crear(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al crear AgendaEvento", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
