package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComboServicioRequest {
    private String empresaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int duracionMinutos;
    private boolean activo = true;
}
