# Manual de Instalación y Despliegue Técnico
## Proyecto: AlMaximoIT - Luis Bonifaz (Spring Boot 3 + Java 17)

Guía técnica concisa para configurar, compilar y levantar el servicio en **Modo H2 (En memoria / Cero configuración)** o **Modo MySQL (Base de datos física)**.

---

## 1. Requisitos del Entorno

* **Java**: JDK 17
* **Maven**: Apache Maven 3.8+
* **MySQL**: MySQL Server 8.0+
* **Tomcat**: Apache Tomcat 10.x

---

## 2. Configuración del Motor de Base de Datos

### Opción A: Modo H2 (Demostración Inmediata / Cero Instalación)
No requiere instalar ningún software adicional. La base de datos, tablas, Stored Procedures y datos semilla se inicializan automáticamente en memoria al iniciar la aplicación.

En `src/main/resources/application.properties`:
```properties
spring.profiles.active=h2
```

---

### Opción B: Modo MySQL (Producción / Base de Datos Física)
1. **Ejecutar Script de Inicialización**:
   Abra su consola de MySQL o Workbench y ejecute el archivo:
   `db/schema_and_procedures_mysql.sql`
   *(Crea la BD `db_productos`, tablas relacionales y los Stored Procedures con soporte de reactivación y Soft Delete).*

2. **Configurar Credenciales**:
   En `src/main/resources/application.properties`:
   ```properties
   spring.profiles.active=mysql
   ```
   En `src/main/resources/application-mysql.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/db_productos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true&characterEncoding=UTF-8
   spring.datasource.username=root
   spring.datasource.password=1234
   ```

---

## 3. Formas de Levantar el Servicio

### Método 1: Ejecución Rápida con Maven (Recomendado para Desarrollo)
Ejecuta el servidor embebido en el puerto `8080`:

```bash
# Iniciar con el perfil configurado en application.properties
mvn spring-boot:run

# O forzar perfil H2 por línea de comandos:
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# O forzar perfil MySQL por línea de comandos:
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

### Método 2: Ejecución Directa del Archivo WAR
El archivo WAR es ejecutable de forma autónoma:

```bash
# 1. Compilar y empaquetar
mvn clean package -DskipTests

# 2. Ejecutar con perfil activo
java -jar dist/AlMaximoIT-LuisBonifaz.war

# O indicando el perfil deseado:
java -jar -Dspring.profiles.active=h2 dist/AlMaximoIT-LuisBonifaz.war
java -jar -Dspring.profiles.active=mysql dist/AlMaximoIT-LuisBonifaz.war
```

---

### Método 3: Despliegue en Servidor Externo Apache Tomcat 10.x
1. Genere el WAR: `mvn clean package -DskipTests`.
2. Copie `dist/AlMaximoIT-LuisBonifaz.war` en la carpeta `webapps/` de su Tomcat 10.
3. Inicie Tomcat ejecutando `bin/startup.bat` (Windows) o `bin/startup.sh` (Linux/Mac).

---

## 4. Ejecución de Pruebas Automatizadas (16 Tests)

El proyecto incluye 16 pruebas automatizadas de integración (`ApiEndpointsIntegrationTest`):

```bash
# Ejecución en H2 (16/16 PASS):
mvn test

# Ejecución contra MySQL 8.0:
mvn test "-Dspring.profiles.active=mysql"
```

---

## 5. Verificación de Rutas y Servicios Activos

| Servicio | URL Local (Spring Boot / Maven) | URL en Tomcat Externo |
|---|---|---|
| **Catálogo Web** | `http://localhost:8080/AlMaximoIT-LuisBonifaz/productos` | `http://localhost:8080/AlMaximoIT-LuisBonifaz/productos` |
| **Documentación Swagger** | `http://localhost:8080/AlMaximoIT-LuisBonifaz/swagger-ui.html` | `http://localhost:8080/AlMaximoIT-LuisBonifaz/swagger-ui.html` |
| **Diagnóstico de Salud** | `http://localhost:8080/AlMaximoIT-LuisBonifaz/api/health` | `http://localhost:8080/AlMaximoIT-LuisBonifaz/api/health` |
| **Estado y Motor BD** | `http://localhost:8080/AlMaximoIT-LuisBonifaz/api/status` | `http://localhost:8080/AlMaximoIT-LuisBonifaz/api/status` |
| **Consola H2** *(Perfil H2)* | `http://localhost:8080/AlMaximoIT-LuisBonifaz/h2-console` | `http://localhost:8080/AlMaximoIT-LuisBonifaz/h2-console` |

---

## 6. Solución de Problemas (Troubleshooting)

* **Si la aplicación o la API no levanta desde el IDE**:
  * Verifique la configuración del servidor en su entorno de desarrollo (NetBeans / Eclipse / IntelliJ / STS): diríjase a las opciones de ejecución (`Run > Run Configurations > Server > Tomcat` o `Services > Servers > Apache Tomcat`) y asegúrese de que el servidor Tomcat esté configurado y asociado al proyecto.
  * Como alternativa directa y libre de dependencias de IDE, ejecute el servidor embebido con el comando: `mvn spring-boot:run` desde la terminal.
