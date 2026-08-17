package com.almaximoit.productos.service;

import com.almaximoit.productos.dto.ProductoFormDTO;
import com.almaximoit.productos.model.Producto;

import java.util.List;

public interface ProductoService {

    /**
     * Realiza la búsqueda de productos según criterios de filtro.
     *
     * @param clave          Clave interna o nombre a buscar (opcional).
     * @param idTipoProducto Identificador de tipo de producto (opcional).
     * @return Lista de productos que cumplen los criterios.
     */
    List<Producto> buscarProductos(String clave, Integer idTipoProducto);

    /**
     * Recupera la estructura completa de un producto (incluyendo proveedores) para su edición.
     *
     * @param idProducto Identificador del producto.
     * @return DTO con los datos del producto y su lista de proveedores vinculados.
     */
    ProductoFormDTO obtenerProductoFormPorId(Integer idProducto);

    /**
     * Guarda de forma transaccional un producto (creación o actualización) y sincroniza sus proveedores.
     *
     * @param form DTO con los datos del formulario de producto.
     * @return Identificador del producto procesado.
     */
    Integer guardarProducto(ProductoFormDTO form);

    /**
     * Ejecuta la eliminación lógica (soft delete) de un producto.
     *
     * @param idProducto Identificador del producto a dar de baja.
     */
    void eliminarProducto(Integer idProducto);
}
