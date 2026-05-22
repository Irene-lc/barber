package edu.upb.barber.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter implements Serializable {



    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {

            filterChain.doFilter(servletRequest, servletResponse);

        }catch (Exception e) {
            log.error("Se genero una exepción generica al validar el JWT", e);
            servletResponse.setContentType(MediaType.APPLICATION_JSON.getType());
            servletResponse.getWriter().write(new tools.jackson.databind.ObjectMapper().writeValueAsString(HttpStatus.INTERNAL_SERVER_ERROR));
            servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public String createToken(Authentication authentication) {
        // 1. Obtener el usuario principal autenticado
        org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        // 2. Definir una clave secreta para firmar el token (mínimo 32 caracteres)
        String secretKeyString = "MiClaveSecretaSuperSeguraYMuyLargaParaEvitarErroresDeFirma123!";
        java.security.Key key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKeyString.getBytes());

        // 3. Configurar tiempos (Expira en 24 horas)
        long currentTimeMillis = System.currentTimeMillis();
        java.util.Date now = new java.util.Date(currentTimeMillis);
        java.util.Date expiration = new java.util.Date(currentTimeMillis + 86400000);

        // 4. Construir el JWT compacto
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(userDetails.getUsername()) // Guarda el email/usuario
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }
}
