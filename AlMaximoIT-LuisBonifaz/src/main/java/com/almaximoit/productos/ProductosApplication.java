package com.almaximoit.productos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.time.LocalDate;

@SpringBootApplication
public class ProductosApplication {

    private static final Logger log = LoggerFactory.getLogger(ProductosApplication.class);

    public static void main(String[] args) {
        try {
            File userDir = new File(System.getProperty("user.dir", "."));
            File logsDir = new File(userDir, "logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            String fecha = LocalDate.now().toString();
            File logFile = new File(logsDir, "almaximoit-" + fecha + ".log");
            String logFilePath = logFile.getAbsolutePath().replace('\\', '/');
            System.setProperty("LOG_FILE", logFilePath);
            System.setProperty("logging.file.name", logFilePath);
        } catch (Exception ignored) {
        }

        SpringApplication.run(ProductosApplication.class, args);
    }

    @Bean
    public CommandLineRunner initLogs() {
        return args -> {
            String fecha = LocalDate.now().toString();
            String defaultPath = "logs/almaximoit-" + fecha + ".log";
            String logPath = System.getProperty("logging.file.name", defaultPath);
            File logFile = new File(logPath);
            if (!logFile.exists()) {
                if (logFile.getParentFile() != null) {
                    logFile.getParentFile().mkdirs();
                }
                logFile.createNewFile();
            }
            log.info("Sistema AlMaximoIT iniciado correctamente. Archivo de log activo en: {}", logFile.getAbsolutePath());
        };
    }
}
