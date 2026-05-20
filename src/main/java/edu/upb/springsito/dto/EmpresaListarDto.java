package edu.upb.springsito.dto;

public record EmpresaListarDto(
        
        String nombre,
        String razonSocial,
        String nit,
        String telefono,
        String email,
        boolean activa) {
}
