package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AuthenticationDto(@JsonAlias({"email", "username"}) String nombre, String password) {
    public String username() {
        return nombre == null ? null : nombre.trim();
    }
}
