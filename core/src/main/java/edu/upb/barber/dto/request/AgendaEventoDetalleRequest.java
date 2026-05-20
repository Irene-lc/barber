package edu.upb.barber.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgendaEventoDetalleRequest {
    private String agendaEventoId;
    private String servicioId;
    private String comboServicioId;
    @JsonProperty("duracion_estimada_minutos")
    private int duracionEstimadaMinutos;
    private BigDecimal precioAcordado;
    private String notas;
}
