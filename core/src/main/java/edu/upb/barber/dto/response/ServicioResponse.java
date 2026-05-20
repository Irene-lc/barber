package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicioResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String nombre;
    private String descripcion;
    private TipoDestinatarioServicio destinatario;
    private CategoriaServicio categoria;
    private int duracionMinutos;
    private BigDecimal precioBase;
    private boolean activo;

    public static ServicioResponse fromEntity(Servicio s) {
        ServicioResponse dto = new ServicioResponse();
        dto.id = s.getId();
        if (s.getEmpresa() != null) {
            dto.empresaId = s.getEmpresa().getId();
            dto.empresaNombre = s.getEmpresa().getNombre();
        }
        dto.nombre = s.getNombre();
        dto.descripcion = s.getDescripcion();
        dto.destinatario = s.getDestinatario();
        dto.categoria = s.getCategoria();
        dto.duracionMinutos = s.getDuracionMinutos();
        dto.precioBase = s.getPrecioBase();
        dto.activo = s.isActivo();
        return dto;
    }
}
