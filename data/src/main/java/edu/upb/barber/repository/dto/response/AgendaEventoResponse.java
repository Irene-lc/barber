package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.AgendaEvento;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AgendaEventoResponse {
    private String id;
    private String clienteId;
    private String clienteNombre;
    private String mascotaId;
    private String mascotaNombre;
    private String sucursalId;
    private String sucursalNombre;
    private String creadoPorUsuarioId;
    private TipoEvento tipoEvento;
    private EstadoEvento estado;
    private OffsetDateTime inicio;
    private OffsetDateTime fin;
    private String notas;

    public static AgendaEventoResponse fromEntity(AgendaEvento a) {
        AgendaEventoResponse dto = new AgendaEventoResponse();
        dto.id = a.getId();
        if (a.getCliente() != null) {
            dto.clienteId = a.getCliente().getId();
            dto.clienteNombre = a.getCliente().getNombre();
        }
        if (a.getMascota() != null) {
            dto.mascotaId = a.getMascota().getId();
            dto.mascotaNombre = a.getMascota().getNombre();
        }
        if (a.getSucursal() != null) {
            dto.sucursalId = a.getSucursal().getId();
            dto.sucursalNombre = a.getSucursal().getNombre();
        }
        if (a.getCreadoPorUsuario() != null) {
            dto.creadoPorUsuarioId = a.getCreadoPorUsuario().getId();
        }
        dto.tipoEvento = a.getTipoEvento();
        dto.estado = a.getEstado();
        dto.inicio = a.getInicio();
        dto.fin = a.getFin();
        dto.notas = a.getNotas();
        return dto;
    }
}
