package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.ComboServicio;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ComboServicioResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int duracionMinutos;
    private boolean activo;

    public static ComboServicioResponse fromEntity(ComboServicio c) {
        ComboServicioResponse dto = new ComboServicioResponse();
        dto.id = c.getId();
        if (c.getEmpresa() != null) {
            dto.empresaId = c.getEmpresa().getId();
            dto.empresaNombre = c.getEmpresa().getNombre();
        }
        dto.nombre = c.getNombre();
        dto.descripcion = c.getDescripcion();
        dto.precio = c.getPrecio();
        dto.duracionMinutos = c.getDuracionMinutos();
        dto.activo = c.isActivo();
        return dto;
    }
}
