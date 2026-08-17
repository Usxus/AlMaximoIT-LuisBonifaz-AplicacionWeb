# AlMaximoIT-LuisBonifaz-AplicacionWeb
### Sistema Web de Gestión de Productos y API RESTful con Spring Boot 3, Java 17, Thymeleaf, Stored Procedures (MySQL / H2) y Swagger UI.

**Candidato**: Luis Bonifaz  
**Stack**: Java 17 | Spring Boot 3.2.3 | Spring JDBC (Stored Procedures) | Thymeleaf | Bootstrap 5 | MySQL 8.0 | H2 Database | OpenAPI 3 (Swagger) | JUnit 5 & MockMvc  

---

## Arquitectura y Características Técnicas

* **Arquitectura Híbrida**: Interfaz Web MVC responsiva con Thymeleaf + API REST documentada con Swagger UI (/swagger-ui.html).
* **Persistencia 100% por Stored Procedures**: Invocación de 8 procedimientos almacenados transaccionales mediante SimpleJdbcCall y JdbcTemplate.
* **Soporte Dual de Motores de BD**:
  * **MySQL 8.0**: Motor físico para entorno de producción/desarrollo con soporte de reactivación automática de claves inactivas y Soft Delete.
  * **H2 Database**: Motor en memoria para pruebas e inicialización autónoma con cero configuración.
* **Manejo Centralizado de Excepciones**: RestControllerAdvice con respuestas JSON estandarizadas (RFC 7807) y bitácora estructurada de logs (logs/almaximoit-YYYY-MM-DD.log).
* **Suite de Pruebas Automatizadas**: 16 pruebas de integración (16/16 PASS) con JUnit 5 y MockMvc.

---

## Inicio Rápido

### 1. Iniciar con Base de Datos en Memoria (H2 - Cero Configuración)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### 2. Iniciar con Base de Datos MySQL
1. Ejecute el script SQL db/schema_and_procedures_mysql.sql en su servidor MySQL.
2. Inicie el servicio:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## Compilación y Pruebas

```bash
# Ejecutar suite de 16 pruebas automatizadas
mvn test

# Compilar paquete WAR para producción (Tomcat 10)
mvn clean package -DskipTests
```
*El archivo empaquetado para distribución se genera en: dist/AlMaximoIT-LuisBonifaz.war.*

---

## URLs Principales del Sistema

| Módulo / Servicio | URL |
|---|---|
| **Catálogo Web de Productos** | http://localhost:8080/AlMaximoIT-LuisBonifaz/productos |
| **Documentación Interactiva Swagger** | http://localhost:8080/AlMaximoIT-LuisBonifaz/swagger-ui.html |
| **Estado y Diagnóstico de BD** | http://localhost:8080/AlMaximoIT-LuisBonifaz/api/status |
| **Consola H2** *(En perfil H2)* | http://localhost:8080/AlMaximoIT-LuisBonifaz/h2-console |

---

## Documentación Técnica Detallada

* [MANUAL_INSTALACION.md](MANUAL_INSTALACION.md): Guía de despliegue en Apache Tomcat 10, ejecución por JAR y configuración de perfiles JDBC.
* [MANUAL_USUARIO.md](MANUAL_USUARIO.md): Guía de usuario para el catálogo web, modales, pestañas interactivas y referencia de endpoints REST.
* [Manual de Usuario.pdf](Manual%20de%20Usuario.pdf): Interfaz visual de uso y operación de la aplicación web con capturas de pantalla.
* [db/schema_and_procedures_mysql.sql](db/schema_and_procedures_mysql.sql): Script DDL, Stored Procedures y datos de prueba para MySQL 8.0.
