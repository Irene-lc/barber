package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.enums.TipoEmpresa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SucursalResponseDto {

    private String id;
    private String nombre;
    private String direccion;
    private String telefono;

    @JsonProperty("empresa_id")
    private String empresaId;

    @JsonProperty("empresa_nombre")
    private String empresaNombre;

    @JsonProperty("tipo_empresa")
    private TipoEmpresa tipoEmpresa;

    private boolean activo;
}
