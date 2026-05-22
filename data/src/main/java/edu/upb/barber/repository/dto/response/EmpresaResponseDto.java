package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Empresa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaResponseDto {
    private String id;
    private String nombre;
    private boolean activa;
    private String nit;
    @JsonProperty("razon_social")
    private String razon_social;

    public EmpresaResponseDto(Empresa empresa) {
        this.id = empresa.getId();
        this.nombre = empresa.getNombre();
//        this.nit =empresa.getNit();
//        this.activa = empresa.isActiva();
//        this.razon_social = empresa.getRazonSocial();

    }

    public EmpresaResponseDto(String id, String nombre, boolean activa, String nit, String razon_social) {
        this.id = id;
        this.nombre = nombre;
        this.activa = activa;
        this.nit = nit;
        this.razon_social = razon_social;
    }

    public EmpresaResponseDto(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public EmpresaResponseDto(String nombre) {
        this.nombre = nombre;
    }

}
