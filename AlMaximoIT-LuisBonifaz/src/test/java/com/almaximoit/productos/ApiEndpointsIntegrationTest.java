package com.almaximoit.productos;

import com.almaximoit.productos.dto.ProductoFormDTO;
import com.almaximoit.productos.dto.ProductoProveedorDTO;
import com.almaximoit.productos.model.Proveedor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite completa de pruebas de integración genérica y dinámica.
 * Valida todos los endpoints REST API, controladores MVC Thymeleaf y procedimientos almacenados (Stored Procedures).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer idTipoProductoDinamico = 1;
    private Integer idProveedorDinamico = 1;
    private Integer idProductoDinamico;
    private String claveProductoDinamico = "SKU-GEN-" + System.currentTimeMillis();

    // -------------------------------------------------------------------------
    // 1. ENDPOINTS DE MONITOREO Y ESTADO
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("1. GET /api/health - Disponibilidad general del microservicio")
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("2. GET /api/status - Estado del servicio y conexión de BD activa")
    public void testStatusCheck() throws Exception {
        mockMvc.perform(get("/api/status").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("AlMaximoIT - Luis Bonifaz"))
                .andExpect(jsonPath("$.database").isMap());
    }

    // -------------------------------------------------------------------------
    // 2. ENDPOINTS DE CATÁLOGOS MAESTROS (STORED PROCEDURES)
    // -------------------------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("3. GET /api/tipos-producto - Listar catálogo y capturar tipo dinámico (sp_listar_tipos_producto)")
    public void testListarTiposProducto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tipos-producto").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        if (root.isArray() && root.size() > 0) {
            idTipoProductoDinamico = root.get(0).get("idTipoProducto").asInt();
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. GET /api/proveedores - Listar catálogo y capturar proveedor dinámico (sp_listar_proveedores)")
    public void testListarProveedores() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/proveedores").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        if (root.isArray() && root.size() > 0) {
            idProveedorDinamico = root.get(0).get("idProveedor").asInt();
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. POST /api/proveedores - Registrar nuevo proveedor genérico (sp_insertar_proveedor)")
    public void testCrearProveedor() throws Exception {
        Proveedor nuevo = new Proveedor(null, "Proveedor Genérico " + System.currentTimeMillis(), "Distribuidor Autorizado");

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProveedor").isNumber())
                .andExpect(jsonPath("$.nombre").value(nuevo.getNombre()));
    }

    // -------------------------------------------------------------------------
    // 3. ENDPOINTS CRUD DE PRODUCTOS (STORED PROCEDURES)
    // -------------------------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("6. GET /api/productos - Listar productos sin filtros (sp_buscar_productos)")
    public void testListarProductosSinFiltro() throws Exception {
        mockMvc.perform(get("/api/productos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @Order(7)
    @DisplayName("7. POST /api/productos - Crear producto dinámico con proveedores (sp_insertar_producto + sp_asignar_proveedor_producto)")
    public void testCrearProducto() throws Exception {
        List<ProductoProveedorDTO> provs = new ArrayList<>();
        provs.add(new ProductoProveedorDTO(null, null, idProveedorDinamico, null, "SKU-PROV-" + System.currentTimeMillis(), new BigDecimal("1250.00")));

        ProductoFormDTO form = new ProductoFormDTO(
                null,
                claveProductoDinamico,
                "Producto Automatizado " + System.currentTimeMillis(),
                new BigDecimal("1999.99"),
                idTipoProductoDinamico,
                provs
        );

        MvcResult result = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.idProducto").isNumber())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        idProductoDinamico = json.get("idProducto").asInt();
    }

    @Test
    @Order(8)
    @DisplayName("8. GET /api/productos?clave=... - Filtrar productos por clave dinámica (sp_buscar_productos)")
    public void testBuscarProductosConFiltro() throws Exception {
        mockMvc.perform(get("/api/productos")
                        .param("clave", claveProductoDinamico)
                        .param("idTipoProducto", String.valueOf(idTipoProductoDinamico))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(9)
    @DisplayName("9. GET /api/productos/{id} - Obtener detalle de producto (sp_obtener_producto_por_id + sp_listar_proveedores_por_producto)")
    public void testObtenerProductoPorId() throws Exception {
        mockMvc.perform(get("/api/productos/" + idProductoDinamico).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(idProductoDinamico))
                .andExpect(jsonPath("$.clave").value(claveProductoDinamico))
                .andExpect(jsonPath("$.proveedores", notNullValue()));
    }

    @Test
    @Order(10)
    @DisplayName("10. POST /api/productos - Actualizar producto dinámico (sp_actualizar_producto + sp_eliminar_proveedores + sp_asignar_proveedor)")
    public void testActualizarProducto() throws Exception {
        List<ProductoProveedorDTO> provs = new ArrayList<>();
        provs.add(new ProductoProveedorDTO(null, null, idProveedorDinamico, null, "SKU-MOD-" + System.currentTimeMillis(), new BigDecimal("1100.00")));

        ProductoFormDTO form = new ProductoFormDTO(
                idProductoDinamico,
                claveProductoDinamico + "-MOD",
                "Producto Automatizado (Actualizado)",
                new BigDecimal("1850.00"),
                idTipoProductoDinamico,
                provs
        );

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/productos/" + idProductoDinamico).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Automatizado (Actualizado)"))
                .andExpect(jsonPath("$.precio").value(1850.00));
    }

    // -------------------------------------------------------------------------
    // 4. VISTAS WEB MVC THYMELEAF Y FORMULARIOS
    // -------------------------------------------------------------------------

    @Test
    @Order(11)
    @DisplayName("11. GET /productos - Vista Web Catálogo")
    public void testVistaWebCatalogo() throws Exception {
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/list"))
                .andExpect(model().attributeExists("productos", "tiposProducto"));
    }

    @Test
    @Order(12)
    @DisplayName("12. GET /productos/nuevo - Vista Web Formulario Nuevo")
    public void testVistaWebNuevo() throws Exception {
        mockMvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attributeExists("productoForm", "tiposProducto", "proveedoresCatalogo"));
    }

    @Test
    @Order(13)
    @DisplayName("13. GET /productos/editar/{id} - Vista Web Formulario Edición dinámico")
    public void testVistaWebEditar() throws Exception {
        mockMvc.perform(get("/productos/editar/" + idProductoDinamico))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attributeExists("productoForm", "esEdicion"));
    }

    @Test
    @Order(14)
    @DisplayName("14. GET /productos/proveedores/{id} - Vista Web Dedicada Proveedores")
    public void testVistaWebProveedores() throws Exception {
        mockMvc.perform(get("/productos/proveedores/" + idProductoDinamico))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/proveedores"))
                .andExpect(model().attributeExists("productoForm", "proveedoresCatalogo"));
    }

    @Test
    @Order(15)
    @DisplayName("15. POST /productos/guardar - Procesamiento Web de Guardar Formulario con Redirección")
    public void testGuardarProductoWebForm() throws Exception {
        mockMvc.perform(post("/productos/guardar")
                        .param("idProducto", String.valueOf(idProductoDinamico))
                        .param("clave", claveProductoDinamico + "-WEB")
                        .param("nombre", "Producto Editado vía Web MVC")
                        .param("idTipoProducto", String.valueOf(idTipoProductoDinamico))
                        .param("precio", "2450.50")
                        .param("proveedores[0].idProveedor", String.valueOf(idProveedorDinamico))
                        .param("proveedores[0].claveProveedor", "SKU-WEB-PROV")
                        .param("proveedores[0].costo", "1800.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attributeExists("mensaje"));
    }

    // -------------------------------------------------------------------------
    // 5. BORRADO LÓGICO (SOFT DELETE)
    // -------------------------------------------------------------------------

    @Test
    @Order(16)
    @DisplayName("16. DELETE /api/productos/{id} - Eliminar producto dinámico (sp_eliminar_producto Soft Delete)")
    public void testEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/" + idProductoDinamico).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
