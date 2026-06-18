package edu.upb.barber.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    private static final long EXPIRY_MILLIS = 60 * 60 * 1000L; // 1 hora

    private record TokenEntry(String email, Instant expiry) {}

    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();

    public String generarToken(String email) {
        // Limpiar tokens viejos del mismo email
        tokens.entrySet().removeIf(e -> e.getValue().email().equalsIgnoreCase(email));

        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenEntry(email, Instant.now().plusMillis(EXPIRY_MILLIS)));
        return token;
    }

    public String validarToken(String token) {
        TokenEntry entry = tokens.get(token);
        if (entry == null || Instant.now().isAfter(entry.expiry())) {
            tokens.remove(token);
            return null;
        }
        return entry.email();
    }

    public void invalidarToken(String token) {
        tokens.remove(token);
    }
}
