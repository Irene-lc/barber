package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.AgendaEventoDetalle;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AgendaEventoDetalleResponseDto {

    private String id;

    @JsonProperty("servicio_id")
    private String servicioId;

    @JsonProperty("servicio_nombre")
    private String servicioNombre;

    @JsonProperty("combo_servicio_id")
    private String comboServicioId;

    @JsonProperty("combo_servicio_nombre")
    private String comboServicioNombre;

    @JsonProperty("duracion_estimada_minutos")
    private Integer duracionEstimadaMinutos;

    @JsonProperty("precio_acordado")
    private BigDecimal precioAcordado;

    private String notas;

    public AgendaEventoDetalleResponseDto(AgendaEventoDetalle detalle) {
        this.id = detalle.getId();
        if (detalle.getServicio() != null) {
            this.servicioId = detalle.getServicio().getId();
            this.servicioNombre = detalle.getServicio().getNombre();
        }
        if (detalle.getComboServicio() != null) {
            this.comboServicioId = detalle.getComboServicio().getId();
            this.comboServicioNombre = detalle.getComboServicio().getNombre();
        }
        this.duracionEstimadaMinutos = detalle.getDuracionEstimadaMinutos();
        this.precioAcordado = detalle.getPrecioAcordado();
        this.notas = detalle.getNotas();
    }
}
