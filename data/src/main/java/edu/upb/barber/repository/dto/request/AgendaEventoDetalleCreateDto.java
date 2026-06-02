package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgendaEventoDetalleCreateDto {

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("combo_servicio_id")
    private String comboServicioId;

    @JsonProperty("duracion_estimada_minutos")
    private Integer duracionEstimadaMinutos;

    @JsonProperty("precio_acordado")
    private BigDecimal precioAcordado;

    private String notas;
}
