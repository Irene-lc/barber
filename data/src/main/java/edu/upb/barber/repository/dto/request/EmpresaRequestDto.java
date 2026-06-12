package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.TipoEmpresa;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaRequestDto {

    private String nombre;

    @JsonProperty("razon_social")
    private String razonSocial;

    private String nit;

    private String telefono;

    private String email;

    private Boolean activo;

    @JsonProperty("tipo_empresa")
    private TipoEmpresa tipoEmpresa;
}
