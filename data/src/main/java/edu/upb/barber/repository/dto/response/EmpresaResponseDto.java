package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Empresa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaResponseDto {

    private String id;
    private String nombre;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String email;
    private boolean activo;

    public EmpresaResponseDto(Empresa empresa) {
        this.id = empresa.getId();
        this.nombre = empresa.getNombre();
        this.razonSocial = empresa.getRazonSocial();
        this.nit = empresa.getNit();
        this.telefono = empresa.getTelefono();
        this.email = empresa.getEmail();
        this.activo = empresa.isActivo();
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
