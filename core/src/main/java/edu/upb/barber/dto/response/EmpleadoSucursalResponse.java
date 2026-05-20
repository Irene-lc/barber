package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.EmpleadoSucursal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoSucursalResponse {
    private String id;
    private String empleadoId;
    private String empleadoNombre;
    private String sucursalId;
    private String sucursalNombre;
    private boolean activo;

    public static EmpleadoSucursalResponse fromEntity(EmpleadoSucursal es) {
        EmpleadoSucursalResponse dto = new EmpleadoSucursalResponse();
        dto.id = es.getId();
        if (es.getEmpleado() != null) {
            dto.empleadoId = es.getEmpleado().getId();
            dto.empleadoNombre = es.getEmpleado().getNombre();
        }
        if (es.getSucursal() != null) {
            dto.sucursalId = es.getSucursal().getId();
            dto.sucursalNombre = es.getSucursal().getNombre();
        }
        dto.activo = es.isActivo();
        return dto;
    }
}
