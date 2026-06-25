package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequestDto(
    String nombre,
    String apellido,
    String email,
    String password,
    @JsonProperty("confirm_password") String confirmPassword,
    String telefono,
    String documento,
    @JsonProperty("empresa_id") String empresaId,
    String notas
) {}
