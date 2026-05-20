package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {
    private String empresaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private boolean activo = true;
}
