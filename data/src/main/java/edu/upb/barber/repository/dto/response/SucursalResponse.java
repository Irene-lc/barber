package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Sucursal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SucursalResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String nombre;
    private String direccion;
    private String telefono;
    private String zonaHoraria;
    private boolean activa;

    public static SucursalResponse fromEntity(Sucursal s) {
        SucursalResponse dto = new SucursalResponse();
        dto.id = s.getId();
        if (s.getEmpresa() != null) {
            dto.empresaId = s.getEmpresa().getId();
            dto.empresaNombre = s.getEmpresa().getNombre();
        }
        dto.nombre = s.getNombre();
        dto.direccion = s.getDireccion();
        dto.telefono = s.getTelefono();
        dto.zonaHoraria = s.getZonaHoraria();
        dto.activa = s.isActiva();
        return dto;
    }
}
