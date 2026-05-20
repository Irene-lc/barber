package edu.upb.barber.dto.request;

import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String empresaId;
    private String nombre;
    private String apellido;
    private String email;
    private String passwordHash;
    private RolUsuario rol;
    private boolean activo = true;
}
