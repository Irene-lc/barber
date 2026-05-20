package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String usuarioId;
    private String nombre;
    private String telefono;
    private String email;
    private String documento;
    private String notas;
    private boolean activo;

    public static ClienteResponse fromEntity(Cliente c) {
        ClienteResponse dto = new ClienteResponse();
        dto.id = c.getId();
        if (c.getEmpresa() != null) {
            dto.empresaId = c.getEmpresa().getId();
            dto.empresaNombre = c.getEmpresa().getNombre();
        }
        if (c.getUsuario() != null) {
            dto.usuarioId = c.getUsuario().getId();
        }
        dto.nombre = c.getNombre();
        dto.telefono = c.getTelefono();
        dto.email = c.getEmail();
        dto.documento = c.getDocumento();
        dto.notas = c.getNotas();
        dto.activo = c.isActivo();
        return dto;
    }
}
