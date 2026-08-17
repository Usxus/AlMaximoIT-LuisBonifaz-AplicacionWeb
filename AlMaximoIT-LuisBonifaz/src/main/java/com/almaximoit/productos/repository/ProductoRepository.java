package com.almaximoit.productos.repository;

import com.almaximoit.productos.dto.ProductoProveedorDTO;
import com.almaximoit.productos.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository {

    /**
     * Consulta productos activos aplicando filtros por clave/nombre y categoría.
     *
     * @param clave          Criterio de búsqueda por clave interna o nombre (coincidencia parcial).
     * @param idTipoProducto Identificador de la categoría o null para todas.
     * @return Lista de productos coincidentes.
     */
    List<Producto> buscarProductos(String clave, Integer idTipoProducto);

    /**
     * Obtiene el detalle de un producto por su clave primaria.
     *
     * @param idProducto Identificador único del producto.
     * @return Instancia del producto o null si no existe o se encuentra inactivo.
     */
    Producto obtenerProductoPorId(Integer idProducto);

    /**
     * Ejecuta el Stored Procedure de inserción de producto y retorna el identificador autogenerado.
     *
     * @param clave          Clave interna única (SKU).
     * @param nombre         Nombre comercial del producto.
     * @param precio         Precio unitario de venta.
     * @param idTipoProducto Identificador de la categoría asociada.
     * @return ID del nuevo registro insertado.
     */
    Integer insertarProducto(String clave, String nombre, BigDecimal precio, Integer idTipoProducto);

    /**
     * Actualiza la información básica de un producto existente.
     *
     * @param idProducto     Identificador del producto a modificar.
     * @param clave          Nueva clave interna (SKU).
     * @param nombre         Nuevo nombre del producto.
     * @param precio         Nuevo precio unitario.
     * @param idTipoProducto Nuevo tipo de producto asociado.
     */
    void actualizarProducto(Integer idProducto, String clave, String nombre, BigDecimal precio, Integer idTipoProducto);

    /**
     * Aplica borrado lógico (soft delete) sobre el producto indicado.
     *
     * @param idProducto Identificador del producto a desactivar.
     */
    void eliminarProducto(Integer idProducto);

    /**
     * Recupera la lista de proveedores y costos asignados a un producto.
     *
     * @param idProducto Identificador del producto.
     * @return Lista de asociaciones de proveedores vinculados.
     */
    List<ProductoProveedorDTO> listarProveedoresPorProducto(Integer idProducto);

    /**
     * Asocia un proveedor y su costo correspondiente a un producto.
     *
     * @param idProducto     Identificador del producto.
     * @param idProveedor    Identificador del proveedor.
     * @param claveProveedor Clave asignada por el proveedor.
     * @param costo          Costo de adquisición otorgado por el proveedor.
     */
    void asignarProveedorProducto(Integer idProducto, Integer idProveedor, String claveProveedor, BigDecimal costo);

    /**
     * Elimina todas las relaciones de proveedores vinculadas a un producto.
     *
     * @param idProducto Identificador del producto.
     */
    void eliminarProveedoresProducto(Integer idProducto);
}
