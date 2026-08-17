package com.almaximoit.productos;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.time.LocalDate;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        try {
            String realPath = servletContext.getRealPath("/");
            if (realPath != null) {
                File dir = new File(realPath);
                File projectDir = dir;

                // Si está desplegado por NetBeans desde target/AlMaximoIT-LuisBonifaz, apunta a la raíz del proyecto
                if (dir.getName().equalsIgnoreCase("AlMaximoIT-LuisBonifaz") 
                        && dir.getParentFile() != null 
                        && dir.getParentFile().getName().equalsIgnoreCase("target")) {
                    projectDir = dir.getParentFile().getParentFile();
                }

                File logsDir = new File(projectDir, "logs");
                if (!logsDir.exists()) {
                    logsDir.mkdirs();
                }

                String fecha = LocalDate.now().toString();
                File logFile = new File(logsDir, "almaximoit-" + fecha + ".log");
                String logFilePath = logFile.getAbsolutePath().replace('\\', '/');
                System.setProperty("LOG_FILE", logFilePath);
                System.setProperty("logging.file.name", logFilePath);
            }
        } catch (Exception ignored) {
        }

        super.onStartup(servletContext);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProductosApplication.class);
    }
}
