package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ComboServicioRequestDto {

    @JsonProperty("empresa_id")
    private String empresaId;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    @JsonProperty("duracion_minutos")
    private Integer duracionMinutos;

    private Boolean activo;
    private List<ComboServicioDetalleRequestDto> detalles;
}
