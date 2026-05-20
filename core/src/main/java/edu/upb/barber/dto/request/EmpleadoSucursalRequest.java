package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoSucursalRequest {
    private String empleadoId;
    private String sucursalId;
    private boolean activo = true;
}
