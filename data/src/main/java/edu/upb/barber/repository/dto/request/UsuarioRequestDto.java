package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDto {

    @JsonProperty("empresa_id")
    private String empresaId;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private RolUsuario rol;

    private Boolean activo;

}