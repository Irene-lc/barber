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

    @JsonProperty("empresa_id")
    private String empresa;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
}