package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Servicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServicioResponseDto {

    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracion;
    private String empresaId;
    private String empresaNombre;
    private String destinatario;
    private String categoria;
    private boolean activo;

    public ServicioResponseDto(Servicio servicio) {
        this.id = servicio.getId();
        this.nombre = servicio.getNombre();
        this.descripcion = servicio.getDescripcion();
        this.precio = servicio.getPrecioBase() != null ? servicio.getPrecioBase().doubleValue() : 0.0;
        this.duracion = servicio.getDuracionMinutos();
        if (servicio.getEmpresa() != null) {
            this.empresaId = servicio.getEmpresa().getId();
            this.empresaNombre = servicio.getEmpresa().getNombre();
        }
        this.destinatario = servicio.getDestinatario() != null ? servicio.getDestinatario().name() : null;
        this.categoria = servicio.getCategoria() != null ? servicio.getCategoria().name() : null;
        this.activo = servicio.isActivo();
    }
}
