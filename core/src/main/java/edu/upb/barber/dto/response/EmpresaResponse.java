package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Empresa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaResponse {
    private String id;
    private String nombre;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String email;
    private boolean activa;

    public static EmpresaResponse fromEntity(Empresa e) {
        EmpresaResponse dto = new EmpresaResponse();
        dto.id = e.getId();
        dto.nombre = e.getNombre();
        dto.razonSocial = e.getRazonSocial();
        dto.nit = e.getNit();
        dto.telefono = e.getTelefono();
        dto.email = e.getEmail();
        dto.activa = e.isActiva();
        return dto;
    }
}
