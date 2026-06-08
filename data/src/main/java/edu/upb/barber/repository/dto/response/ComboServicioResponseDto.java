package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.ComboServicio;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ComboServicioResponseDto {

    private String id;

    @JsonProperty("empresa_id")
    private String empresaId;

    @JsonProperty("empresa_nombre")
    private String empresaNombre;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    @JsonProperty("duracion_minutos")
    private Integer duracionMinutos;

    private Boolean activo;
    private List<ComboServicioDetalleResponseDto> detalles;

    public ComboServicioResponseDto(ComboServicio combo) {
        this.id = combo.getId();
        if (combo.getEmpresa() != null) {
            this.empresaId = combo.getEmpresa().getId();
            this.empresaNombre = combo.getEmpresa().getNombre();
        }
        this.nombre = combo.getNombre();
        this.descripcion = combo.getDescripcion();
        this.precio = combo.getPrecio();
        this.duracionMinutos = combo.getDuracionMinutos();
        this.activo = combo.isActivo();
    }
}
