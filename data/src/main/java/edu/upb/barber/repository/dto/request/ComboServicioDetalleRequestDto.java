package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ComboServicioDetalleRequestDto {

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("orden_ejecucion")
    private int ordenEjecucion = 1;
}
