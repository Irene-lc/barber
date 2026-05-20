package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Mascota;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MascotaResponse {
    private String id;
    private String clienteId;
    private String clienteNombre;
    private String nombre;
    private String especie;
    private String raza;
    private String notasEspeciales;
    private boolean activa;

    public static MascotaResponse fromEntity(Mascota m) {
        MascotaResponse dto = new MascotaResponse();
        dto.id = m.getId();
        if (m.getCliente() != null) {
            dto.clienteId = m.getCliente().getId();
            dto.clienteNombre = m.getCliente().getNombre();
        }
        dto.nombre = m.getNombre();
        dto.especie = m.getEspecie();
        dto.raza = m.getRaza();
        dto.notasEspeciales = m.getNotasEspeciales();
        dto.activa = m.isActiva();
        return dto;
    }
}
