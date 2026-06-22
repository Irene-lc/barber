package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.enums.TipoEmpresa;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaResponseDto {

    private String id;
    private String nombre;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String email;
    private boolean activo;

    @JsonProperty("tipo")
    private TipoEmpresa tipoEmpresa;

    public EmpresaResponseDto(Empresa empresa) {
        this.id = empresa.getId();
        this.nombre = empresa.getNombre();
        this.razonSocial = empresa.getRazonSocial();
        this.nit = empresa.getNit();
        this.telefono = empresa.getTelefono();
        this.email = empresa.getEmail();
        this.activo = empresa.getActivo();
        this.tipoEmpresa = empresa.getTipoEmpresa();

    }

    public EmpresaResponseDto(
            String id,
            String nombre,
            String nit
    ) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;

    }
}
