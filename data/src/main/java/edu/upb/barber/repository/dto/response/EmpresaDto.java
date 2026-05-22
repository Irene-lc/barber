package edu.upb.barber.repository.dto.response;


import edu.upb.barber.repository.entity.Empresa;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDto {
    private String id;
    private Boolean activa;
    private String nit;
    private String nombre;
    private String razon_social;

    public EmpresaDto(Empresa empresa) {
        this.id = empresa.getId();
        this.activa = empresa.isActiva();
        this.nit = empresa.getNit();
        this.nombre = empresa.getNombre();
        this.razon_social = empresa.getRazonSocial();
    }

    public EmpresaDto(String id, String nit) {
        this.id = id;
        this.nit = nit;
    }

    public EmpresaDto(String nit) {
        this.nit = nit;
    }

}
