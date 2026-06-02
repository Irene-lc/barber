package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.ComboServicioDetalle;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComboServicioDetalleResponseDto {

    private String id;

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("servicio_nombre")
    private String servicioNombre;

    @JsonProperty("orden_ejecucion")
    private int ordenEjecucion;

    public ComboServicioDetalleResponseDto(ComboServicioDetalle detalle) {
        this.id = detalle.getId();
        if (detalle.getServicio() != null) {
            this.servicioId = detalle.getServicio().getId();
            this.servicioNombre = detalle.getServicio().getNombre();
        }
        this.ordenEjecucion = detalle.getOrdenEjecucion();
    }
}
