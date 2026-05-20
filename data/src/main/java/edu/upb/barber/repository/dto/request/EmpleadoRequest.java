package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmpleadoRequest {
    private String empresaId;
    private String usuarioId;
    private String nombre;
    private String telefono;
    private String email;
    private String cargo;
    private String especialidad;
    private BigDecimal porcentajeComision;
    private BigDecimal pagoFijoMensual;
    private String fotoUrl;
    private boolean disponible = true;
    private boolean activo = true;
}
