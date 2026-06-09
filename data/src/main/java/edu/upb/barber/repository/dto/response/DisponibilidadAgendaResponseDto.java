package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DisponibilidadAgendaResponseDto {

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    private LocalDate fecha;

    @JsonProperty("duracion_minutos")
    private Integer duracionMinutos;

    private List<SlotDisponibilidadResponseDto> slots;
}
