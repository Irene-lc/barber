package edu.upb.barber.repository.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ComboServicioRequestDto {

    private String empresaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracionMinutos;
    private Boolean activo;
}
