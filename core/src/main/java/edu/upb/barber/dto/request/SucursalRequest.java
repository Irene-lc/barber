package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SucursalRequest {
    private String empresaId;
    private String nombre;
    private String direccion;
    private String telefono;
    private String zonaHoraria = "America/La_Paz";
    private boolean activa = true;
}
