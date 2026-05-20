package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {
    private String empresaId;
    private String usuarioId;
    private String nombre;
    private String telefono;
    private String email;
    private String documento;
    private String notas;
    private boolean activo = true;
}
