package edu.upb.barber.repository.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class SlotDisponibilidadResponseDto {

    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private List<EmpleadoDisponibleResponseDto> empleados;
}
