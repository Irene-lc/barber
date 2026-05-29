package edu.upb.barber.integracion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Sistema1CrearEmpresaRequest {
    @JsonProperty("email_empresa")
    private String emailEmpresa;
    private String nit;
    private String nombre;
    @JsonProperty("razon_social")
    private String razonSocial;
}
