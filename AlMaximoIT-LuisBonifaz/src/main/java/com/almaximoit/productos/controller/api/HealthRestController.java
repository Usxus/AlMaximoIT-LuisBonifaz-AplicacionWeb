package com.almaximoit.productos.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Health Check / Estado del Servicio", description = "Endpoints para monitoreo y verificación de disponibilidad del microservicio y base de datos.")
public class HealthRestController {

    private static final Logger log = LoggerFactory.getLogger(HealthRestController.class);

    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    public HealthRestController(JdbcTemplate jdbcTemplate, Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.environment = environment;
    }

    /**
     * Endpoint simplificado de Health Check con status booleano y timestamp ISO-8601.
     */
    @GetMapping("/health")
    @Operation(summary = "Verificar estado del servicio (Health Check)", description = "Retorna el estado de disponibilidad simplificado con status booleano y timestamp ISO-8601.")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            jdbcTemplate.execute("SELECT 1");
            response.put("status", true);
            response.put("timestamp", Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Health check falló al consultar la base de datos: {}", e.getMessage(), e);
            response.put("status", false);
            response.put("error", e.getMessage() != null ? e.getMessage() : e.toString());
            response.put("timestamp", Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Endpoint detallado del estado del sistema y estado de motores de base de datos (H2 y MySQL según perfil activo).
     */
    @GetMapping("/status")
    @Operation(summary = "Estado detallado del sistema y bases de datos", description = "Retorna información detallada de la aplicación, perfil activo y estado de conexión de H2 y MySQL.")
    public ResponseEntity<Map<String, Object>> statusCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("application", "AlMaximoIT - Luis Bonifaz");
        response.put("service", "Sistema de Gestión de Productos (MVC & REST)");
        response.put("version", "1.0.0");

        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = (activeProfiles != null && activeProfiles.length > 0) ? activeProfiles[0] : "h2";
        response.put("activeProfile", currentProfile);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String dbTypeReal = "UNKNOWN";
        boolean isConnected = false;
        String errorMessage = null;
        try {
            jdbcTemplate.execute("SELECT 1");
            dbTypeReal = jdbcTemplate.execute((ConnectionCallback<String>) conn ->
                    conn.getMetaData().getDatabaseProductName()
            );
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
            log.error("Fallo de conexión a la base de datos en /api/status: {}", errorMessage, e);
        }

        boolean isH2Active = (dbTypeReal != null && dbTypeReal.toUpperCase().contains("H2")) || currentProfile.equalsIgnoreCase("h2");
        boolean isMySQLActive = (dbTypeReal != null && dbTypeReal.toUpperCase().contains("MYSQL")) || currentProfile.equalsIgnoreCase("mysql");

        Map<String, Object> databases = new LinkedHashMap<>();

        // Estado motor H2
        Map<String, Object> h2Info = new LinkedHashMap<>();
        h2Info.put("type", "H2");
        h2Info.put("descripcion", (isH2Active && isConnected) ? "CONNECTED" : "INACTIVE");
        h2Info.put("status", isH2Active && isConnected);
        if (isH2Active && !isConnected && errorMessage != null) {
            h2Info.put("error", errorMessage);
        }
        databases.put("h2", h2Info);

        // Estado motor MySQL
        Map<String, Object> mysqlInfo = new LinkedHashMap<>();
        mysqlInfo.put("type", "MySQL");
        mysqlInfo.put("descripcion", (isMySQLActive && isConnected) ? "CONNECTED" : "INACTIVE");
        mysqlInfo.put("status", isMySQLActive && isConnected);
        if (isMySQLActive && !isConnected && errorMessage != null) {
            mysqlInfo.put("error", errorMessage);
        }
        databases.put("mysql", mysqlInfo);

        response.put("database", databases);

        return isConnected
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
