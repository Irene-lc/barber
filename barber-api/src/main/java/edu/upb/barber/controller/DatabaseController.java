package edu.upb.barber.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/db")
public class DatabaseController {

    private final DataSource dataSource;

    public DatabaseController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                return ResponseEntity.ok("Conexión con BD exitosa");
            }
            return ResponseEntity.status(503).body("Conexión inválida");
        } catch (SQLException e) {
            return ResponseEntity.status(503).body("Error: " + e.getMessage());
        }
    }
}
