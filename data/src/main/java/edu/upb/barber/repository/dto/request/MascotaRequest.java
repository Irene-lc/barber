package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MascotaRequest {
    private String clienteId;
    private String nombre;
    private String especie;
    private String raza;
    private String notasEspeciales;
    private boolean activa = true;
}
