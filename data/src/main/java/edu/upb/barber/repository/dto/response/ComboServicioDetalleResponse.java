package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.ComboServicioDetalle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComboServicioDetalleResponse {
    private String id;
    private String comboServicioId;
    private String comboServicioNombre;
    private String servicioId;
    private String servicioNombre;
    private int ordenEjecucion;

    public static ComboServicioDetalleResponse fromEntity(ComboServicioDetalle c) {
        ComboServicioDetalleResponse dto = new ComboServicioDetalleResponse();
        dto.id = c.getId();
        if (c.getComboServicio() != null) {
            dto.comboServicioId = c.getComboServicio().getId();
            dto.comboServicioNombre = c.getComboServicio().getNombre();
        }
        if (c.getServicio() != null) {
            dto.servicioId = c.getServicio().getId();
            dto.servicioNombre = c.getServicio().getNombre();
        }
        dto.ordenEjecucion = c.getOrdenEjecucion();
        return dto;
    }
}
