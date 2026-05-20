package edu.upb.barber.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaRequest {
    private String nombre;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String email;
    private boolean activa = true;
}
