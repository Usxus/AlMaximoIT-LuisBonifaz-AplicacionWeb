package com.almaximoit.productos.controller.api;

import com.almaximoit.productos.dto.ProductoFormDTO;
import com.almaximoit.productos.model.Producto;
import com.almaximoit.productos.model.Proveedor;
import com.almaximoit.productos.model.TipoProducto;
import com.almaximoit.productos.service.ProductoService;
import com.almaximoit.productos.service.ProveedorService;
import com.almaximoit.productos.service.TipoProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Productos y Catálogos (API REST)", description = "Operaciones CRUD y catálogos ejecutados mediante Stored Procedures.")
public class ProductoRestController {

    private final ProductoService productoService;
    private final TipoProductoService tipoProductoService;
    private final ProveedorService proveedorService;

    public ProductoRestController(ProductoService productoService,
                                  TipoProductoService tipoProductoService,
                                  ProveedorService proveedorService) {
        this.productoService = productoService;
        this.tipoProductoService = tipoProductoService;
        this.proveedorService = proveedorService;
    }

    /**
     * Búsqueda y filtrado de productos activos.
     */
    @GetMapping("/productos")
    @Operation(summary = "Listar o buscar productos", description = "Ejecuta sp_buscar_productos en la BD con filtros opcionales de Clave y Tipo de Producto.")
    public ResponseEntity<List<Producto>> listarProductos(
            @Parameter(description = "Clave o nombre del producto a filtrar") @RequestParam(required = false) String clave,
            @Parameter(description = "ID del tipo de producto a filtrar") @RequestParam(required = false) Integer idTipoProducto) {
        return ResponseEntity.ok(productoService.buscarProductos(clave, idTipoProducto));
    }

    /**
     * Obtiene el detalle de un producto por su identificador primario.
     */
    @GetMapping("/productos/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Ejecuta sp_obtener_producto_por_id y sp_listar_proveedores_por_producto retornando el producto con sus proveedores asociados.")
    public ResponseEntity<ProductoFormDTO> obtenerProductoPorId(
            @Parameter(description = "ID del producto") @PathVariable Integer id) {
        return ResponseEntity.ok(productoService.obtenerProductoFormPorId(id));
    }

    /**
     * Crea o actualiza un producto junto con sus proveedores vinculados.
     */
    @PostMapping("/productos")
    @Operation(summary = "Crear o actualizar producto", description = "Ejecuta sp_insertar_producto / sp_actualizar_producto y sp_asignar_proveedor_producto.")
    public ResponseEntity<Map<String, Object>> guardarProducto(@Valid @RequestBody ProductoFormDTO form) {
        Integer idCreated = productoService.guardarProducto(form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("idProducto", idCreated);
        response.put("message", "Producto guardado correctamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Realiza la eliminación lógica (Soft Delete) de un producto.
     */
    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto (Soft Delete)", description = "Ejecuta sp_eliminar_producto para realizar el borrado lógico del producto.")
    public ResponseEntity<Map<String, Object>> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar") @PathVariable Integer id) {
        productoService.eliminarProducto(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Producto eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna el catálogo maestro de tipos de producto activos.
     */
    @GetMapping("/tipos-producto")
    @Operation(summary = "Catálogo de Tipos de Producto", description = "Ejecuta sp_listar_tipos_producto retornando las categorías activas.")
    public ResponseEntity<List<TipoProducto>> listarTiposProducto() {
        return ResponseEntity.ok(tipoProductoService.listarTiposProducto());
    }

    /**
     * Retorna el catálogo maestro de proveedores activos.
     */
    @GetMapping("/proveedores")
    @Operation(summary = "Catálogo de Proveedores", description = "Ejecuta sp_listar_proveedores retornando todos los proveedores registrados.")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }

    /**
     * Registra un nuevo proveedor de forma dinámica.
     */
    @PostMapping("/proveedores")
    @Operation(summary = "Crear nuevo proveedor", description = "Ejecuta sp_insertar_proveedor para registrar un proveedor.")
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody Proveedor proveedor) {
        Proveedor nuevo = proveedorService.crearProveedor(proveedor.getNombre(), proveedor.getDescripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}
