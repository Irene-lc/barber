package edu.upb.springsito.dto;

public record EmpresaGuardarDto(
        String nombre,
        String razonSocial,
        String nit,
        String telefono,
        String email,
        Boolean activa) {
}
