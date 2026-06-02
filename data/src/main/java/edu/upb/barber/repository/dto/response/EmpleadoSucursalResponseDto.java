package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.EmpleadoSucursal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmpleadoSucursalResponseDto {

    private String id;

    @JsonProperty("empleado_id")
    private String empleadoId;

    @JsonProperty("empleado_nombre")
    private String empleadoNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    private Boolean activo;

    public EmpleadoSucursalResponseDto(EmpleadoSucursal es) {
        this.id = es.getId();
        if (es.getEmpleado() != null) {
            this.empleadoId = es.getEmpleado().getId();
            this.empleadoNombre = es.getEmpleado().getNombre();
        }
        if (es.getSucursal() != null) {
            this.sucursalId = es.getSucursal().getId();
            this.sucursalNombre = es.getSucursal().getNombre();
        }
        this.activo = es.isActivo();
    }
}
