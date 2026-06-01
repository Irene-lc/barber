package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Empresa;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
@Getter
public class ClienteRequestDto {

    private EmpresaDto empresa;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String documento;
    private String notas;
    private boolean activo;

    @Data
    public static class EmpresaDto {
        private String id;
    }
}