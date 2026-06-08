package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmpleadoSucursalRequestDto {

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    private Boolean activo = true;
}
