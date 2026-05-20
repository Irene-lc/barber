package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private boolean activo;

    public static UsuarioResponse fromEntity(Usuario u) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.id = u.getId();
        if (u.getEmpresa() != null) {
            dto.empresaId = u.getEmpresa().getId();
            dto.empresaNombre = u.getEmpresa().getNombre();
        }
        dto.nombre = u.getNombre();
        dto.apellido = u.getApellido();
        dto.email = u.getEmail();
        dto.rol = u.getRol();
        dto.activo = u.isActivo();
        return dto;
    }
}
