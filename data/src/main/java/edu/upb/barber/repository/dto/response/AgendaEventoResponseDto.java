package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.AgendaEvento;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class AgendaEventoResponseDto {

    private String id;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("cliente_nombre")
    private String clienteNombre;

    @JsonProperty("mascota_id")
    private String mascotaId;

    @JsonProperty("mascota_nombre")
    private String mascotaNombre;

    @JsonProperty("sucursal_id")
    private String sucursalId;

    @JsonProperty("sucursal_nombre")
    private String sucursalNombre;

    @JsonProperty("tipo_evento")
    private TipoEvento tipoEvento;

    private EstadoEvento estado;
    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private String notas;

    private List<DetalleDto> detalles;
    private List<EmpleadoDto> empleados;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleDto {
        @JsonProperty("servicio_id")
        private String servicioId;
        @JsonProperty("servicio_nombre")
        private String servicioNombre;
        @JsonProperty("precio_acordado")
        private Double precioAcordado;
        @JsonProperty("duracion_estimada_minutos")
        private Integer duracionEstimadaMinutos;

        @JsonProperty("producto_id")
        private String productoId;
        @JsonProperty("producto_nombre")
        private String productoNombre;
        private Integer cantidad;
        private String tipo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpleadoDto {
        @JsonProperty("empleado_id")
        private String empleadoId;
        @JsonProperty("empleado_nombre")
        private String empleadoNombre;
        @JsonProperty("rol_en_evento")
        private String rolEnEvento;
    }

    public AgendaEventoResponseDto(AgendaEvento ae) {
        this.id = ae.getId();
        if (ae.getCliente() != null) {
            this.clienteId = ae.getCliente().getId();
            this.clienteNombre = ae.getCliente().getNombre();
        }
        if (ae.getMascota() != null) {
            this.mascotaId = ae.getMascota().getId();
            this.mascotaNombre = ae.getMascota().getNombre();
        }
        if (ae.getSucursal() != null) {
            this.sucursalId = ae.getSucursal().getId();
            this.sucursalNombre = ae.getSucursal().getNombre();
        }
        this.tipoEvento = ae.getTipoEvento();
        this.estado = ae.getEstado();
        this.inicio = ae.getInicio();
        this.fin = ae.getFin();
        this.notas = ae.getNotas();
    }
}
