package com.almaximoit.productos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la especificación OpenAPI 3 / Swagger para la documentación de la API REST.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlMáximo IT | Sistema de Catálogo de Productos | Luis Bonifaz")
                        .version("1.0.0")
                        .description("API RESTful para la gestión y catálogo de productos y proveedores.")
                        .contact(new Contact()
                                .name("Luis Abiel Bonifaz Armenta")
                                .url("https://www.linkedin.com/in/luis-bonifaz/"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
