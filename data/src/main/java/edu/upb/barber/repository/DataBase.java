package edu.upb.barber.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/db")
public class DataBase {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/status")
    public ResponseEntity<String> checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                return ResponseEntity.ok("Conexión con BD exitosa");
            } else {
                return ResponseEntity.status(503).body(" Conexión inválida");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(503).body(" Error: " + e.getMessage());
        }
    }
}