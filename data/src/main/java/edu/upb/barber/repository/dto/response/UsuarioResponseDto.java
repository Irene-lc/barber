package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDto {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private boolean activo;

    public UsuarioResponseDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol();
        this.activo = usuario.isActivo();
    }

    public UsuarioResponseDto(
            String id,
            String nombre,
            String email
    ) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

}